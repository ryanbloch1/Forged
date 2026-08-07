package com.example.forged.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.forged.data.local.enums.BodyPartRole

@Entity(
    tableName = "exercise_body_part_cross_ref",
    primaryKeys = ["exerciseId", "bodyPartId"],
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = BodyPartEntity::class,
            parentColumns = ["id"],
            childColumns = ["bodyPartId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("exerciseId"),
        Index("bodyPartId"),
    ],
)
data class ExerciseBodyPartCrossRefEntity(
    val exerciseId: String,
    val bodyPartId: String,
    val role: BodyPartRole,
)
