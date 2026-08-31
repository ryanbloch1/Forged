package com.example.forged.data.repository

import com.example.forged.data.local.SessionDao
import com.example.forged.domain.model.Session
import com.example.forged.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao,
) : SessionRepository {

    override fun getSessions(): Flow<List<Session>> {
        return sessionDao.getSessionsWithDetails().map { sessions ->
            sessions.map { it.toDomain() }
        }
    }

    override suspend fun getSession(id: String): Session? {
        return sessionDao.getSessionWithDetails(id)?.toDomain()
    }

    override suspend fun saveSession(session: Session) {
        sessionDao.upsertSessionGraph(
            session = session.toEntity(),
            entries = session.toEntryEntities(),
            sets = session.toSetEntities(),
        )
    }

    override suspend fun deleteSession(id: String) {
        sessionDao.deleteSession(id)
    }
}
