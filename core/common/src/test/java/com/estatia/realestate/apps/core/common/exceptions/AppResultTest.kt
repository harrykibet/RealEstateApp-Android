package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.testing.assertions.assertError
import com.estatia.realestate.apps.core.testing.assertions.assertSuccess
import org.junit.Assert.assertEquals
import org.junit.Test

class AppResultTest {

    @Test
    fun `map transforms success value using platform assertions`() {
        val result: AppResult<Int> = AppResult.Success(10)
        
        val mapped = result.map { it * 2 }
        
        val data = mapped.assertSuccess()
        assertEquals(20, data)
    }

    @Test
    fun `map propagates error using platform assertions`() {
        val exception = RemoteServiceException.Unknown(Exception("Error"))
        val result: AppResult<Int> = AppResult.Error(exception)
        
        val mapped = result.map { it * 2 }
        
        val err = mapped.assertError()
        assertEquals(exception, err)
    }

    @Test
    fun `fold calls onSuccess for success`() {
        val result: AppResult<String> = AppResult.Success("data")
        val output = result.fold(
            onSuccess = { "Success: $it" },
            onError = { "Error: ${it.message}" }
        )
        
        assertEquals("Success: data", output)
    }

    @Test
    fun `fold calls onError for error`() {
        val result: AppResult<String> = AppResult.Error(RemoteServiceException.Unknown(Exception("fail")))
        val output = result.fold(
            onSuccess = { "Success: $it" },
            onError = { "Error: ${it.message}" }
        )
        
        assertEquals("Error: fail", output)
    }

    @Test
    fun `getOrThrow returns data on success`() {
        val result = AppResult.Success("test")
        assertEquals("test", result.getOrThrow())
    }

    @Test(expected = RemoteServiceException.Unknown::class)
    fun `getOrThrow throws on error`() {
        val result = AppResult.Error(RemoteServiceException.Unknown(Exception("boom")))
        result.getOrThrow()
    }
}
