package com.feelsoftware.feelfine.ui.onboarding.data

import com.feelsoftware.feelfine.data.model.UserProfile
import java.util.Date

internal sealed interface OnboardingStep {
    data class Name(val name: String? = null) : OnboardingStep

    data class Gender(val gender: UserProfile.Gender? = null) : OnboardingStep

    data class Weight(val weight: Float, val range: ClosedRange<Float>) : OnboardingStep

    data class Birthday(
        val birthday: Date? = null,
        val termsAccepted: Boolean = false,
    ) : OnboardingStep
}

internal fun interface OnboardingStepValidator<T : OnboardingStep> {
    /**
     * @return `true` if the [step] is valid and we might show to user next step, `false` otherwise
     */
    fun validate(step: T): Boolean

    object Name : OnboardingStepValidator<OnboardingStep.Name> {
        override fun validate(step: OnboardingStep.Name): Boolean =
            step.name.orEmpty().trim().isEmpty().not()
    }

    object Gender : OnboardingStepValidator<OnboardingStep.Gender> {
        override fun validate(step: OnboardingStep.Gender): Boolean =
            step.gender != null
    }

    object Weight : OnboardingStepValidator<OnboardingStep.Weight> {
        override fun validate(step: OnboardingStep.Weight): Boolean =
            true
    }

    object Birthday : OnboardingStepValidator<OnboardingStep.Birthday> {
        override fun validate(step: OnboardingStep.Birthday): Boolean =
            step.birthday != null && step.termsAccepted
    }
}