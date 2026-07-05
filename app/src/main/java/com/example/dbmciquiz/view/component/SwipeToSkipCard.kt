package com.example.dbmciquiz.view.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.dbmciquiz.view.theme.Spacing
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** How far you must drag (fraction of card width) to commit the swipe. */
private const val SWIPE_COMMIT_FRACTION = 0.35f

/** How far the card flings off-screen on commit, in card widths. */
private const val SWIPE_OFF_FRACTION = 1.3f
private const val MAX_TILT_DEGREES = 8f
private const val FLING_DURATION_MS = 250

/**
 * On left swipe it fire [onSwiped], revealing a "[label]" hint behind it. Past
 * [SWIPE_COMMIT_FRACTION] of the width it commits, otherwise it springs back. Right-to-left only.
 */
@Composable
fun SwipeToSkipCard(
    label: String,
    onSwiped: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var width by remember { mutableIntStateOf(0) }
    val progress = if (width == 0) 0f else (-offsetX.value / width).coerceIn(0f, 1f)

    Box(modifier = modifier.onSizeChanged { width = it.width }) {
        // The skip hint sits behind the page, fading in as it slides.
        SkipIndicator(
            label = label,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .alpha(progress)
                .padding(end = Spacing.large)
        )
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .graphicsLayer {
                    rotationZ = if (width == 0) 0f else offsetX.value / width * MAX_TILT_DEGREES
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            // Clamp to <= 0: only right-to-left drags move the page.
                            scope.launch {
                                offsetX.snapTo((offsetX.value + dragAmount).coerceAtMost(0f))
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                // Past the threshold: fling off-screen, advance, then snap back to
                                // centre for the next question. Otherwise, spring back to rest.
                                if (-offsetX.value > width * SWIPE_COMMIT_FRACTION) {
                                    offsetX.animateTo(
                                        -width * SWIPE_OFF_FRACTION,
                                        tween(FLING_DURATION_MS)
                                    )
                                    onSwiped()
                                    offsetX.snapTo(0f)
                                } else {
                                    offsetX.animateTo(0f, spring())
                                }
                            }
                        }
                    )
                },
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 12.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            content()
        }
    }
}

/** The "» [label]" hint revealed behind the page as it slides away. */
@Composable
private fun SkipIndicator(label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "»",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
