package com.example.forged.domain.model

data class ExerciseEntry(
    val id: String,
    val exercise: Exercise,
    val orderIndex: Int,
    val durationMinutes: Int?,
    val sets: List<SetEntry>,
)
