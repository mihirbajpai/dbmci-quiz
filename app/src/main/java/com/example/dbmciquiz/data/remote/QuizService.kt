package com.example.dbmciquiz.data.remote

import retrofit2.http.GET

interface QuizService {
    @GET("dr-samrat/53846277a8fcb034e482906ccc0d12b2/raw")
    suspend fun fetchQuestions(): List<QuestionDto>
}
