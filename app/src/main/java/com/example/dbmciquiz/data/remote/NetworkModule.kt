package com.example.dbmciquiz.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object NetworkModule {
    private const val BASE_URL = "https://gist.githubusercontent.com/"
    private val json = Json { ignoreUnknownKeys = true }

    val quizService: QuizService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(QuizService::class.java)
    }
}
