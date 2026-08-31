package com.example.forged.domain.usecase

import com.example.forged.domain.repository.SessionRepository
import javax.inject.Inject

class DeleteSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(id: String) {
        sessionRepository.deleteSession(id)
    }
}
