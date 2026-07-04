package com.example.dbmciquiz.view.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private const val ENTER_DURATION_MS = 350

/**
 * Fades [this] in while sliding it from a ([dx], [dy]) offset to its resting position — replayed
 * whenever [key] changes and on first composition. [delayMillis] staggers grouped items (e.g. the
 * options of a question). Values are applied in the draw phase, so the animation never recomposes.
 */
@Composable
fun Modifier.enterFrom(key: Any?, dx: Dp = 0.dp, dy: Dp = 0.dp, delayMillis: Int = 0): Modifier {
    // A fresh Animatable per [key] starts the item hidden (0) before it animates in (1).
    val progress = remember(key) { Animatable(0f) }
    LaunchedEffect(key) {
        if (delayMillis > 0) delay(delayMillis.toLong().milliseconds)
        progress.animateTo(1f, tween(ENTER_DURATION_MS, easing = FastOutSlowInEasing))
    }
    return this.graphicsLayer {
        val p = progress.value
        alpha = p
        translationX = (1f - p) * dx.toPx()
        translationY = (1f - p) * dy.toPx()
    }
}
