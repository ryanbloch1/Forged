package com.example.forged.domain.repository

import com.example.forged.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getSessions(): Flow<List<Session>>
    suspend fun getSession(id: String): Session?
    suspend fun saveSession(session: Session)
    suspend fun deleteSession(id: String)
}
