package com.feelsoftware.feelfine.fit

import android.content.Intent
import com.feelsoftware.feelfine.fit.model.ActivityInfo
import com.feelsoftware.feelfine.fit.model.Duration as LegacyDuration
import androidx.activity.ComponentActivity
import com.feelsoftware.feelfine.data.db.dao.ActivityDao
import com.feelsoftware.feelfine.data.db.dao.SleepDao
import com.feelsoftware.feelfine.data.db.dao.StepsDao
import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.fit.model.SleepInfo
import com.feelsoftware.feelfine.fit.model.StepsInfo
import com.feelsoftware.feelfine.utils.ActivityEngine
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import java.util.Date
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber

@Suppress("DEPRECATION")
@Deprecated("Migrate to HealthConnectPermissionManager")
class HealthConnectFitPermissionManagerWrapper(
    private val activityDao: ActivityDao,
    activityEngine: ActivityEngine,
    private val permissionManager: HealthConnectPermissionManager,
    private val sleepDao: SleepDao,
    private val stepsDao: StepsDao,
    private val userRepository: UserRepository,
) : FitPermissionManager {

    init {
        activityEngine.registerCallback(object : ActivityEngine.Callback {
            override fun onActivityCreated(activity: ComponentActivity) {
                permissionManager.init(activity)
            }

            override fun onActivityDestroyed() {
                permissionManager.dispose()
            }
        })
    }

    override fun hasPermission(): Boolean {
        return permissionManager.hasPermission().value
    }

    override fun hasPermissionObservable(): Observable<Boolean> {
        return createRxObservable {
            permissionManager.hasPermission()
        }
    }

    override fun requestPermission(): Single<Boolean> {
        return createRxSingle {
            permissionManager.requestPermission()
        }.flatMap { hasPermission ->
            if (hasPermission) {
                userRepository.getProfileLegacy()
                    .firstOrError()
                    .flatMapCompletable { profile ->
                        userRepository.setProfileLegacy(profile.copy(isDemo = false))
                    }
                    // Clear cached mocked data
                    .andThen(activityDao.delete())
                    .andThen(sleepDao.delete())
                    .andThen(stepsDao.delete())
                    .subscribeOn(Schedulers.io())
                    .andThen(Single.just(true))
            } else {
                Single.just(false)
            }
        }
    }

    override fun resetPermission(): Single<Boolean> {
        return Single.just(false)
    }

    override fun onPermissionResult(requestCode: Int, resultCode: Int, data: Intent?) {}
}

@Deprecated("Migrate to HealthConnectRepository")
class HealthConnectFitRepositoryWrapper(
    private val repository: HealthConnectRepository,
) : FitRepository {

    override fun getSteps(startTime: Date, endTime: Date): Single<List<StepsInfo>> {
        return getData(
            startTime = startTime,
            endTime = endTime,
            fetcher = {
                repository.getSteps(date = it)
                    .mapCatching { steps ->
                        StepsInfo(
                            date = steps.date.toJavaDate(),
                            count = steps.count.toInt(),
                        )
                    }
            },
            fallback = {
                StepsInfo(
                    date = it,
                    count = 0,
                )
            },
        )
    }

    override fun getSleep(startTime: Date, endTime: Date): Single<List<SleepInfo>> {
        return getData(
            startTime = startTime,
            endTime = endTime,
            fetcher = {
                repository.getSleep(date = it)
                    .mapCatching { sleep ->
                        SleepInfo(
                            date = sleep.date.toJavaDate(),
                            lightSleep = sleep.lightSleep.toDuration(),
                            deepSleep = sleep.deepSleep.toDuration(),
                            awake = sleep.awake.toDuration(),
                            outOfBed = sleep.outOfBed.toDuration(),
                        )
                    }
            },
            fallback = {
                SleepInfo(
                    date = endTime,
                    lightSleep = LegacyDuration(0),
                    deepSleep = LegacyDuration(0),
                    awake = LegacyDuration(0),
                    outOfBed = LegacyDuration(0),
                )
            },
        )
    }

    override fun getActivity(startTime: Date, endTime: Date): Single<List<ActivityInfo>> {
        return getData(
            startTime = startTime,
            endTime = endTime,
            fetcher = {
                repository.getActivity(date = it)
                    .mapCatching { activity ->
                        ActivityInfo(
                            date = activity.date.toJavaDate(),
                            activityUnknown = activity.other.toDuration(),
                            activityWalking = activity.walking.toDuration(),
                            activityRunning = activity.running.toDuration(),
                        )
                    }
            },
            fallback = {
                ActivityInfo(
                    date = endTime,
                    activityUnknown = LegacyDuration(0),
                    activityWalking = LegacyDuration(0),
                    activityRunning = LegacyDuration(0),
                )
            },
        )
    }

    private inline fun <reified T> getData(
        startTime: Date,
        endTime: Date,
        crossinline fetcher: suspend (LocalDate) -> Result<T>,
        crossinline fallback: (Date) -> T,
    ): Single<List<T>> {
        return createRxSingle {
            var date = startTime.toLocalDate()
            val days = endTime.toLocalDate()
                .minus(startTime.toLocalDate()).days + 1
            val results = List(days) {
                async {
                    fetcher(date.also { date = date.plus(DatePeriod(days = 1)) })
                }
            }.awaitAll()

            // Return error only if all results failed, otherwise fallback to zero steps and log the error
            if (results.all { it.isFailure }) {
                return@createRxSingle Result.failure(results.first().exceptionOrNull()!!)
            }

            results
                .mapIndexed { day, result ->
                    result.getOrElse {
                        Timber.e(it, "Failed to get ${T::class}")
                        fallback(
                            startTime.toLocalDate()
                                .plus(DatePeriod(days = day))
                                .toJavaDate()
                        )
                    }
                }
                .let { Result.success(it) }
        }
    }
}

private fun Date.toLocalDate(): LocalDate {
    return Instant.fromEpochMilliseconds(time)
        .toLocalDateTime(TimeZone.currentSystemDefault()).date
}

private fun LocalDate.toJavaDate(): Date {
    return Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, dayOfMonth)
        set(Calendar.MONTH, monthNumber - 1)
        set(Calendar.YEAR, year)
    }.time
}

private fun Duration.toDuration(): LegacyDuration {
    return LegacyDuration(inWholeMinutes.toInt())
}

private fun <T : Any> createRxSingle(block: suspend CoroutineScope.() -> Result<T>): Single<T> {
    return Single.create { emitter ->
        val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        coroutineScope.launch {
            block()
                .onSuccess { emitter.onSuccess(it) }
                .onFailure { emitter.onError(it) }
        }

        emitter.setCancellable {
            coroutineScope.cancel()
        }
    }
}

private fun <T : Any> createRxObservable(block: () -> Flow<T>): Observable<T> {
    return Observable.create { emitter ->
        val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        block()
            .onEach { emitter.onNext(it) }
            .catch { emitter.onError(it) }
            .launchIn(coroutineScope)

        emitter.setCancellable {
            coroutineScope.cancel()
        }
    }
}
