package com.feelsoftware.feelfine.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.feelsoftware.feelfine.data.db.entity.MoodEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.*

@Dao
interface MoodDao {

    @Query("SELECT * FROM mood")
    fun get(): Observable<List<MoodEntity>>

    @Query("SELECT * FROM mood WHERE date=:date")
    fun getByDate(date: Date): Observable<List<MoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mood: MoodEntity): Completable

    @Query("DELETE FROM mood")
    fun delete(): Completable
}