package com.feelsoftware.feelfine.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.feelsoftware.feelfine.data.db.entity.ActivityEntity
import com.feelsoftware.feelfine.data.db.entity.SleepEntity
import com.feelsoftware.feelfine.data.db.entity.StepsEntity
import com.feelsoftware.feelfine.data.repository.DataSource
import io.reactivex.rxjava3.core.Single
import java.util.*

@Dao
interface StepsDao : DataSource<StepsEntity> {

    @Query("SELECT * FROM steps WHERE date >= :startTime AND date <= :endTime")
    override fun getAll(startTime: Date, endTime: Date): Single<List<StepsEntity>>
}

@Dao
interface SleepDao : DataSource<SleepEntity> {

    @Query("SELECT * FROM sleep WHERE date >= :startTime AND date <= :endTime")
    override fun getAll(startTime: Date, endTime: Date): Single<List<SleepEntity>>
}

@Dao
interface ActivityDao : DataSource<ActivityEntity> {

    @Query("SELECT * FROM activity WHERE date >= :startTime AND date <= :endTime")
    override fun getAll(startTime: Date, endTime: Date): Single<List<ActivityEntity>>
}