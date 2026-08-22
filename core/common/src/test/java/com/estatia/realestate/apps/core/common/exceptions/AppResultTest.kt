package com.estatia.realestate.apps.core.common.exceptions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppResultTest {

    @Test
    fun `map transforms success value`() {
        val result: AppResult<Int> = AppResult.Success(10)
        val mapped = result.map { it * 2 }
        
        assertTrue(mapped is AppResult.Success)
        assertEquals(20, (mapped as AppResult.Success).data)
    }

    @Test
    fun `map propagates error`() {
        val exception = RemoteServiceException.Unknown(Exception("Error"))
        val result: AppResult<Int> = AppResult.Error(exception)
        val mapped = result.map { it * 2 }
        
        assertTrue(mapped is AppResult.Error)
        assertEquals(exception, (mapped as AppResult.Error).exception)
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

    @Test
    fun `getOrNull returns data on success`() {
        val result = AppResult.Success("test")
        assertEquals("test", result.getOrNull())
    }

    @Test
    fun `getOrNull returns null on error`() {
        val result = AppResult.Error(RemoteServiceException.Unknown(Exception("boom")))
        assertNull(result.getOrNull())
    }
}
