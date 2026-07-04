package com.example.dbmciquiz.data.repository

import com.example.dbmciquiz.data.model.Question
import com.example.dbmciquiz.data.remote.NetworkModule
import com.example.dbmciquiz.data.remote.QuizService
import com.example.dbmciquiz.data.remote.toDomain

/** Singleton data source for quiz questions, with an in-memory cache. */
object QuizRepository {
    private val service: QuizService = NetworkModule.quizService

    // Memoize on success: fetch once per process, but a failed first fetch still lets Retry work.
    private var questions: List<Question>? = null
    suspend fun fetchQuestions(): List<Question> =
        questions ?: service.fetchQuestions().map { it.toDomain() }.also { questions = it }
}
