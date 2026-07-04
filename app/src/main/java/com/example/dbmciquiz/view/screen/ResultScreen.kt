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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dbmciquiz.view.component.enterFrom
import com.example.dbmciquiz.view.theme.Spacing

/** Delay between each stat card sliding in. */
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
            .padding(Spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Quiz Results",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
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
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Spacing.small))
            Text(
                text = "You've completed the quiz. Here's your performance summary:",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Spacing.medium)
            )
            Spacer(Modifier.height(Spacing.xLarge))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
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

            Spacer(Modifier.height(Spacing.medium))

            StatCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .enterFrom(key = Unit, dy = 24.dp, delayMillis = STAT_STAGGER_MS * 2),
                title = "Skipped Questions",
                value = skipped
            )

            Spacer(Modifier.height(Spacing.xLarge))

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
                    modifier = Modifier.padding(vertical = Spacing.xSmall)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: Int,
    suffix: String = ""
) {
    // Count up from zero once the card first shows.
    var launched by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { launched = true }
    val shown by animateIntAsState(
        targetValue = if (launched) value else 0,
        animationSpec = tween(COUNT_UP_MS, easing = FastOutSlowInEasing),
        label = "countUp"
    )
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(Spacing.medium)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(Spacing.small))
        Text(
            text = "$shown$suffix",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
