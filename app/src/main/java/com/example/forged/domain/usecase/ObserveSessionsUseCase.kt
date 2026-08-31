package com.example.forged.domain.usecase

import com.example.forged.domain.model.Session
import com.example.forged.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSessionsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    operator fun invoke(): Flow<List<Session>> = sessionRepository.getSessions()
}
