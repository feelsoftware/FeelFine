@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import com.feelsoftware.feelfine.data.db.dao.ActivityDao
import com.feelsoftware.feelfine.data.db.dao.SleepDao
import com.feelsoftware.feelfine.data.db.dao.StepsDao
import com.feelsoftware.feelfine.data.repository.ActivityDataRepository
import com.feelsoftware.feelfine.data.repository.ActivityRemoteDataSource
import com.feelsoftware.feelfine.data.repository.SleepDataRepository
import com.feelsoftware.feelfine.data.repository.SleepRemoteDataSource
import com.feelsoftware.feelfine.data.repository.StepsDataRepository
import com.feelsoftware.feelfine.data.repository.StepsRemoteDataSource
import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.fit.FitPermissionManager
import com.feelsoftware.feelfine.fit.FitRepository
import com.feelsoftware.feelfine.fit.GoogleFitPermissionManager
import com.feelsoftware.feelfine.fit.GoogleFitRepository
import com.feelsoftware.feelfine.fit.HealthConnectClientProvider
import com.feelsoftware.feelfine.fit.HealthConnectClientProviderImpl
import com.feelsoftware.feelfine.fit.HealthConnectFitPermissionManagerWrapper
import com.feelsoftware.feelfine.fit.HealthConnectFitRepositoryWrapper
import com.feelsoftware.feelfine.fit.HealthConnectPermissionManager
import com.feelsoftware.feelfine.fit.HealthConnectPermissionManagerImpl
import com.feelsoftware.feelfine.fit.HealthConnectRepository
import com.feelsoftware.feelfine.fit.HealthConnectRepositoryImpl
import com.feelsoftware.feelfine.fit.mock.MockFitRepository
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.utils.ActivityEngine
import org.koin.android.ext.koin.androidApplication
import org.koin.core.scope.Scope
import org.koin.dsl.module

val fitModule = module {
    single<HealthConnectClientProvider> {
        HealthConnectClientProviderImpl(
            context = androidApplication(),
        )
    }
    single<HealthConnectPermissionManager> {
        HealthConnectPermissionManagerImpl(
            clientProvider = get<HealthConnectClientProvider>(),
        )
    }
    factory<HealthConnectRepository> {
        HealthConnectRepositoryImpl(
            clientProvider = get<HealthConnectClientProvider>(),
            permissionManager = get<HealthConnectPermissionManager>(),
        )
    }
    factory<FitRepository> {
        val profile = get<UserRepository>().getProfileLegacy().firstOrError().blockingGet()
        if (profile.isDemo) {
            MockFitRepository()
        } else {
            if (hasHealthConnect) {
                HealthConnectFitRepositoryWrapper(
                    repository = get<HealthConnectRepository>(),
                )
            } else {
                GoogleFitRepository(
                    activityEngine = get<ActivityEngine>(),
                    permissionManager = get<FitPermissionManager>(),
                )
            }
        }
    }
    single<FitPermissionManager> {
        if (hasHealthConnect) {
            HealthConnectFitPermissionManagerWrapper(
                activityDao = get<ActivityDao>(),
                activityEngine = get<ActivityEngine>(),
                permissionManager = get<HealthConnectPermissionManager>(),
                sleepDao = get<SleepDao>(),
                stepsDao = get<StepsDao>(),
                userRepository = get<UserRepository>(),
            )
        } else {
            GoogleFitPermissionManager(
                activityDao = get<ActivityDao>(),
                activityEngine = get<ActivityEngine>(),
                sleepDao = get<SleepDao>(),
                stepsDao = get<StepsDao>(),
                userRepository = get<UserRepository>(),
            )
        }
    }
    factory<GetFitDataUseCase> {
        GetFitDataUseCase(
            stepsRepository = get<StepsDataRepository>(),
            sleepRepository = get<SleepDataRepository>(),
            activityRepository = get<ActivityDataRepository>(),
        )
    }
    factory<StepsDataRepository> {
        StepsDataRepository(
            localDataSource = get<StepsDao>(),
            remoteDataSource = StepsRemoteDataSource(get<FitRepository>())
        )
    }
    factory<SleepDataRepository> {
        SleepDataRepository(
            localDataSource = get<SleepDao>(),
            remoteDataSource = SleepRemoteDataSource(get<FitRepository>())
        )
    }
    factory<ActivityDataRepository> {
        ActivityDataRepository(
            localDataSource = get<ActivityDao>(),
            remoteDataSource = ActivityRemoteDataSource(get<FitRepository>())
        )
    }
}

private inline val Scope.hasHealthConnect: Boolean
    get() = get<HealthConnectClientProvider>().invoke().getOrNull() != null
