package com.example.forged.data.repository

import com.example.forged.data.local.ExerciseDao
import com.example.forged.domain.model.Exercise
import com.example.forged.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao,
) : ExerciseRepository {

    override fun getExercises(): Flow<List<Exercise>> {
        return exerciseDao.getAllExercises().map { exercises ->
            exercises.map { it.toDomain() }
        }
    }
}
