package com.example.forged.data.local

import androidx.room.TypeConverter
import com.example.forged.data.local.enums.BodyPartRole
import java.time.Instant
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): Long? = value?.toEpochDay()

    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)

    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun fromBodyPartRole(value: BodyPartRole?): String? = value?.name

    @TypeConverter
    fun toBodyPartRole(value: String?): BodyPartRole? = value?.let(BodyPartRole::valueOf)
}
