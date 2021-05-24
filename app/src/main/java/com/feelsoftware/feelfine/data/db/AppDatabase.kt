package com.feelsoftware.feelfine.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.feelsoftware.feelfine.data.db.dao.UserProfileDao
import com.feelsoftware.feelfine.data.db.entity.UserProfileEntity

@Database(
    version = 1,
    entities = [
        UserProfileEntity::class,
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
}