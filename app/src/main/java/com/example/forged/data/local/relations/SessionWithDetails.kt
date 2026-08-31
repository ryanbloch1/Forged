package com.example.forged.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.forged.data.local.entities.ExerciseEntryEntity
import com.example.forged.data.local.entities.SessionEntity

data class SessionWithDetails(
    @Embedded val session: SessionEntity,
    @Relation(
        entity = ExerciseEntryEntity::class,
        parentColumn = "id",
        entityColumn = "sessionId",
    )
    val exerciseEntries: List<ExerciseEntryWithDetails>,
)
