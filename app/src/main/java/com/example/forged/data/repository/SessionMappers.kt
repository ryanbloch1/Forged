package com.example.forged.data.repository

import com.example.forged.data.local.entities.ExerciseEntity
import com.example.forged.data.local.entities.ExerciseEntryEntity
import com.example.forged.data.local.entities.ExerciseSetEntryEntity
import com.example.forged.data.local.entities.SessionEntity
import com.example.forged.data.local.relations.ExerciseEntryWithDetails
import com.example.forged.data.local.relations.SessionWithDetails
import com.example.forged.domain.model.Exercise
import com.example.forged.domain.model.ExerciseEntry
import com.example.forged.domain.model.Session
import com.example.forged.domain.model.SetEntry

fun SessionWithDetails.toDomain(): Session {
    return Session(
        id = session.id,
        date = session.date,
        startTime = session.startTime,
        endTime = session.endTime,
        durationMinutes = session.durationMinutes,
        notes = session.notes,
        bodyParts = session.bodyParts.toBodyPartList(),
        exercises = exerciseEntries
            .sortedBy { it.entry.orderIndex }
            .map { it.toDomain() },
    )
}

fun ExerciseEntryWithDetails.toDomain(): ExerciseEntry {
    return ExerciseEntry(
        id = entry.id,
        exercise = exercise.toDomain(),
        orderIndex = entry.orderIndex,
        durationMinutes = entry.durationMinutes,
        sets = sets
            .sortedBy { it.setNumber }
            .map { it.toDomain() },
    )
}

fun ExerciseEntity.toDomain(): Exercise {
    return Exercise(
        id = id,
        name = name,
        bodyParts = emptyList(),
    )
}

fun ExerciseSetEntryEntity.toDomain(): SetEntry {
    return SetEntry(
        id = id,
        setNumber = setNumber,
        reps = reps,
        weightKg = weightKg,
    )
}

fun Session.toEntity(): SessionEntity {
    return SessionEntity(
        id = id,
        date = date,
        startTime = startTime,
        endTime = endTime,
        durationMinutes = durationMinutes,
        notes = notes,
        bodyParts = bodyParts.toCsv(),
    )
}

fun ExerciseEntry.toEntity(sessionId: String): ExerciseEntryEntity {
    return ExerciseEntryEntity(
        id = id,
        sessionId = sessionId,
        exerciseId = exercise.id,
        orderIndex = orderIndex,
        durationMinutes = durationMinutes,
    )
}

fun SetEntry.toEntity(exerciseEntryId: String): ExerciseSetEntryEntity {
    return ExerciseSetEntryEntity(
        id = id,
        exerciseEntryId = exerciseEntryId,
        setNumber = setNumber,
        reps = reps,
        weightKg = weightKg,
    )
}

fun Session.toEntryEntities(): List<ExerciseEntryEntity> {
    return exercises.map { it.toEntity(sessionId = id) }
}

fun Session.toSetEntities(): List<ExerciseSetEntryEntity> {
    return exercises.flatMap { entry ->
        entry.sets.map { set -> set.toEntity(exerciseEntryId = entry.id) }
    }
}

private fun String?.toBodyPartList(): List<String> {
    return this
        ?.split(",")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        .orEmpty()
}

private fun List<String>.toCsv(): String? {
    return takeIf { it.isNotEmpty() }?.joinToString(", ")
}
