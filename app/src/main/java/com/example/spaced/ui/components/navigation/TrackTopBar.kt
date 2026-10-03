package com.example.spaced.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.spaced.ui.theme.TopDateHeaderTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackTopBar(
    onCloseClick: () -> Unit,
    primaryBg: Color,
    onPrimaryColor: Color
) {
    TopAppBar(
        title = {
            Text(
                text = "Track",
                style = TopDateHeaderTextStyle,
                color = onPrimaryColor
            )
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(onPrimaryColor.copy(alpha = 0.15f))
                    .clickable(
                        role = Role.Button,
                        onClick = onCloseClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = onPrimaryColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = primaryBg,
            titleContentColor = onPrimaryColor
        )
    )
}