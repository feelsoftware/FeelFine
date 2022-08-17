package com.feelsoftware.feelfine.fit

import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType

interface SignInOptions {

    private val fitnessOptions: FitnessOptions
        get() = FitnessOptions.builder()
            // Activities
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_READ)
            // Steps
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            // Sleep
            .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
            .build()

    val signInOptions: GoogleSignInOptionsExtension
        get() = OptionsWrapper(fitnessOptions)

    val logoutOptions: GoogleSignInOptions
        get() = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
}

private class OptionsWrapper(
    private val fitnessOptions: FitnessOptions
) : GoogleSignInOptionsExtension {

    override fun getExtensionType(): Int = fitnessOptions.extensionType

    override fun toBundle(): Bundle = fitnessOptions.toBundle()

    override fun getImpliedScopes(): List<Scope> = listOf(
        Scope(Scopes.PROFILE),
        Scope(Scopes.EMAIL),
    ) + fitnessOptions.impliedScopes
}