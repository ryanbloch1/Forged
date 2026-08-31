package com.example.forged.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val date: LocalDate,
    val startTime: Instant?,
    val endTime: Instant?,
    val durationMinutes: Int,
    val notes: String?,
    val bodyParts: String? = null,
)
