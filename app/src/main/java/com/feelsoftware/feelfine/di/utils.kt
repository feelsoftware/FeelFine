@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import android.app.NotificationManager
import androidx.core.content.getSystemService
import androidx.work.WorkManager
import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.utils.*
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val utilsModule = module {
    single<ActivityEngine> {
        ActivityEngineImpl(androidApplication())
    }
    single<OnBoardingFlowManager> {
        OnBoardingFlowManager(get<UserRepository>())
    }
    single<WorkManager> {
        WorkManager.getInstance(androidContext())
    }
    single<MoodTracker> {
        MoodTracker(get<WorkManager>())
    }
    single<MoodNotificationManager> {
        MoodNotificationManager(
            androidContext(),
            androidContext().getSystemService<NotificationManager>()!!
        )
    }
}