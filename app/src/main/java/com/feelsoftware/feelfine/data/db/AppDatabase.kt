package com.feelsoftware.feelfine.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.feelsoftware.feelfine.data.db.dao.ActivityDao
import com.feelsoftware.feelfine.data.db.dao.SleepDao
import com.feelsoftware.feelfine.data.db.dao.StepsDao
import com.feelsoftware.feelfine.data.db.dao.UserProfileDao
import com.feelsoftware.feelfine.data.db.entity.ActivityEntity
import com.feelsoftware.feelfine.data.db.entity.SleepEntity
import com.feelsoftware.feelfine.data.db.entity.StepsEntity
import com.feelsoftware.feelfine.data.db.entity.UserProfileEntity

@Database(
    version = 1,
    entities = [
        UserProfileEntity::class,
        StepsEntity::class,
        SleepEntity::class,
        ActivityEntity::class
    ],
    exportSchema = true
)
@TypeConverters(
    DateConverter::class,
    DurationConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao

    abstract fun stepsDao(): StepsDao

    abstract fun sleepDao(): SleepDao

    abstract fun activityDao(): ActivityDao
}