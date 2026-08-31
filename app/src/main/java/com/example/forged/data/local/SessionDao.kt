package com.example.forged.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.forged.data.local.entities.ExerciseEntryEntity
import com.example.forged.data.local.entities.ExerciseSetEntryEntity
import com.example.forged.data.local.entities.SessionEntity
import com.example.forged.data.local.relations.SessionWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Query("SELECT * FROM sessions ORDER BY date DESC")
    fun getSessions(): Flow<List<SessionEntity>>

    @Transaction
    @Query("SELECT * FROM sessions ORDER BY date DESC")
    fun getSessionsWithDetails(): Flow<List<SessionWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseEntries(entries: List<ExerciseEntryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetEntries(sets: List<ExerciseSetEntryEntity>)

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteSession(id: String)

    @Query("DELETE FROM exercise_entries WHERE sessionId = :sessionId")
    suspend fun deleteEntriesForSession(sessionId: String)

    @Transaction
    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getSessionWithDetails(id: String): SessionWithDetails?

    @Query("SELECT COUNT(*) FROM sessions")
    suspend fun countSessions(): Int

    @Transaction
    suspend fun upsertSessionGraph(
        session: SessionEntity,
        entries: List<ExerciseEntryEntity>,
        sets: List<ExerciseSetEntryEntity>,
    ) {
        insertSession(session)
        deleteEntriesForSession(session.id)
        if (entries.isNotEmpty()) {
            insertExerciseEntries(entries)
        }
        if (sets.isNotEmpty()) {
            insertSetEntries(sets)
        }
    }
}
