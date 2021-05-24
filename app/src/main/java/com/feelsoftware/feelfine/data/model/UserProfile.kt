package com.feelsoftware.feelfine.data.model

data class UserProfile(
    val name: String,
    val gender: Gender,
    val weight: Float,
    val age: Int,
) {

    companion object {
        val EMPTY = UserProfile("", Gender.MALE, 0f, 0)
    }

    enum class Gender {
        MALE,
        FEMALE
    }
}