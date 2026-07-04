package com.example.dbmciquiz.presentation.screen.quiz

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dbmciquiz.presentation.theme.QuizOnSurface
import com.example.dbmciquiz.presentation.theme.QuizSurfaceHigh
import com.example.dbmciquiz.presentation.theme.QuizTrack

/**
 * Bottom bar shown after answering: a sliding progress bar counts down [durationMs] before
 * the quiz auto-advances. Tapping Cancel stops the countdown (the caller then swaps Skip -> Next).
 */
@Composable
fun AutoAdvanceBar(durationMs: Long, onCancel: () -> Unit) {
    // Drive the sliding bar locally so it exactly mirrors the countdown window.
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMs.toInt(), easing = LinearEasing)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(QuizSurfaceHigh, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Moving to next question…",
                color = QuizOnSurface,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium
            )
            // Plain clickable text (not a TextButton, whose min-height would unbalance the padding).
            Text(
                text = "Cancel",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onCancel)
                    .padding(horizontal = 8.dp)
            )
        }
        LinearProgressIndicator(
            progress = { progress.value },
            color = MaterialTheme.colorScheme.primary,
            trackColor = QuizTrack,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        )
    }
}
