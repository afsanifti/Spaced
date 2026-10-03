package com.example.spaced.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.example.spaced.MainActivity
import com.example.spaced.ui.utils.PomodoroPhase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max

class PomodoroService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)
    private var timerJob: Job? = null

    private var targetEndRealtimeMillis: Long = 0L

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val totalSecs = intent.getIntExtra(EXTRA_TOTAL_SECONDS, _pomodoroState.value.totalSeconds)
                val phase = intent.getStringExtra(EXTRA_PHASE)?.let { PomodoroPhase.valueOf(it) } ?: _pomodoroState.value.phase
                val title = intent.getStringExtra(EXTRA_TITLE) ?: _pomodoroState.value.title
                val stepIndex = intent.getIntExtra(EXTRA_STEP_INDEX, _pomodoroState.value.currentStepIndex)
                startTimer(totalSecs, phase, title, stepIndex)
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_STOP -> stopTimer()
        }
        return START_STICKY
    }

    private fun startTimer(durationSeconds: Int, phase: PomodoroPhase, title: String, stepIndex: Int) {
        targetEndRealtimeMillis = SystemClock.elapsedRealtime() + (durationSeconds * 1000L)

        _pomodoroState.value = PomodoroState(
            phase = phase,
            remainingSeconds = durationSeconds,
            totalSeconds = durationSeconds,
            progress = 1f,
            isRunning = true,
            isSessionActive = true,
            currentStepIndex = stepIndex,
            title = title
        )

        startForegroundWithNotification()
        runTimerLoop()
    }

    private fun resumeTimer() {
        val remaining = _pomodoroState.value.remainingSeconds
        targetEndRealtimeMillis = SystemClock.elapsedRealtime() + (remaining * 1000L)
        _pomodoroState.value = _pomodoroState.value.copy(isRunning = true, isSessionActive = true)

        startForegroundWithNotification()
        runTimerLoop()
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _pomodoroState.value = _pomodoroState.value.copy(isRunning = false)
        updateNotification()
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _pomodoroState.value = _pomodoroState.value.copy(
            isRunning = false,
            isSessionActive = false,
            currentStepIndex = 0,
            remainingSeconds = _pomodoroState.value.totalSeconds,
            progress = 1f
        )
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun runTimerLoop() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isActive) {
                val now = SystemClock.elapsedRealtime()
                val remainingMillis = max(0L, targetEndRealtimeMillis - now)
                val remainingSecs = (remainingMillis / 1000L).toInt()
                val totalSecs = _pomodoroState.value.totalSeconds
                val currentProgress = if (totalSecs > 0) remainingMillis.toFloat() / (totalSecs * 1000L) else 0f

                _pomodoroState.value = _pomodoroState.value.copy(
                    remainingSeconds = remainingSecs,
                    progress = currentProgress
                )

                updateNotification()

                if (remainingMillis <= 0L) {
                    onTimerFinished()
                    break
                }
                delay(500L)
            }
        }
    }

    private fun onTimerFinished() {
        _pomodoroState.value = _pomodoroState.value.copy(
            isRunning = false,
            remainingSeconds = 0,
            progress = 0f
        )
        stopForeground(STOP_FOREGROUND_DETACH)
    }

    private fun startForegroundWithNotification() {
        try {
            val notification = buildNotification()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                } else {
                    0
                }
                startForeground(NOTIFICATION_ID, notification, serviceType)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun buildNotification(): android.app.Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val state = _pomodoroState.value
        val mins = state.remainingSeconds / 60
        val secs = state.remainingSeconds % 60
        val timeString = String.format("%02d:%02d", mins, secs)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("${state.title} (${state.phase.name})")
            .setContentText("Time remaining: $timeString")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun updateNotification() {
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, buildNotification())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pomodoro Timer Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        serviceJob.cancel()
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "pomodoro_timer_channel"
        private const val NOTIFICATION_ID = 1001

        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"

        const val EXTRA_TOTAL_SECONDS = "EXTRA_TOTAL_SECONDS"
        const val EXTRA_PHASE = "EXTRA_PHASE"
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val EXTRA_STEP_INDEX = "EXTRA_STEP_INDEX"

        private val _pomodoroState = MutableStateFlow(PomodoroState())
        val pomodoroState: StateFlow<PomodoroState> = _pomodoroState.asStateFlow()
    }
}