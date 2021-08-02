package com.feelsoftware.feelfine.data.repository

import com.feelsoftware.feelfine.data.db.dao.UserProfileDao
import com.feelsoftware.feelfine.data.db.entity.UserProfileEntity
import com.feelsoftware.feelfine.data.model.UserProfile
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class UserRepository(
    private val dao: UserProfileDao,
) {

    private val emptyProfile = UserProfile("", UserProfile.Gender.MALE, 0, 0)

    fun setProfile(profile: UserProfile): Completable =
        dao.insert(profile.toEntity())
            .subscribeOn(Schedulers.io())

    fun hasProfile(): Observable<Boolean> =
        getProfile()
            .map {
                it != emptyProfile
            }

    fun getProfile(): Observable<UserProfile> =
        dao.get()
            .map {
                it.firstOrNull()?.toModel() ?: emptyProfile
            }
            .subscribeOn(Schedulers.io())

    fun clear(): Completable =
        dao.delete()
            .subscribeOn(Schedulers.io())
}

private const val USER_ID = 1

private fun UserProfile.toEntity() = UserProfileEntity(
    id = USER_ID,
    name = name,
    gender = gender.toEntity(),
    weight = weight,
    age = age,
)

private fun UserProfile.Gender.toEntity() = when (this) {
    UserProfile.Gender.MALE -> UserProfileEntity.Gender.MALE
    UserProfile.Gender.FEMALE -> UserProfileEntity.Gender.FEMALE
}

private fun UserProfileEntity.toModel() = UserProfile(
    name = name,
    gender = gender.toModel(),
    weight = weight,
    age = age,
)

private fun UserProfileEntity.Gender.toModel() = when (this) {
    UserProfileEntity.Gender.MALE -> UserProfile.Gender.MALE
    UserProfileEntity.Gender.FEMALE -> UserProfile.Gender.FEMALE
}