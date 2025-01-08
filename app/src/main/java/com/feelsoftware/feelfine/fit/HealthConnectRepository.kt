@file:Suppress("SameParameterValue")

package com.feelsoftware.feelfine.fit

import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.toJavaLocalDateTime

data class Activity(
    val date: LocalDate,
    val walking: Duration,
    val running: Duration,
    val other: Duration,
)

data class Sleep(
    val date: LocalDate,
    val lightSleep: Duration,
    val deepSleep: Duration,
    val awake: Duration,
    val outOfBed: Duration,
)

data class Steps(
    val date: LocalDate,
    val count: Long,
)

interface HealthConnectRepository {

    suspend fun getActivity(date: LocalDate): Result<Activity>

    suspend fun getSleep(date: LocalDate): Result<Sleep>

    suspend fun getSteps(date: LocalDate): Result<Steps>
}

class HealthConnectRepositoryImpl(
    private val clientProvider: HealthConnectClientProvider,
    permissionManager: HealthConnectPermissionManager,
) : HealthConnectRepository {

    private val hasPermission: StateFlow<Boolean> = permissionManager.hasPermission()

    override suspend fun getActivity(date: LocalDate): Result<Activity> {
        return get<ExerciseSessionRecord>(
            startTime = date.dayStart(),
            endTime = date.dayEnd(),
        ).mapCatching { records ->
            val walking = records.duration(ExerciseSessionRecord.EXERCISE_TYPE_WALKING)
            val running = records.duration(ExerciseSessionRecord.EXERCISE_TYPE_RUNNING)
            val other = records.duration() - walking - running

            Activity(
                date = date,
                walking = walking,
                running = running,
                other = other,
            )
        }
    }

    override suspend fun getSleep(date: LocalDate): Result<Sleep> {
        return get<SleepSessionRecord>(
            startTime = date.minus(DatePeriod(days = 1)).atTime(hour = 12, minute = 0, second = 0),
            endTime = date.atTime(hour = 12, minute = 0, second = 0),
        ).mapCatching { records ->
            val lightSleep = records.duration(SleepSessionRecord.STAGE_TYPE_LIGHT) +
                    records.duration(SleepSessionRecord.STAGE_TYPE_REM) +
                    records.duration(SleepSessionRecord.STAGE_TYPE_UNKNOWN)
            val deepSleep = records.duration(SleepSessionRecord.STAGE_TYPE_DEEP)
            val awake = records.duration(SleepSessionRecord.STAGE_TYPE_AWAKE)
            val outOfBed = records.duration(SleepSessionRecord.STAGE_TYPE_OUT_OF_BED)

            Sleep(
                date = date,
                lightSleep = lightSleep,
                deepSleep = deepSleep,
                awake = awake,
                outOfBed = outOfBed,
            )
        }
    }

    override suspend fun getSteps(date: LocalDate): Result<Steps> {
        return aggregate(
            startTime = date.dayStart(),
            endTime = date.dayEnd(),
            metrics = setOf(StepsRecord.COUNT_TOTAL),
        )
            .mapCatching { records ->
                val steps = records[StepsRecord.COUNT_TOTAL] as? Long ?: 0L

                Steps(
                    date = date,
                    count = steps,
                )
            }
    }

    private suspend inline fun <reified T : Record> get(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
    ): Result<List<T>> = runCatching {
        if (!hasPermission.value) {
            return@runCatching emptyList<T>()
        }
        val client = clientProvider().getOrThrow()

        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = T::class,
                timeRangeFilter = TimeRangeFilter.between(
                    startTime = startTime.toJavaLocalDateTime(),
                    endTime = endTime.toJavaLocalDateTime(),
                )
            )
        )

        response.records
    }

    private suspend fun aggregate(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        metrics: Set<AggregateMetric<Number>>,
    ): Result<Map<AggregateMetric<Number>, Number>> = runCatching {
        if (!hasPermission.value) {
            return@runCatching emptyMap()
        }
        val client = clientProvider().getOrThrow()

        val response = client.aggregate(
            AggregateRequest(
                metrics = metrics,
                timeRangeFilter = TimeRangeFilter.between(
                    startTime = startTime.toJavaLocalDateTime(),
                    endTime = endTime.toJavaLocalDateTime(),
                ),
            )
        )

        metrics.associateWith { metric -> (response[metric] ?: 0L) }
    }

    @JvmName("exerciseDuration")
    private fun List<ExerciseSessionRecord>.duration(
        @ExerciseSessionRecord.ExerciseTypes type: Int,
    ): Duration {
        return filter { it.exerciseType == type }
            .duration()
    }

    private fun List<ExerciseSessionRecord>.duration(): Duration {
        return sumOf { it.endTime.epochSecond - it.startTime.epochSecond }
            .seconds
    }

    @JvmName("sleepDuration")
    private fun List<SleepSessionRecord>.duration(
        @SleepSessionRecord.StageTypes type: Int
    ): Duration {
        return map { record -> record.stages.filter { type == it.stage } }
            .flatten()
            .sumOf { it.endTime.epochSecond - it.startTime.epochSecond }
            .seconds
    }

    private fun LocalDate.dayStart(): LocalDateTime =
        atTime(hour = 0, minute = 0, second = 0)

    private fun LocalDate.dayEnd(): LocalDateTime =
        atTime(hour = 23, minute = 59, second = 59)
}
