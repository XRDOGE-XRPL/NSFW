package com.xrdoge.nsfw.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val Scheme = darkColorScheme(
    primary = NeonPink,
    onPrimary = Night,
    secondary = NeonPurple,
    onSecondary = Night,
    background = Night,
    onBackground = androidx.compose.ui.graphics.Color.White,
    surface = Elevated,
    onSurface = androidx.compose.ui.graphics.Color.White,
    surfaceVariant = CardStroke,
    onSurfaceVariant = Mist,
    error = Danger,
    outline = CardStroke,
)

@Composable
fun NsfwTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Scheme,
        typography = NsfwTypography,
        content = content,
    )
}
