package com.feelsoftware.feelfine.data.repository

import com.feelsoftware.feelfine.data.db.dao.UserProfileDao
import com.feelsoftware.feelfine.data.db.entity.UserProfileEntity
import com.feelsoftware.feelfine.data.model.UserProfile
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// TODO: add interface UserRepository and class UserRepositoryImpl
class UserRepository(
    private val dao: UserProfileDao,
) {

    private val emptyProfile = UserProfile(
        name = "",
        gender = UserProfile.Gender.MALE,
        weight = 0f,
        birthday = Date(),
        isDemo = true,
    )

    suspend fun setProfile(profile: UserProfile) {
        dao.insert(profile.toEntity())
    }

    suspend fun hasProfile(): Boolean =
        getProfile().first() != emptyProfile

    fun getProfile(): Flow<UserProfile> =
        dao.get()
            .map {
                it.firstOrNull()?.toModel() ?: emptyProfile
            }

    suspend fun clear() {
        dao.delete()
    }

    @Deprecated("Use Coroutines instead")
    fun setProfileLegacy(profile: UserProfile): Completable =
        dao.insertLegacy(profile.toEntity())
            .subscribeOn(Schedulers.io())

    @Deprecated("Use Coroutines instead")
    fun hasProfileLegacy(): Observable<Boolean> =
        getProfileLegacy()
            .map {
                it != emptyProfile
            }

    @Deprecated("Use Coroutines instead")
    fun getProfileLegacy(): Observable<UserProfile> =
        dao.getLegacy()
            .map {
                it.firstOrNull()?.toModel() ?: emptyProfile
            }
            .subscribeOn(Schedulers.io())

    fun clearLegacy(): Completable =
        dao.deleteLegacy()
            .subscribeOn(Schedulers.io())
}

private const val USER_ID = 1

private fun UserProfile.toEntity() = UserProfileEntity(
    id = USER_ID,
    name = name,
    gender = gender.toEntity(),
    weight = weight,
    birthday = birthday,
    isDemo = isDemo,
)

private fun UserProfile.Gender.toEntity() = when (this) {
    UserProfile.Gender.MALE -> UserProfileEntity.Gender.MALE
    UserProfile.Gender.FEMALE -> UserProfileEntity.Gender.FEMALE
}

private fun UserProfileEntity.toModel() = UserProfile(
    name = name,
    gender = gender.toModel(),
    weight = weight,
    birthday = birthday,
    isDemo = isDemo,
)

private fun UserProfileEntity.Gender.toModel() = when (this) {
    UserProfileEntity.Gender.MALE -> UserProfile.Gender.MALE
    UserProfileEntity.Gender.FEMALE -> UserProfile.Gender.FEMALE
}