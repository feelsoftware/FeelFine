@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import android.app.NotificationManager
import androidx.core.content.getSystemService
import androidx.work.WorkManager
import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.score.ScoreCalculator
import com.feelsoftware.feelfine.score.ScoreCalculatorImpl
import com.feelsoftware.feelfine.score.ScoreTargetProvider
import com.feelsoftware.feelfine.score.ScoreTargetProviderImpl
import com.feelsoftware.feelfine.utils.ActivityEngine
import com.feelsoftware.feelfine.utils.ActivityEngineImpl
import com.feelsoftware.feelfine.utils.MoodNotificationManager
import com.feelsoftware.feelfine.utils.MoodTracker
import com.feelsoftware.feelfine.utils.StringResources
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val utilsModule = module {
    single<ActivityEngine> {
        ActivityEngineImpl(androidApplication())
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
    single<ScoreTargetProvider> {
        ScoreTargetProviderImpl(get<UserRepository>())
    }
    single<ScoreCalculator> {
        ScoreCalculatorImpl()
    }
    single {
        StringResources(get<ActivityEngine>())
    }
}