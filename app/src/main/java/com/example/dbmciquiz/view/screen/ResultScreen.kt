package com.example.dbmciquiz.view.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dbmciquiz.view.component.enterFrom
import com.example.dbmciquiz.view.theme.QuizOnSurface
import com.example.dbmciquiz.view.theme.QuizOnSurfaceMuted
import com.example.dbmciquiz.view.theme.QuizSurface

/** Delay between consecutive stat cards sliding in. */
private const val STAT_STAGGER_MS = 90

/** How long a stat's number takes to count up from zero. */
private const val COUNT_UP_MS = 800

@Composable
fun ResultScreen(
    correct: Int,
    total: Int,
    skipped: Int,
    longestStreak: Int,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Quiz Results",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = QuizOnSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Congratulations!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = QuizOnSurface,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "You've completed the quiz. Here's your performance summary:",
                color = QuizOnSurfaceMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    modifier = Modifier
                        .weight(1f)
                        .enterFrom(key = Unit, dy = 24.dp),
                    title = "Correct Answers",
                    value = correct,
                    suffix = "/$total"
                )
                StatCard(
                    modifier = Modifier
                        .weight(1f)
                        .enterFrom(key = Unit, dy = 24.dp, delayMillis = STAT_STAGGER_MS),
                    title = "Highest Streak",
                    value = longestStreak
                )
            }

            Spacer(Modifier.height(16.dp))

            StatCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .enterFrom(key = Unit, dy = 24.dp, delayMillis = STAT_STAGGER_MS * 2),
                title = "Skipped Questions",
                value = skipped
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onRestart,
                shape = CircleShape,
                modifier = Modifier.enterFrom(
                    key = Unit,
                    dy = 24.dp,
                    delayMillis = STAT_STAGGER_MS * 3
                )
            ) {
                Text(
                    text = "Restart Quiz",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, title: String, value: Int, suffix: String = "") {
    // Count the number up from zero once the card is composed.
    var launched by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { launched = true }
    val shown by animateIntAsState(
        targetValue = if (launched) value else 0,
        animationSpec = tween(COUNT_UP_MS, easing = FastOutSlowInEasing),
        label = "countUp"
    )
    Column(
        modifier = modifier
            .background(QuizSurface, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Text(
            text = title,
            color = QuizOnSurfaceMuted,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "$shown$suffix",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = QuizOnSurface
        )
    }
}
