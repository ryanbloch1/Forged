package com.example.forged.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.forged.data.local.ExerciseDao
import com.example.forged.data.local.ForgedDatabase
import com.example.forged.data.local.SessionDao
import com.example.forged.data.local.seedInitialData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val TAG = "ForgedDatabase"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ForgedDatabase {
        val holder = DatabaseHolder()
        val database = Room.databaseBuilder(
            context,
            ForgedDatabase::class.java,
            "forged.db",
        )
            .fallbackToDestructiveMigration()
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            runSeed(holder.database, reason = "onCreate")
                        }
                    }
                },
            )
            .build()
        holder.database = database

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val count = database.sessionDao().countSessions()
                Log.i(TAG, "session count=$count")
                if (count == 0) {
                    runSeed(database, reason = "empty-db")
                }
            } catch (error: Throwable) {
                Log.e(TAG, "Failed checking/seeding database", error)
            }
        }

        return database
    }

    private suspend fun runSeed(database: ForgedDatabase, reason: String) {
        try {
            database.seedInitialData()
            Log.i(TAG, "Seed completed ($reason)")
        } catch (error: Throwable) {
            Log.e(TAG, "Seed failed ($reason)", error)
        }
    }

    @Provides
    fun provideSessionDao(database: ForgedDatabase): SessionDao = database.sessionDao()

    @Provides
    fun provideExerciseDao(database: ForgedDatabase): ExerciseDao = database.exerciseDao()
}

private class DatabaseHolder {
    lateinit var database: ForgedDatabase
}
