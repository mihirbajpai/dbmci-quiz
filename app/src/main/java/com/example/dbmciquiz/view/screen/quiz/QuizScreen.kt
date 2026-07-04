package com.example.dbmciquiz.view.screen.quiz

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dbmciquiz.R
import com.example.dbmciquiz.view.DataState
import com.example.dbmciquiz.view.Screen
import com.example.dbmciquiz.view.component.LottiePlayer
import com.example.dbmciquiz.view.component.SwipeToSkipCard
import com.example.dbmciquiz.view.component.enterFrom
import com.example.dbmciquiz.view.screen.ErrorScreen
import com.example.dbmciquiz.view.screen.SplashScreen
import com.example.dbmciquiz.view.theme.Spacing

/** Delay between consecutive option pills sliding in when a new question appears. */
private const val OPTION_STAGGER_MS = 55

@Composable
fun QuizQuestionScreen(vm: QuizViewModel = viewModel(), navigateTo: (route: String) -> Unit) {
    val questionsState by vm.questionsState.collectAsStateWithLifecycle()
    if (questionsState is DataState.Loading) {
        SplashScreen()
        return
    } else if (questionsState is DataState.Failure) {
        ErrorScreen(
            message = (questionsState as DataState.Failure).error.message,
            onRetry = vm::fetchQuestions
        )
        return
    }
    val questions = (questionsState as DataState.Success).value
    val currentIndex by vm.currentIndex.collectAsStateWithLifecycle()
    val selectedOptionIndex by vm.selectedOptionIndex.collectAsStateWithLifecycle()
    val streak by vm.streak.collectAsStateWithLifecycle()

    // currentIndex passing the last question means the quiz is finished. Navigation is a side
    // effect, so it runs from a LaunchedEffect — never directly during composition.
    val finished = currentIndex >= questions.size
    LaunchedEffect(finished) {
        if (finished.not()) return@LaunchedEffect
        navigateTo(
            Screen.RESULT.withArgs(
                vm.correctCount,
                questions.size,
                vm.skippedCount,
                vm.longestStreak
            )
        )
    }
    // The effect fires after this composition, so the finished frame still runs — nothing to draw.
    if (finished) return

    val currentQuestion = questions[currentIndex]
    val questionCount = questions.size
    val answered = selectedOptionIndex != null

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.large, vertical = Spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quiz",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "$streak",
                    style = MaterialTheme.typography.titleMedium,
                    // secondary is the streak-flame colour once the milestone is hit.
                    color = if (streak >= QuizViewModel.STREAK_MILESTONE) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Spacer(Modifier.width(Spacing.xSmall))
                StreakIcon(streak = streak)
            }
            Spacer(Modifier.height(Spacing.medium))
            Text(
                text = "Question ${currentIndex + 1} of $questionCount",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Spacing.small))
            QuizProgressBar(current = currentIndex + 1, total = questionCount)
            // The question is a page: swipe it right-to-left to skip/advance. It fills the space
            // between the header and the button so there is room to drag and reveal the indicator.
            SwipeToSkipCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = if (selectedOptionIndex == null) "Skip" else "Next",
                onSwiped = vm::onSkipOrNext
            ) {
                Column(modifier = Modifier.padding(horizontal = Spacing.medium, vertical = 28.dp)) {
                    Text(
                        text = currentQuestion.text,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        // Slide in from the right each time the question changes.
                        modifier = Modifier
                            .fillMaxWidth()
                            .enterFrom(key = currentIndex, dx = 40.dp)
                    )
                    Spacer(Modifier.height(Spacing.xLarge))
                    currentQuestion.options.forEachIndexed { index, option ->
                        val state = when {
                            answered.not() -> OptionState.DEFAULT
                            index == currentQuestion.correctIndex -> OptionState.CORRECT
                            index == selectedOptionIndex -> OptionState.WRONG
                            else -> OptionState.DEFAULT
                        }
                        key(index) {
                            OptionPill(
                                // Stagger the options up as the question appears.
                                modifier = Modifier
                                    .enterFrom(
                                        key = currentIndex,
                                        dy = 16.dp,
                                        delayMillis = index * OPTION_STAGGER_MS
                                    )
                                    .padding(vertical = Spacing.xSmall),
                                text = option,
                                state = state,
                                enabled = answered.not()
                            ) { vm.selectOption(index) }
                        }
                    }
                }
            }
            val autoAdvancing by vm.autoAdvancing.collectAsStateWithLifecycle()
            // Fixed-height slot so swapping the shorter button for the taller auto-advance bar
            // doesn't resize the card above and shift it.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 68.dp),
                contentAlignment = Alignment.Center
            ) {
                if (autoAdvancing) {
                    AutoAdvanceBar(
                        durationMs = QuizViewModel.AUTO_ADVANCE_MS,
                        onCancel = vm::cancelAutoAdvance
                    )
                } else {
                    SkipNextButton(
                        label = if (selectedOptionIndex == null) "Skip" else "Next",
                        onClick = vm::onSkipOrNext
                    )
                }
            }
        }

        val showCelebration by vm.showCelebration.collectAsStateWithLifecycle()
        val context = LocalContext.current
        LaunchedEffect(showCelebration) {
            if (showCelebration) {
                Toast.makeText(context, "🔥 New max streak record!", Toast.LENGTH_SHORT).show()
            }
        }
        if (showCelebration) {
            LottiePlayer(res = R.raw.celebration, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun QuizProgressBar(current: Int, total: Int) {
    val target = if (total == 0) 0f else current.toFloat() / total
    val animatedProgress by animateFloatAsState(targetValue = target, label = "progress")
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .height(Spacing.small)
            .clip(CircleShape),
        progress = { animatedProgress },
        color = MaterialTheme.colorScheme.onSurface,
        trackColor = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
private fun SkipNextButton(label: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text = label, modifier = Modifier.padding(vertical = Spacing.small))
    }
}
