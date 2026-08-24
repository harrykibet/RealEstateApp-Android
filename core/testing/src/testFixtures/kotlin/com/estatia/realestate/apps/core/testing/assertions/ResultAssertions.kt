package com.estatia.realestate.apps.core.testing.assertions

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import org.junit.Assert.assertTrue
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Asserts that the [AppResult] is a Success and returns the data.
 */
@OptIn(ExperimentalContracts::class)
fun <T> AppResult<T>.assertSuccess(): T {
    contract {
        returns() implies (this@assertSuccess is AppResult.Success<T>)
    }
    assertTrue("Expected Success, but got $this", this is AppResult.Success)
    return (this as AppResult.Success).data
}

/**
 * Asserts that the [AppResult] is an Error and returns the exception.
 */
@OptIn(ExperimentalContracts::class)
fun <T> AppResult<T>.assertError(): com.estatia.realestate.apps.core.common.exceptions.AppException {
    contract {
        returns() implies (this@assertError is AppResult.Error)
    }
    assertTrue("Expected Error, but got $this", this is AppResult.Error)
    return (this as AppResult.Error).exception
}
