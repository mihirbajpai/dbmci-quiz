package com.example.dbmciquiz.data.repository

import com.example.dbmciquiz.data.model.Question
import com.example.dbmciquiz.data.remote.NetworkModule
import com.example.dbmciquiz.data.remote.QuizService
import com.example.dbmciquiz.data.remote.toDomain

object QuizRepository {
    private val service: QuizService = NetworkModule.quizService

    // Cache data so that no need to fetch on each initialization
    private var questions: List<Question>? = null
    suspend fun fetchQuestions(): List<Question> =
        questions ?: service.fetchQuestions().map { it.toDomain() }.also { questions = it }
}
