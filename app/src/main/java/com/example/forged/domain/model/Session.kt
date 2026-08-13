package com.example.forged.domain.model

import java.time.Instant
import java.time.LocalDate

data class Session(
    val id: String,
    val date: LocalDate,
    val startTime: Instant?,
    val endTime: Instant?,
    val durationMinutes: Int,
    val notes: String?,
    val exercises: List<ExerciseEntry>,
)
