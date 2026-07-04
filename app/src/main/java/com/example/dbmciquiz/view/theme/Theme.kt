package com.example.dbmciquiz.view.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Everything reads MaterialTheme.colorScheme, which swaps light/dark on its own.
private val DarkColors = darkColorScheme(
    primary = DarkAccent,
    onPrimary = DarkOnAccent,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,                   // option pills / stat cards
    onSurface = DarkOnSurface,
    surfaceContainerHigh = DarkSurfaceHigh,  // raised card / bar
    onSurfaceVariant = DarkOnSurfaceMuted,   // muted text
    outlineVariant = DarkTrack,              // progress track / border
    tertiary = DarkCorrect,                  // correct answer
    error = DarkWrong,                       // wrong answer
    secondary = DarkFlameLit                 // lit streak flame
)

private val LightColors = lightColorScheme(
    primary = LightAccent,
    onPrimary = LightOnAccent,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceContainerHigh = LightSurfaceHigh,
    onSurfaceVariant = LightOnSurfaceMuted,
    outlineVariant = LightTrack,
    tertiary = LightCorrect,
    error = LightWrong,
    secondary = LightFlameLit
)

@Composable
fun DBMCIQuizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
