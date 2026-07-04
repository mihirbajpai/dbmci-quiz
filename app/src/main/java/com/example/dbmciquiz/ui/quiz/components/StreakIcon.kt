package com.example.dbmciquiz.ui.quiz.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.dbmciquiz.R
import com.example.dbmciquiz.ui.quiz.QuizViewModel

/**
 * Single streak flame. Frozen/unlit below [QuizViewModel.STREAK_MILESTONE]; at or above it the
 * flame lights up and re-plays its activation each time [streak] increases.
 */
@Composable
fun StreakIcon(streak: Int) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.streak_activation)
    )
    val lit = streak >= QuizViewModel.STREAK_MILESTONE
    if (lit) {
        // key(streak) recreates the animation state, replaying it on each increment.
        key(streak) {
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = 1,
                isPlaying = true
            )
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(32.dp)
            )
        }
    } else {
        // Frozen on the first (unlit) frame.
        LottieAnimation(
            composition = composition,
            progress = { 0f },
            modifier = Modifier.size(32.dp)
        )
    }
}
