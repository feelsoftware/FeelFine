package com.feelsoftware.feelfine.ui.onboarding

import com.feelsoftware.feelfine.data.model.UserProfile
import java.util.Date

sealed interface OnboardingStep {
    data class Name(val name: String? = null) : OnboardingStep

    data class Gender(val gender: UserProfile.Gender? = null) : OnboardingStep

    data class Weight(val weight: Float? = null) : OnboardingStep

    data class Birthday(val birthday: Date? = null) : OnboardingStep
}