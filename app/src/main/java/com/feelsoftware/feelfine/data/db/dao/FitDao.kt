package com.feelsoftware.feelfine.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.feelsoftware.feelfine.data.db.entity.ActivityEntity
import com.feelsoftware.feelfine.data.db.entity.SleepEntity
import com.feelsoftware.feelfine.data.db.entity.StepsEntity
import com.feelsoftware.feelfine.data.repository.DataSource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.*

@Dao
interface StepsDao : DataSource<StepsEntity> {

    @Query("SELECT * FROM steps WHERE date >= :startTime AND date <= :endTime")
    override fun getAll(startTime: Date, endTime: Date): Single<List<StepsEntity>>

    @Query("DELETE FROM steps")
    override fun delete(): Completable
}

@Dao
interface SleepDao : DataSource<SleepEntity> {

    @Query("SELECT * FROM sleep WHERE date >= :startTime AND date <= :endTime")
    override fun getAll(startTime: Date, endTime: Date): Single<List<SleepEntity>>

    @Query("DELETE FROM sleep")
    override fun delete(): Completable
}

@Dao
interface ActivityDao : DataSource<ActivityEntity> {

    @Query("SELECT * FROM activity WHERE date >= :startTime AND date <= :endTime")
    override fun getAll(startTime: Date, endTime: Date): Single<List<ActivityEntity>>

    @Query("DELETE FROM activity")
    override fun delete(): Completable
}