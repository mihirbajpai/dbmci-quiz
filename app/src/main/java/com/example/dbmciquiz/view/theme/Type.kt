package com.example.dbmciquiz.view.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight

private val Default = Typography()

/**
 * Type scale with the app's weights baked in (bold display/headline/title, semi-bold button
 * labels), so screens use the style alone instead of repeating `fontWeight`.
 */
val Typography = Typography(
    displayMedium = Default.displayMedium.copy(fontWeight = FontWeight.Bold),
    headlineMedium = Default.headlineMedium.copy(fontWeight = FontWeight.Bold),
    headlineSmall = Default.headlineSmall.copy(fontWeight = FontWeight.Bold),
    titleLarge = Default.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = Default.titleMedium.copy(fontWeight = FontWeight.Bold),
    labelLarge = Default.labelLarge.copy(fontWeight = FontWeight.SemiBold)
)
