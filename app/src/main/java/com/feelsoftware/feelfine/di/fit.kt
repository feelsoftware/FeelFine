@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import com.feelsoftware.feelfine.data.db.dao.ActivityDao
import com.feelsoftware.feelfine.data.db.dao.SleepDao
import com.feelsoftware.feelfine.data.db.dao.StepsDao
import com.feelsoftware.feelfine.data.repository.*
import com.feelsoftware.feelfine.fit.FitPermissionManager
import com.feelsoftware.feelfine.fit.FitRepository
import com.feelsoftware.feelfine.fit.GoogleFitPermissionManager
import com.feelsoftware.feelfine.fit.GoogleFitRepository
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.utils.ActivityEngine
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val fitModule = module {
    single<FitRepository> {
        GoogleFitRepository(androidContext(), get<FitPermissionManager>())
//        MockFitRepository()
    }
    single<FitPermissionManager> {
        GoogleFitPermissionManager(get<ActivityEngine>())
    }
    factory<GetFitDataUseCase> {
        GetFitDataUseCase(
            get<StepsDataRepository>(),
            get<SleepDataRepository>(),
            get<ActivityDataRepository>(),
        )
    }
    factory<StepsDataRepository> {
        StepsDataRepository(
            get<StepsDao>(),
            StepsRemoteDataSource(get<FitRepository>())
        )
    }
    factory<SleepDataRepository> {
        SleepDataRepository(
            get<SleepDao>(),
            SleepRemoteDataSource(get<FitRepository>())
        )
    }
    factory<ActivityDataRepository> {
        ActivityDataRepository(
            get<ActivityDao>(),
            ActivityRemoteDataSource(get<FitRepository>())
        )
    }
}