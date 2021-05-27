package com.feelsoftware.feelfine.data.model

data class UserProfile(
    val name: String,
    val gender: Gender,
    val weight: Int,
    val age: Int,
) {

    companion object {
        val EMPTY = UserProfile("", Gender.MALE, 0, 0)
    }

    enum class Gender {
        MALE,
        FEMALE
    }
}