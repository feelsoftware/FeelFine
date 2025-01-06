@file:Suppress("DEPRECATION")

package com.feelsoftware.feelfine.fit

import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.fit.model.ActivityInfo
import com.feelsoftware.feelfine.fit.model.Duration
import com.feelsoftware.feelfine.fit.model.SleepInfo
import com.feelsoftware.feelfine.fit.model.StepsInfo
import com.feelsoftware.feelfine.utils.ActivityEngine
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessActivities
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.fitness.model.AggregateBy
import com.google.api.services.fitness.model.AggregateRequest
import com.google.api.services.fitness.model.AggregateResponse
import com.google.api.services.fitness.model.Dataset
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

interface FitRepository {

    fun getSteps(startTime: Date, endTime: Date): Single<List<StepsInfo>>

    fun getSleep(startTime: Date, endTime: Date): Single<List<SleepInfo>>

    fun getActivity(startTime: Date, endTime: Date): Single<List<ActivityInfo>>
}

@Deprecated("Migrate to HealthConnectRepository")
class GoogleFitRepository(
    private val activityEngine: ActivityEngine,
    private val permissionManager: FitPermissionManager,
) : FitRepository, SignInOptions {

    override fun getSteps(
        startTime: Date,
        endTime: Date
    ): Single<List<StepsInfo>> =
        checkPermissionAndGetData(startTime, endTime, stepsRequest)
            .map(::parseSteps)
            .subscribeOn(Schedulers.io())

    override fun getSleep(
        startTime: Date,
        endTime: Date
    ): Single<List<SleepInfo>> =
        checkPermissionAndGetData(startTime, endTime, sleepRequest)
            .map(::parseSleep)
            .subscribeOn(Schedulers.io())

    override fun getActivity(
        startTime: Date,
        endTime: Date
    ): Single<List<ActivityInfo>> =
        checkPermissionAndGetData(startTime, endTime, activityRequest)
            .map(::parseActivity)
            .subscribeOn(Schedulers.io())

    // region Get data
    private fun checkPermissionAndGetData(
        startTime: Date,
        endTime: Date,
        request: AggregateRequest
    ): Single<AggregateResponse> = permissionManager.hasPermissionObservable()
        .filter { it }
        .flatMapSingle {
            getData(startTime, endTime, request)
                .subscribeOn(Schedulers.io())
        }.firstOrError()

    private fun getData(
        startTime: Date,
        endTime: Date,
        request: AggregateRequest
    ): Single<AggregateResponse> = Single.create { emitter ->
        if (startTime.after(endTime)) {
            emitter.onError(IllegalArgumentException("Invalid date range, [$startTime, $endTime]"))
            return@create
        }

        val activity = activityEngine.activity ?: run {
            if (emitter.isDisposed) return@create
            emitter.onError(IllegalStateException("Activity is null"))
            return@create
        }

        val scopes = signInOptions.impliedScopes ?: emptyList()
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if (account?.account == null) {
            if (emitter.isDisposed) return@create
            emitter.onError(IllegalStateException("Account is null"))
            return@create
        }

        request.startTimeMillis = startTime.time
        request.endTimeMillis = endTime.time

        val credentials = GoogleAccountCredential.usingOAuth2(
            activity, scopes.map { it.scopeUri }
        )
        credentials.selectedAccount = account.account

        val fitness = com.google.api.services.fitness.Fitness.Builder(
            AndroidHttp.newCompatibleTransport(),
            AndroidJsonFactory.getDefaultInstance(),
            credentials
        )
            .setApplicationName(activity.getString(R.string.app_name))
            .build()

        try {
            val response = fitness.users()
                .dataset()
                .aggregate("me", request)
                .execute()
            emitter.onSuccess(response)
        } catch (error: Throwable) {
            Timber.e(error, "Failed to get fit data")
            if (emitter.isDisposed) return@create
            emitter.onError(error)
        }
    }
    // endregion

    // region Requests
    private val stepsRequest: AggregateRequest
        get() = AggregateRequest().apply {
            aggregateBy = listOf(
                AggregateBy().apply {
                    dataTypeName = "com.google.step_count.delta"
                }
            )
        }
    private val sleepRequest: AggregateRequest
        get() = AggregateRequest().apply {
            aggregateBy = listOf(
                AggregateBy().apply {
                    dataTypeName = "com.google.sleep.segment"
                }
            )
        }
    private val activityRequest: AggregateRequest
        get() = AggregateRequest().apply {
            aggregateBy = listOf(
                AggregateBy().apply {
                    dataTypeName = "com.google.activity.segment"
                }
            )
        }
    // engredion

    // region Parsing steps
    private val Date.toShortString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(this)

    private fun parseSteps(response: AggregateResponse): List<StepsInfo> {
        val values = mutableListOf<StepsInfo>()
        for (dataSet in response.bucket.flatMap { it.dataset }) {
            values += parseSteps(dataSet)
        }
        // Group by days
        val map = values.groupBy {
            it.date.toShortString
        }
        val calendar = Calendar.getInstance()
        return map.keys.map { key ->
            calendar.time = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(key)!!
            calendar.set(Calendar.HOUR_OF_DAY, 12)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)

            val count = map.getValue(key).sumBy { it.count }
            StepsInfo(
                date = calendar.time,
                count = count
            )
        }
    }

    private fun parseSteps(dataSet: Dataset): List<StepsInfo> {
        val list = mutableListOf<StepsInfo>()

        for (point in dataSet.point) {
            val startTime = Date(TimeUnit.NANOSECONDS.toMillis(point.startTimeNanos))
            val value = point.value.firstOrNull()?.run {
                intVal ?: fpVal?.roundToInt()
            } ?: 0
            list += StepsInfo(
                date = startTime,
                count = value
            )
        }

        return list
    }
    // endregion

    // region Parsing sleep
    private fun parseSleep(response: AggregateResponse): List<SleepInfo> {
        val rawValues = mutableListOf<SleepInfo>()
        for (dataSet in response.bucket.flatMap { it.dataset }) {
            rawValues += parseSleep(dataSet)
        }

        val min = rawValues.minOfOrNull { it.date }?.let {
            Calendar.getInstance().apply {
                time = it
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.time
        } ?: return emptyList()
        val max = rawValues.maxOfOrNull { it.date }?.let {
            Calendar.getInstance().apply {
                time = it
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }.time
        } ?: return emptyList()
        val days = (TimeUnit.DAYS.convert(max.time - min.time, TimeUnit.MILLISECONDS)).toInt() + 3

        val dates = mutableListOf<Date>()
        val calendar = Calendar.getInstance().apply {
            time = min
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            add(Calendar.DAY_OF_YEAR, -1)
        }
        repeat(days) {
            dates += calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val list = mutableListOf<SleepInfo>()
        for (i in 0 until dates.size - 2) {
            val today = dates[i]
            val tomorrow = dates[i + 1]

            val values = rawValues.filter {
                it.date.time >= today.time && it.date.time < tomorrow.time
            }
            if (values.isEmpty()) continue
            list += SleepInfo(
                date = today,
                lightSleep = values.sumBy { it.lightSleep.minutesTotal }.run(::Duration),
                deepSleep = values.sumBy { it.deepSleep.minutesTotal }.run(::Duration),
                awake = values.sumBy { it.awake.minutesTotal }.run(::Duration),
                outOfBed = values.sumBy { it.outOfBed.minutesTotal }.run(::Duration),
            )
        }
        return list
    }

    private fun parseSleep(dataSet: Dataset): List<SleepInfo> {
        val list = mutableListOf<SleepInfo>()

        for (point in dataSet.point) {
            val segmentType = point.value.firstOrNull()?.intVal?.takeIf { segmentType ->
                SleepType.values().any { segmentType == it.segmentType }
            } ?: continue

            val startTime = Date(TimeUnit.NANOSECONDS.toMillis(point.startTimeNanos))
            val endTime = Date(TimeUnit.NANOSECONDS.toMillis(point.endTimeNanos))
            val duration = (endTime.time - startTime.time).toInt()

            list += SleepInfo(
                date = startTime,
                lightSleep = takeDuration(
                    duration,
                    segmentType,
                    SleepType.GENERAL,
                    SleepType.LIGHT,
                    SleepType.REM
                ),
                deepSleep = takeDuration(duration, segmentType, SleepType.DEEP),
                awake = takeDuration(duration, segmentType, SleepType.AWAKE),
                outOfBed = takeDuration(duration, segmentType, SleepType.OUT_OF_BED),
            )
        }

        return list
    }

    private enum class SleepType(val segmentType: Int) {
        AWAKE(1),
        GENERAL(2),
        OUT_OF_BED(3),
        LIGHT(4),
        DEEP(5),
        REM(6)
    }

    private fun takeDuration(
        duration: Int,
        segmentType: Int,
        vararg sleepType: SleepType
    ) = Duration(
        if (sleepType.any { it.segmentType == segmentType }) {
            duration / 1_000 / 60
        } else {
            0
        }
    )
    // endregion

    // region Parsing Activities
    private fun parseActivity(response: AggregateResponse): List<ActivityInfo> {
        val values = mutableListOf<ActivityInfo>()
        for (dataSet in response.bucket.flatMap { it.dataset }) {
            values += parseActivity(dataSet)
        }
        // Group by days
        val map = values.groupBy {
            it.date.toShortString
        }
        val calendar = Calendar.getInstance()
        return map.keys.map { key ->
            calendar.time = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(key)!!
            calendar.set(Calendar.HOUR_OF_DAY, 12)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)

            val list = map.getValue(key)
            ActivityInfo(
                date = calendar.time,
                activityUnknown = list.sumBy { it.activityUnknown.minutesTotal }.run(::Duration),
                activityWalking = list.sumBy { it.activityWalking.minutesTotal }.run(::Duration),
                activityRunning = list.sumBy { it.activityRunning.minutesTotal }.run(::Duration),
            )
        }
    }

    private fun parseActivity(dataSet: Dataset): List<ActivityInfo> {
        val list = mutableListOf<ActivityInfo>()

        for (point in dataSet.point) {
            val rawActivity = point.value.firstOrNull()?.intVal
            if (rawActivity == null || rawActivity == 4) continue
            if (isSleepActivityType(rawActivity)) continue

            val startTime = Date(TimeUnit.NANOSECONDS.toMillis(point.startTimeNanos))
            val endTime = Date(TimeUnit.NANOSECONDS.toMillis(point.endTimeNanos))
            val duration = (endTime.time - startTime.time).toInt()

            list += ActivityInfo(
                date = startTime,
                activityUnknown = takeDuration(duration, rawActivity, ActivityType.UNKNOWN),
                activityWalking = takeDuration(duration, rawActivity, ActivityType.WALKING),
                activityRunning = takeDuration(duration, rawActivity, ActivityType.RUNNING),
            )
        }

        return list
    }

    private enum class ActivityType(val raw: Int) {
        UNKNOWN(0),
        WALKING(7),
        RUNNING(8)
    }

    private fun takeDuration(
        duration: Int,
        rawActivity: Int,
        vararg activityType: ActivityType
    ) = Duration(
        if (activityType.any { it.raw == rawActivity }) {
            duration / 1_000 / 60
        } else {
            0
        }
    )

    private val sleepActivities = mapOf(
        72 to FitnessActivities.SLEEP,
        112 to FitnessActivities.SLEEP_AWAKE,
        110 to FitnessActivities.SLEEP_DEEP,
        109 to FitnessActivities.SLEEP_LIGHT,
        111 to FitnessActivities.SLEEP_REM,
    )

    private fun isSleepActivityType(rawActivity: Int?): Boolean =
        sleepActivities.containsKey(rawActivity)
    // endregion
}
