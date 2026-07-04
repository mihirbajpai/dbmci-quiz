package com.example.dbmciquiz.view.screen.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dbmciquiz.view.theme.QuizCorrect
import com.example.dbmciquiz.view.theme.QuizOnSurface
import com.example.dbmciquiz.view.theme.QuizSurface
import com.example.dbmciquiz.view.theme.QuizWrong
import com.example.dbmciquiz.view.theme.Spacing

/** Visual state of an answer option once (or before) the question is answered. */
enum class OptionState { DEFAULT, CORRECT, WRONG }

@Composable
fun OptionPill(
    modifier: Modifier,
    text: String,
    state: OptionState,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val targetColor = when (state) {
        OptionState.DEFAULT -> QuizSurface
        OptionState.CORRECT -> QuizCorrect
        OptionState.WRONG -> QuizWrong
    }
    // Smoothly reveal the correct/incorrect colour when answered.
    val background by animateColorAsState(targetColor, label = "optionColor")
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(background)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = Spacing.medium, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = QuizOnSurface,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
