package com.example.spaced.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        modifier = Modifier.padding(top = 24.dp),
        title = {
            Text(
                text = "Track",
                style = TopDateHeaderTextStyle
            )
        },
        navigationIcon = {
            Surface(
                modifier = Modifier
                    .padding(start = 12.dp, end = 8.dp)
                    .size(36.dp),
                shape = CircleShape,
                color = onPrimaryColor.copy(alpha = 0.15f)
            ) {
                IconButton(onClick = onCloseClick) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = onPrimaryColor
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = primaryBg,
            titleContentColor = onPrimaryColor
        )
    )
}