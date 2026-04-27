package com.mototrack.wit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Dark = darkColorScheme(
    primary = Color(0xFFFF6B35),
    secondary = Color(0xFF00B4D8),
    background = Color(0xFF0B0F14),
    surface = Color(0xFF111821),
)
private val Light = lightColorScheme(
    primary = Color(0xFFFF6B35),
    secondary = Color(0xFF0077B6),
)

@Composable
fun MotoTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}
