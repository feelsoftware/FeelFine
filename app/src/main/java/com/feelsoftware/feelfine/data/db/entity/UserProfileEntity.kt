package com.feelsoftware.feelfine.data.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Entity(
    tableName = "user_profile"
)
@TypeConverters(UserProfileGenderConverter::class)
@Keep
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val gender: Gender,
    val weight: Float,
    val birthday: Date,
    val isDemo: Boolean,
) {

    enum class Gender {
        MALE,
        FEMALE
    }
}

class UserProfileGenderConverter {

    @TypeConverter
    fun toRaw(gender: UserProfileEntity.Gender): Int = gender.ordinal

    @TypeConverter
    fun toGender(raw: Int) = UserProfileEntity.Gender.values()[raw]
}