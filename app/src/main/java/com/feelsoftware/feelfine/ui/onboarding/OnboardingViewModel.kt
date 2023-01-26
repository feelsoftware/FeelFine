@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class OnboardingViewModel : ViewModel() {

    private val steps = mutableListOf<OnboardingStep>(
        OnboardingStep.Name(),
        OnboardingStep.Gender(),
        OnboardingStep.Weight(),
        OnboardingStep.Birthday(),
    )
    private val currentStep = MutableStateFlow<OnboardingStep>(steps.first())

    val step: StateFlow<OnboardingStep> = currentStep.asStateFlow()

    val backHandlerEnabled: StateFlow<Boolean> = currentStep.map { it !is OnboardingStep.Name }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    private val _finishOnboarding: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val onFinish: StateFlow<Boolean> = _finishOnboarding.asStateFlow()

    fun previousStep() {
        val index = steps.indexOf(currentStep.value)
        if (index <= 0) return
        currentStep.value = steps[index - 1]
    }

    fun nextStep() {
        val index = steps.indexOf(currentStep.value)
        if (index == steps.size - 1) {
            _finishOnboarding.value = true
        } else {
            currentStep.value = steps[index + 1]
        }
    }
}