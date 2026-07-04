package com.example.dbmciquiz.data.remote

import com.example.dbmciquiz.data.model.Question
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network representation of a question, mapped 1:1 from the remote JSON via [SerialName].
 * Kept separate from the domain [Question] so wire changes don't leak into the UI.
 */
@Serializable
data class QuestionDto(
    @SerialName("id") val id: Int,
    @SerialName("question") val question: String,
    @SerialName("options") val options: List<String>,
    @SerialName("correctOptionIndex") val correctOptionIndex: Int,
)

/** Maps the network DTO to the domain model. */
fun QuestionDto.toDomain(): Question = Question(
    id = id,
    text = question,
    options = options,
    correctIndex = correctOptionIndex,
)
