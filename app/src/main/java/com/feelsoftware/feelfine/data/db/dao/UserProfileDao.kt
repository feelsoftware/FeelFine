package com.feelsoftware.feelfine.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.feelsoftware.feelfine.data.db.entity.UserProfileEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile")
    fun get(): Flow<List<UserProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun delete()

    @Deprecated("Use Coroutines instead")
    @Query("SELECT * FROM user_profile")
    fun getLegacy(): Observable<List<UserProfileEntity>>

    @Deprecated("Use Coroutines instead")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLegacy(profile: UserProfileEntity): Completable

    @Deprecated("Use Coroutines instead")
    @Query("DELETE FROM user_profile")
    fun deleteLegacy(): Completable
}