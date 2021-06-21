package com.feelsoftware.feelfine.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.feelsoftware.feelfine.fit.model.Duration
import java.util.*

@Entity(
    tableName = "steps"
)
data class StepsEntity(
    @PrimaryKey
    val date: Date,
    val count: Int,
)

@Entity(
    tableName = "sleep"
)
data class SleepEntity(
    @PrimaryKey
    val date: Date,
    val lightSleep: Duration,
    val deepSleep: Duration,
    val awake: Duration,
    val outOfBed: Duration,
)

@Entity(
    tableName = "activity"
)
data class ActivityEntity(
    @PrimaryKey
    val date: Date,
    val activityUnknown: Duration,
    val activityWalking: Duration,
    val activityRunning: Duration,
)