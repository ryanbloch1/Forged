package com.example.forged.domain.usecase

import com.example.forged.domain.model.Exercise
import com.example.forged.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveExercisesUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
) {
    operator fun invoke(): Flow<List<Exercise>> = exerciseRepository.getExercises()
}
