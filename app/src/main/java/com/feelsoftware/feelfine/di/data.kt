@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import androidx.room.Room
import com.feelsoftware.feelfine.data.db.AppDatabase
import com.feelsoftware.feelfine.data.db.dao.ActivityDao
import com.feelsoftware.feelfine.data.db.dao.SleepDao
import com.feelsoftware.feelfine.data.db.dao.StepsDao
import com.feelsoftware.feelfine.data.db.dao.UserProfileDao
import com.feelsoftware.feelfine.data.repository.UserRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "db.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    factory<UserProfileDao> {
        get<AppDatabase>().userProfileDao()
    }
    factory<StepsDao> {
        get<AppDatabase>().stepsDao()
    }
    factory<SleepDao> {
        get<AppDatabase>().sleepDao()
    }
    factory<ActivityDao> {
        get<AppDatabase>().activityDao()
    }
}

val repositoryModule = module {
    single<UserRepository> {
        UserRepository(get<UserProfileDao>())
    }
}