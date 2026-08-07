package com.example.forged.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_set_entries",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseEntryId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("exerciseEntryId"),
    ],
)
data class ExerciseSetEntryEntity(
    @PrimaryKey val id: String,
    val exerciseEntryId: String,
    val setNumber: Int,
    val reps: Int,
    val weightKg: Double?,
)
