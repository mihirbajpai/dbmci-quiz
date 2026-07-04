package com.example.dbmciquiz.view.theme

import androidx.compose.ui.graphics.Color

// Raw palette values. Screens never use these directly — they read MaterialTheme.colorScheme
// (via its Material slots and the semantic aliases in Theme.kt), which resolves to the dark or
// light scheme with the system theme.

// ---- Dark (mockup default) ----
val DarkBackground = Color(0xFF0D1117)
val DarkSurface = Color(0xFF1B222C)        // option pill / card surface
val DarkSurfaceHigh = Color(0xFF232B36)    // raised surface (buttons, cards)
val DarkOnSurface = Color(0xFFE6EAF0)      // primary text
val DarkOnSurfaceMuted = Color(0xFF9BA3AF) // secondary text
val DarkTrack = Color(0xFF2A323D)          // progress track / border
val DarkCorrect = Color(0xFF3FA85A)        // correct answer
val DarkWrong = Color(0xFF8E2C2C)          // wrong answer
val DarkFlameLit = Color(0xFFEF4E2B)       // lit streak count / flame
val DarkAccent = Color(0xFFB9D8FF)         // primary accent
val DarkOnAccent = Color(0xFF0D1117)       // text on accent

// ---- Light ----
// bg < surface < surfaceHigh in lightness, mirroring the dark set so elevation reads the same
// (raised cards lighter, inset pills darker). Answered pills are soft tints so dark text stays legible.
val LightBackground = Color(0xFFE8ECF2)
val LightSurface = Color(0xFFF3F5F9)
val LightSurfaceHigh = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF141A22)
val LightOnSurfaceMuted = Color(0xFF5C6673)
val LightTrack = Color(0xFFD6DCE5)
val LightCorrect = Color(0xFF9BD9AE)
val LightWrong = Color(0xFFE7ABAB)
val LightFlameLit = Color(0xFFDA3C1C)
val LightAccent = Color(0xFF2D6CDF)
val LightOnAccent = Color(0xFFFFFFFF)
