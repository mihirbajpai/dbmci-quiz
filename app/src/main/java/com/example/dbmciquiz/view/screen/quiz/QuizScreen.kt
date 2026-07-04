package com.example.dbmciquiz.view.screen.quiz

import com.example.dbmciquiz.view.Screen
import com.example.dbmciquiz.view.screen.SplashScreen
import com.example.dbmciquiz.view.screen.ErrorScreen
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.dbmciquiz.R
import com.example.dbmciquiz.view.DataState
import com.example.dbmciquiz.view.theme.QuizFlameLit
import com.example.dbmciquiz.view.theme.QuizOnSurface
import com.example.dbmciquiz.view.theme.QuizOnSurfaceMuted
import com.example.dbmciquiz.view.theme.QuizSurfaceHigh
import com.example.dbmciquiz.view.theme.QuizTrack

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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quiz",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = QuizOnSurface
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "$streak",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (streak >= QuizViewModel.STREAK_MILESTONE) QuizFlameLit else QuizOnSurface
                )
                Spacer(Modifier.width(4.dp))
                StreakIcon(streak = streak)
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Question ${currentIndex + 1} of $questionCount",
                color = QuizOnSurfaceMuted,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            QuizProgressBar(current = currentIndex + 1, total = questionCount)
            Spacer(Modifier.height(28.dp))

            Text(
                text = currentQuestion.text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = QuizOnSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            currentQuestion.options.forEachIndexed { index, option ->
                val state = when {
                    answered.not() -> OptionState.DEFAULT
                    index == currentQuestion.correctIndex -> OptionState.CORRECT
                    index == selectedOptionIndex -> OptionState.WRONG
                    else -> OptionState.DEFAULT
                }
                OptionPill(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = option,
                    state = state,
                    enabled = answered.not()
                ) { vm.selectOption(index) }
            }

            Spacer(Modifier.weight(1f))
            val autoAdvancing by vm.autoAdvancing.collectAsStateWithLifecycle()
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

        val showCelebration by vm.showCelebration.collectAsStateWithLifecycle()
        val context = LocalContext.current
        LaunchedEffect(showCelebration) {
            if (showCelebration) {
                Toast.makeText(context, "🔥 New max streak record!", Toast.LENGTH_SHORT).show()
            }
        }
        if (showCelebration) CelebrationOverlay()
    }
}

@Composable
private fun QuizProgressBar(current: Int, total: Int) {
    val target = if (total == 0) 0f else current.toFloat() / total
    val animatedProgress by animateFloatAsState(targetValue = target, label = "progress")
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape),
        progress = { animatedProgress },
        color = QuizOnSurface,
        trackColor = QuizTrack
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
            containerColor = QuizSurfaceHigh,
            contentColor = QuizOnSurface
        )
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

/** Full-screen celebration Lottie (plays once); the caller controls how long it stays visible. */
@Composable
private fun CelebrationOverlay() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.celebration)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        isPlaying = true
    )
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize()
        )
    }
}
