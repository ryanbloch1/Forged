package com.example.forged.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.forged.data.local.entities.BodyPartEntity
import com.example.forged.data.local.entities.ExerciseBodyPartCrossRefEntity
import com.example.forged.data.local.entities.ExerciseEntity
import com.example.forged.data.local.entities.ExerciseEntryEntity
import com.example.forged.data.local.entities.ExerciseSetEntryEntity
import com.example.forged.data.local.entities.SessionEntity
import com.example.forged.domain.model.BodyPartRole
import java.time.Instant
import java.time.LocalDate

@Database(
    entities = [
        SessionEntity::class,
        ExerciseEntryEntity::class,
        ExerciseSetEntryEntity::class,
        ExerciseEntity::class,
        BodyPartEntity::class,
        ExerciseBodyPartCrossRefEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class ForgedDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun exerciseDao(): ExerciseDao
}

suspend fun ForgedDatabase.seedInitialData() {
    val bodyParts = listOf(
        BodyPartEntity(id = "chest", name = "Chest"),
        BodyPartEntity(id = "back", name = "Back"),
        BodyPartEntity(id = "legs", name = "Legs"),
        BodyPartEntity(id = "shoulders", name = "Shoulders"),
        BodyPartEntity(id = "biceps", name = "Biceps"),
        BodyPartEntity(id = "triceps", name = "Triceps"),
    )

    val exercises = listOf(
        ExerciseEntity(id = "bench-press", name = "Bench Press"),
        ExerciseEntity(id = "incline-dumbbell-press", name = "Incline Dumbbell Press"),
        ExerciseEntity(id = "cable-fly", name = "Cable Fly"),
        ExerciseEntity(id = "deadlift", name = "Deadlift"),
        ExerciseEntity(id = "pull-up", name = "Pull-Up"),
        ExerciseEntity(id = "barbell-row", name = "Barbell Row"),
        ExerciseEntity(id = "lat-pulldown", name = "Lat Pulldown"),
        ExerciseEntity(id = "back-squat", name = "Back Squat"),
        ExerciseEntity(id = "romanian-deadlift", name = "Romanian Deadlift"),
        ExerciseEntity(id = "leg-press", name = "Leg Press"),
        ExerciseEntity(id = "overhead-press", name = "Overhead Press"),
        ExerciseEntity(id = "lateral-raise", name = "Lateral Raise"),
        ExerciseEntity(id = "barbell-curl", name = "Barbell Curl"),
        ExerciseEntity(id = "hammer-curl", name = "Hammer Curl"),
        ExerciseEntity(id = "tricep-pushdown", name = "Tricep Pushdown"),
    )

    val crossRefs = listOf(
        ExerciseBodyPartCrossRefEntity("bench-press", "chest", BodyPartRole.PRIMARY),
        ExerciseBodyPartCrossRefEntity("bench-press", "triceps", BodyPartRole.SECONDARY),
        ExerciseBodyPartCrossRefEntity("incline-dumbbell-press", "chest", BodyPartRole.PRIMARY),
        ExerciseBodyPartCrossRefEntity("cable-fly", "chest", BodyPartRole.PRIMARY),
        ExerciseBodyPartCrossRefEntity("deadlift", "back", BodyPartRole.PRIMARY),
        ExerciseBodyPartCrossRefEntity("deadlift", "legs", BodyPartRole.SECONDARY),
        ExerciseBodyPartCrossRefEntity("pull-up", "back", BodyPartRole.PRIMARY),
        ExerciseBodyPartCrossRefEntity("pull-up", "biceps", BodyPartRole.SECONDARY),
        ExerciseBodyPartCrossRefEntity("barbell-row", "back", BodyPartRole.PRIMARY),
        ExerciseBodyPartCrossRefEntity("lat-pulldown", "back", BodyPartRole.PRIMARY),
        ExerciseBodyPartCrossRefEntity("back-squat", "legs", BodyPartRole.PRIMARY),
        ExerciseBodyPartCrossRefEntity("romanian-deadlift", "legs", BodyPartRole.PRIMARY),
        ExerciseBodyPartCrossRefEntity("leg-press", "legs", BodyPartRole.PRIMARY),
        ExerciseBodyPartCrossRefEntity("overhead-press", "shoulders", BodyPartRole.PRIMARY),
        ExerciseBodyPartCrossRefEntity("lateral-raise", "shoulders", BodyPartRole.PRIMARY),
        ExerciseBodyPartCrossRefEntity("barbell-curl", "biceps", BodyPartRole.PRIMARY),
        ExerciseBodyPartCrossRefEntity("hammer-curl", "biceps", BodyPartRole.PRIMARY),
        ExerciseBodyPartCrossRefEntity("tricep-pushdown", "triceps", BodyPartRole.PRIMARY),
    )

    exerciseDao().seedCatalog(bodyParts, exercises, crossRefs)

    val sessionDao = sessionDao()

    sessionDao.upsertSessionGraph(
        session = SessionEntity(
            id = "seed-session-1",
            date = LocalDate.of(2026, 8, 20),
            startTime = Instant.parse("2026-08-20T17:00:00Z"),
            endTime = Instant.parse("2026-08-20T18:05:00Z"),
            durationMinutes = 65,
            notes = "Chest day",
            bodyParts = "Chest, Triceps",
        ),
        entries = listOf(
            ExerciseEntryEntity(
                id = "seed-entry-1",
                sessionId = "seed-session-1",
                exerciseId = "bench-press",
                orderIndex = 0,
                durationMinutes = 25,
            ),
            ExerciseEntryEntity(
                id = "seed-entry-2",
                sessionId = "seed-session-1",
                exerciseId = "incline-dumbbell-press",
                orderIndex = 1,
                durationMinutes = 20,
            ),
        ),
        sets = listOf(
            ExerciseSetEntryEntity("seed-set-1", "seed-entry-1", 1, 8, 80.0),
            ExerciseSetEntryEntity("seed-set-2", "seed-entry-1", 2, 8, 80.0),
            ExerciseSetEntryEntity("seed-set-3", "seed-entry-1", 3, 6, 85.0),
            ExerciseSetEntryEntity("seed-set-4", "seed-entry-2", 1, 10, 30.0),
            ExerciseSetEntryEntity("seed-set-5", "seed-entry-2", 2, 10, 30.0),
        ),
    )

    sessionDao.upsertSessionGraph(
        session = SessionEntity(
            id = "seed-session-2",
            date = LocalDate.of(2026, 8, 18),
            startTime = Instant.parse("2026-08-18T18:00:00Z"),
            endTime = Instant.parse("2026-08-18T19:10:00Z"),
            durationMinutes = 70,
            notes = "Back focus",
            bodyParts = "Back, Biceps",
        ),
        entries = listOf(
            ExerciseEntryEntity(
                id = "seed-entry-3",
                sessionId = "seed-session-2",
                exerciseId = "deadlift",
                orderIndex = 0,
                durationMinutes = 30,
            ),
            ExerciseEntryEntity(
                id = "seed-entry-4",
                sessionId = "seed-session-2",
                exerciseId = "pull-up",
                orderIndex = 1,
                durationMinutes = 15,
            ),
        ),
        sets = listOf(
            ExerciseSetEntryEntity("seed-set-6", "seed-entry-3", 1, 5, 120.0),
            ExerciseSetEntryEntity("seed-set-7", "seed-entry-3", 2, 5, 120.0),
            ExerciseSetEntryEntity("seed-set-8", "seed-entry-4", 1, 8, null),
            ExerciseSetEntryEntity("seed-set-9", "seed-entry-4", 2, 6, null),
        ),
    )

    sessionDao.upsertSessionGraph(
        session = SessionEntity(
            id = "seed-session-3",
            date = LocalDate.of(2026, 8, 21),
            startTime = null,
            endTime = null,
            durationMinutes = 55,
            notes = null,
            bodyParts = "Legs",
        ),
        entries = listOf(
            ExerciseEntryEntity(
                id = "seed-entry-5",
                sessionId = "seed-session-3",
                exerciseId = "back-squat",
                orderIndex = 0,
                durationMinutes = 35,
            ),
        ),
        sets = listOf(
            ExerciseSetEntryEntity("seed-set-10", "seed-entry-5", 1, 5, 100.0),
            ExerciseSetEntryEntity("seed-set-11", "seed-entry-5", 2, 5, 100.0),
            ExerciseSetEntryEntity("seed-set-12", "seed-entry-5", 3, 5, 105.0),
        ),
    )
}
