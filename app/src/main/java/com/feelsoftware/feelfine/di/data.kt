@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import androidx.room.Room
import com.feelsoftware.feelfine.data.db.AppDatabase
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
}

val repositoryModule = module {
    single<UserRepository> {
        UserRepository(get<UserProfileDao>())
    }
}