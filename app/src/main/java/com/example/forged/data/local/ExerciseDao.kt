package com.example.forged.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.forged.data.local.entities.BodyPartEntity
import com.example.forged.data.local.entities.ExerciseBodyPartCrossRefEntity
import com.example.forged.data.local.entities.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyParts(bodyParts: List<BodyPartEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(crossRefs: List<ExerciseBodyPartCrossRefEntity>)

    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query(
        """
        SELECT exercises.* FROM exercises
        INNER JOIN exercise_body_part_cross_ref AS cross_ref
            ON exercises.id = cross_ref.exerciseId
        WHERE cross_ref.bodyPartId = :bodyPartId AND cross_ref.role = :role
        ORDER BY exercises.name ASC
        """,
    )
    fun getExercisesByBodyPart(bodyPartId: String, role: String): Flow<List<ExerciseEntity>>

    @Transaction
    suspend fun seedCatalog(
        bodyParts: List<BodyPartEntity>,
        exercises: List<ExerciseEntity>,
        crossRefs: List<ExerciseBodyPartCrossRefEntity>,
    ) {
        insertBodyParts(bodyParts)
        insertExercises(exercises)
        insertCrossRefs(crossRefs)
    }
}
