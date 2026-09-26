package com.example.spaced.ui.components.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun ReviewTopBar(
    onBackClick: () -> Unit,
    primaryBg: Color,
    onPrimaryColor: Color,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.padding(top = 8.dp),
        title = {
            Text(
                text = "Review",
                style = TopDateHeaderTextStyle
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = onPrimaryColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = primaryBg,
            titleContentColor = onPrimaryColor
        )
    )
}