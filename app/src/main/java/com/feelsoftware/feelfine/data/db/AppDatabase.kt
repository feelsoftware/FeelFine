package com.feelsoftware.feelfine.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.feelsoftware.feelfine.data.db.dao.*
import com.feelsoftware.feelfine.data.db.entity.*

@Database(
    version = 1,
    entities = [
        ActivityEntity::class,
        MoodEntity::class,
        SleepEntity::class,
        StepsEntity::class,
        UserProfileEntity::class,
    ],
    exportSchema = true
)
@TypeConverters(
    DateConverter::class,
    DurationConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun activityDao(): ActivityDao

    abstract fun moodDao(): MoodDao

    abstract fun sleepDao(): SleepDao

    abstract fun stepsDao(): StepsDao

    abstract fun userProfileDao(): UserProfileDao
}