package com.feelsoftware.feelfine.data.model

class Optional<T> private constructor(
    private var _value: T?
) {

    companion object {

        fun <T> empty() = Optional<T>(null)

        fun <T> of(value: T) = Optional(value)

        fun <T> ofNullable(value: T?) = Optional(value)
    }

    val value: T
        get() = _value ?: throw NoSuchElementException("No value present")

    val isPresent: Boolean
        get() = _value != null
}
