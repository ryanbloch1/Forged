package com.example.forged.domain.repository

import com.example.forged.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getExercises(): Flow<List<Exercise>>
}
