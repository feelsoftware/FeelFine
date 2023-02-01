package com.feelsoftware.feelfine.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.feelsoftware.feelfine.data.db.dao.*
import com.feelsoftware.feelfine.data.db.entity.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

@Database(
    version = 3,
    entities = [
        ActivityEntity::class,
        MoodEntity::class,
        SleepEntity::class,
        StepsEntity::class,
        UserProfileEntity::class,
    ],
    exportSchema = false,
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

    fun clear(): Completable =
        Completable.complete()
            .andThen(activityDao().delete())
            .andThen(moodDao().delete())
            .andThen(sleepDao().delete())
            .andThen(stepsDao().delete())
            .andThen(userProfileDao().deleteLegacy())
            .subscribeOn(Schedulers.io())
}