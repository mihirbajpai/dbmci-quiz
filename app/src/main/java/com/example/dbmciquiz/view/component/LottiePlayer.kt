package com.example.dbmciquiz.view.component

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

/** Plays a raw-res Lottie; with [isPlaying] false it just holds the first frame. */
@Composable
fun LottiePlayer(
    @RawRes res: Int,
    modifier: Modifier = Modifier,
    iterations: Int = 1,
    isPlaying: Boolean = true
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(res))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        isPlaying = isPlaying
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}
