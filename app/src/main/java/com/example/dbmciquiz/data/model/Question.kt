package com.example.dbmciquiz.data.model

data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
)
