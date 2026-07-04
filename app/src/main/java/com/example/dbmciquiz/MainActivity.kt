package com.example.dbmciquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dbmciquiz.ui.quiz.QuizApp
import com.example.dbmciquiz.ui.theme.DBMCIQuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DBMCIQuizTheme {
                QuizApp()
            }
        }
    }
}
