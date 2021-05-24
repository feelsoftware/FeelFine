package com.feelsoftware.feelfine.data.db.dao

import androidx.room.*
import com.feelsoftware.feelfine.data.db.entity.UserProfileEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile")
    fun get(): Observable<List<UserProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(profile: UserProfileEntity): Completable

    @Query("DELETE FROM user_profile")
    fun delete(): Completable
}