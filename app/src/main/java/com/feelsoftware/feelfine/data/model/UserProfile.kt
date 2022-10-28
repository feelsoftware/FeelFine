package com.feelsoftware.feelfine.data.model

data class UserProfile(
    val name: String,
    val gender: Gender,
    val weight: Int,
    val age: Int,
    val isDemo: Boolean,
) {

    enum class Gender {
        MALE,
        FEMALE
    }
}