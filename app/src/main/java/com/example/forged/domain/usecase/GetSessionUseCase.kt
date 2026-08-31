package com.example.forged.domain.usecase

import com.example.forged.domain.model.Session
import com.example.forged.domain.repository.SessionRepository
import javax.inject.Inject

class GetSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(id: String): Session? = sessionRepository.getSession(id)
}
