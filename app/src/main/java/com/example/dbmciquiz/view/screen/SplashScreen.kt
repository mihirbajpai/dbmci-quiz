package com.example.dbmciquiz.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieConstants
import com.example.dbmciquiz.R
import com.example.dbmciquiz.view.component.LottiePlayer
import com.example.dbmciquiz.view.theme.QuizOnSurface
import com.example.dbmciquiz.view.theme.QuizOnSurfaceMuted
import com.example.dbmciquiz.view.theme.Spacing

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.xLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottiePlayer(
            res = R.raw.splash_questions,
            modifier = Modifier.size(220.dp),
            iterations = LottieConstants.IterateForever
        )
        Spacer(Modifier.height(Spacing.medium))
        Text(
            text = "DBMCI Quiz",
            style = MaterialTheme.typography.titleLarge,
            color = QuizOnSurface
        )
        Spacer(Modifier.height(Spacing.xSmall))
        Text(
            text = "Loading your questions…",
            color = QuizOnSurfaceMuted
        )
    }
}
