@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feelsoftware.feelfine.data.model.UserProfile.Gender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val WEIGHT_NONE = 0f

internal class OnboardingViewModel : ViewModel() {

    private val steps = mutableListOf<OnboardingStep>(
        OnboardingStep.Name(),
        OnboardingStep.Gender(),
        OnboardingStep.Weight(weight = WEIGHT_NONE, range = 1f..635f),
        OnboardingStep.Birthday(),
    )
    private val validators = mutableListOf<OnboardingStepValidator<*>>(
        OnboardingStepValidator.Name,
        OnboardingStepValidator.Gender,
        OnboardingStepValidator.Weight,
        OnboardingStepValidator.Birthday,
    )
    private val currentStep = MutableStateFlow<OnboardingStep>(steps.first())

    val step: StateFlow<OnboardingStep> = currentStep.asStateFlow()

    private val _nextStepEnabled = MutableStateFlow<Boolean>(false)
    val nextStepEnabled: StateFlow<Boolean> = _nextStepEnabled.asStateFlow()

    val stepIndex: StateFlow<Int> = currentStep.map { steps.indexOf(it) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = -1
        )
    val stepsCount: StateFlow<Int> = MutableStateFlow(4).asStateFlow()

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
        validateCurrentStep()
    }

    fun nextStep() {
        val index = steps.indexOf(currentStep.value)
        if (index == steps.size - 1) {
            _finishOnboarding.value = true
        } else if (nextStepEnabled.value) {
            currentStep.value = steps[index + 1]
            validateCurrentStep()
        }
    }

    fun updateStepData(step: OnboardingStep) {
        steps[stepIndex.value] = step
        currentStep.value = step
        validateCurrentStep()

        if (step is OnboardingStep.Gender) {
            // Update default weight based on gender
            val weightData = steps.filterIsInstance<OnboardingStep.Weight>().first()
                .takeIf { it.weight == WEIGHT_NONE } ?: return
            val weightStep = steps.indexOfFirst { it is OnboardingStep.Weight }
                .takeIf { it != -1 } ?: return
            steps[weightStep] = weightData.copy(
                weight = if (step.gender == Gender.MALE) 65f else 55f
            )
        }
    }

    private fun validateCurrentStep() {
        _nextStepEnabled.value = validators.validate(currentStep.value)
    }
}

private inline fun <reified T : OnboardingStep> List<OnboardingStepValidator<*>>.validate(
    step: T
): Boolean = when (step) {
    is OnboardingStep.Name ->
        find<OnboardingStepValidator.Name>().validate(step)
    is OnboardingStep.Gender ->
        find<OnboardingStepValidator.Gender>().validate(step)
    is OnboardingStep.Weight ->
        find<OnboardingStepValidator.Weight>().validate(step)
    is OnboardingStep.Birthday ->
        find<OnboardingStepValidator.Birthday>().validate(step)
    else ->
        throw IllegalArgumentException("Unsupported step $this, only 4 steps are supported")
}

private inline fun <reified T : OnboardingStepValidator<*>> List<OnboardingStepValidator<*>>.find(): T =
    filterIsInstance<T>().first()