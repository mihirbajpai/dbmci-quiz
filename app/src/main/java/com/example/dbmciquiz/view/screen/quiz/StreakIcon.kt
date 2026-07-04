package com.example.dbmciquiz.view.screen.quiz

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dbmciquiz.R
import com.example.dbmciquiz.view.component.LottiePlayer

/**
 * A single streak flame. Unlit below [QuizViewModel.STREAK_MILESTONE]; at or above it lights up
 * and replays each time [streak] goes up.
 */
@Composable
fun StreakIcon(streak: Int) {
    if (streak >= QuizViewModel.STREAK_MILESTONE) {
        // key(streak) restarts the animation each time it goes up.
        key(streak) {
            LottiePlayer(res = R.raw.streak_activation, modifier = Modifier.size(32.dp))
        }
    } else {
        LottiePlayer(
            res = R.raw.streak_activation,
            modifier = Modifier.size(32.dp),
            isPlaying = false
        )
    }
}
