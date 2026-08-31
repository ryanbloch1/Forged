package com.example.forged.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.forged.data.local.entities.ExerciseEntity
import com.example.forged.data.local.entities.ExerciseEntryEntity
import com.example.forged.data.local.entities.ExerciseSetEntryEntity

data class ExerciseEntryWithDetails(
    @Embedded val entry: ExerciseEntryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseEntryId",
    )
    val sets: List<ExerciseSetEntryEntity>,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "id",
    )
    val exercise: ExerciseEntity,
)
