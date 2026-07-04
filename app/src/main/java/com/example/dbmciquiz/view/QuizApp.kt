package com.example.dbmciquiz.view

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dbmciquiz.view.screen.ResultScreen
import com.example.dbmciquiz.view.screen.quiz.QuizQuestionScreen

/**
 * Root of the quiz experience: Quiz ⇄ Result navigation on the app background.
 *
 * The [QuizViewModel] is scoped to the Quiz destination's back-stack entry (obtained inside
 * [QuizQuestionScreen]), so navigating to a fresh Quiz entry on Restart gives a brand-new
 * ViewModel — no explicit reset needed. Result data travels as route arguments, so the Result
 * screen never touches the ViewModel.
 */
@Composable
fun QuizApp() {
    val navController = rememberNavController()
    // Result route pattern: base route + typed arg placeholders.
    val resultRoute = "${Screen.RESULT.route}/{correct}/{total}/{skipped}/{longest_streak}"
    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
        navController = navController,
        startDestination = Screen.QUIZ.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable(Screen.QUIZ.route) {
            QuizQuestionScreen {
                navController.navigate(it) {
                    popUpTo(0)
                }
            }
        }

        composable(
            route = resultRoute,
            arguments = listOf(
                navArgument("correct") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
                navArgument("skipped") { type = NavType.IntType },
                navArgument("longest_streak") { type = NavType.IntType }
            )
        ) { entry ->
            val args = requireNotNull(entry.arguments)
            ResultScreen(
                correct = args.getInt("correct"),
                total = args.getInt("total"),
                skipped = args.getInt("skipped"),
                longestStreak = args.getInt("longest_streak")
            ) {
                navController.navigate(Screen.QUIZ.route) {
                    popUpTo(0)
                }
            }
        }
    }
}

/**
 * Navigation destinations. Each carries its base [route]; [withArgs] appends positional
 * arguments (used by the Result destination to pass its score data).
 */
enum class Screen(val route: String) {
    QUIZ("quiz"),
    RESULT("result");

    fun withArgs(vararg args: Any): String {
        return "$route/${args.joinToString("/")}"
    }
}
