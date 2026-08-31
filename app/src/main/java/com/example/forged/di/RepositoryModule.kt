package com.example.forged.di

import com.example.forged.data.repository.ExerciseRepositoryImpl
import com.example.forged.data.repository.SessionRepositoryImpl
import com.example.forged.domain.repository.ExerciseRepository
import com.example.forged.domain.repository.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(impl: ExerciseRepositoryImpl): ExerciseRepository
}
