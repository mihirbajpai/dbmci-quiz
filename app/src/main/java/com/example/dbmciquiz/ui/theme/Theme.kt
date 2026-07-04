package com.example.dbmciquiz.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Fixed dark scheme so the app always matches the mockup (no dynamic/Material You override).
private val QuizColorScheme = darkColorScheme(
    primary = QuizAccent,
    onPrimary = QuizOnAccent,
    background = QuizBackground,
    onBackground = QuizOnSurface,
    surface = QuizSurface,
    onSurface = QuizOnSurface,
    surfaceVariant = QuizSurfaceHigh,
    onSurfaceVariant = QuizOnSurfaceMuted,
    error = QuizWrong
)

@Composable
fun DBMCIQuizTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = QuizColorScheme,
        typography = Typography,
        content = content
    )
}
