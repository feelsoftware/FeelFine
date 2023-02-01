package com.feelsoftware.feelfine.data.model

import java.util.Calendar
import java.util.Date

data class UserProfile(
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

// TODO: fix age calculation
val UserProfile.age: Int
    get() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        calendar.time = birthday
        val birthdayYear = calendar.get(Calendar.YEAR)
        return currentYear - birthdayYear
    }