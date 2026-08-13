package com.example.forged.domain.model

data class Exercise(
    val id: String,
    val name: String,
    val bodyParts: List<ExerciseBodyPart> = emptyList(),
)
