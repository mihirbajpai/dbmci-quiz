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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.dbmciquiz.view.theme.QuizSurfaceHigh
import com.example.dbmciquiz.view.theme.QuizTrack
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Fraction of the card's width the page must be dragged past for the swipe to commit. */
private const val SWIPE_COMMIT_FRACTION = 0.35f

/** How far the page flings off-screen when a swipe completes, as a multiple of the card width. */
private const val SWIPE_OFF_FRACTION = 1.3f

/** Maximum tilt (degrees) the page leans to when dragged a full width. */
private const val MAX_TILT_DEGREES = 8f

/** Duration (ms) of the fling-off animation once a swipe commits. */
private const val FLING_DURATION_MS = 250

/**
 * Wraps [content] in a Tinder-style page that can be swiped **left** (right-to-left) to trigger
 * [onSwiped]. As the page slides away it reveals a "[label]" indicator behind it. Past
 * [SWIPE_COMMIT_FRACTION] of the width the swipe completes; otherwise it springs back. Rightward
 * drags are ignored.
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
        // Behind the page: the skip indicator, fading in as the page slides left.
        SkipIndicator(
            label = label,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .alpha(progress)
                .padding(end = 24.dp)
        )
        // The swipeable page — a floating card that tilts around its own center as it slides.
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
                            // Clamp to <= 0 so only right-to-left drags move the page.
                            scope.launch {
                                offsetX.snapTo(
                                    (offsetX.value + dragAmount).coerceAtMost(0f)
                                )
                            }
                        },
                        onDragEnd = {
                            scope.launch {
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
            color = QuizSurfaceHigh,
            shadowElevation = 12.dp,
            border = BorderStroke(1.dp, QuizTrack)
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
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
