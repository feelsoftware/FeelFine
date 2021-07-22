@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import androidx.room.Room
import com.feelsoftware.feelfine.data.db.AppDatabase
import com.feelsoftware.feelfine.data.db.dao.*
import com.feelsoftware.feelfine.data.repository.MoodDateConverter
import com.feelsoftware.feelfine.data.repository.MoodRepository
import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.data.usecase.GetCurrentMoodUseCase
import com.feelsoftware.feelfine.data.usecase.GetMoodByDateUseCase
import com.feelsoftware.feelfine.data.usecase.GetPercentMoodUseCase
import com.feelsoftware.feelfine.data.usecase.SetMoodUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "db.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    factory<ActivityDao> {
        get<AppDatabase>().activityDao()
    }
    factory<MoodDao> {
        get<AppDatabase>().moodDao()
    }
    factory<SleepDao> {
        get<AppDatabase>().sleepDao()
    }
    factory<StepsDao> {
        get<AppDatabase>().stepsDao()
    }
    factory<UserProfileDao> {
        get<AppDatabase>().userProfileDao()
    }
}

val repositoryModule = module {
    single<UserRepository> {
        UserRepository(get<UserProfileDao>())
    }
    single<MoodRepository> {
        MoodRepository(get<MoodDao>(), get<MoodDateConverter>())
    }
    single<MoodDateConverter> {
        MoodDateConverter()
    }
}

val useCaseModule = module {
    factory<SetMoodUseCase> {
        SetMoodUseCase(get<MoodRepository>())
    }
    factory<GetMoodByDateUseCase> {
        GetMoodByDateUseCase(get<MoodRepository>())
    }
    factory<GetCurrentMoodUseCase> {
        GetCurrentMoodUseCase(get<GetMoodByDateUseCase>())
    }
    factory<GetPercentMoodUseCase> {
        GetPercentMoodUseCase(get<GetMoodByDateUseCase>())
    }
}