package com.example.dbmciquiz.data.remote

import com.example.dbmciquiz.data.model.Question
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionDto(
    @SerialName("id") val id: Int,
    @SerialName("question") val question: String,
    @SerialName("options") val options: List<String>,
    @SerialName("correctOptionIndex") val correctOptionIndex: Int,
)

fun QuestionDto.toDomain(): Question = Question(
    text = question,
    options = options,
    correctIndex = correctOptionIndex,
)
