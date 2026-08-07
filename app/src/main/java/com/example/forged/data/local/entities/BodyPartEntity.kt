package com.example.forged.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_parts")
data class BodyPartEntity(
    @PrimaryKey val id: String,
    val name: String,
)
