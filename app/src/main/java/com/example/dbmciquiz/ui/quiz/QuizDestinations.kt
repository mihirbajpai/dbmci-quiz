package com.example.dbmciquiz.ui.quiz

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
