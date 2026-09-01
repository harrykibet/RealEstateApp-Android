package com.estatia.realestate.apps.core.testing_network.fake.source

import app.cash.turbine.test
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.testing.chaos.auth.AuthBehavior
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeAuthRemoteDataSourceTest {

    private lateinit var dataSource: FakeAuthRemoteDataSource

    @Before
    fun setup() {
        dataSource = FakeAuthRemoteDataSource()
    }

    @Test
    fun `signInWithEmail updates authenticated state and user`() = runTest {
        dataSource.isUserAuthenticated().test {
            assertEquals(false, awaitItem())

            val email = "test@estatia.com"
            val result = dataSource.signInWithEmail(email, "pass")
            
            assertTrue(result is AppResult.Success)
            val user = (result as AppResult.Success).data
            assertEquals(email, user.email)
            assertEquals(true, awaitItem())
            assertEquals(user.userId, dataSource.getCurrentUserId())
        }
    }

    @Test
    fun `signOut clears authenticated state`() = runTest {
        dataSource.signInWithEmail("test@test.com", "pass")
        
        dataSource.isUserAuthenticated().test {
            assertEquals(true, awaitItem())

            dataSource.signOut()
            
            assertEquals(false, awaitItem())
            assertNull(dataSource.getCurrentUserId())
        }
    }

    @Test(expected = AuthException.SessionExpired::class)
    fun `setNextBehavior TokenExpired throws SessionExpired`() = runTest {
        dataSource.setNextBehavior(AuthBehavior.TokenExpired)
        dataSource.signInWithEmail("test@test.com", "pass")
    }

    @Test
    fun `chaos behavior auto-resets after throw`() = runTest {
        dataSource.setNextBehavior(AuthBehavior.TokenExpired)
        
        try {
            dataSource.signInWithEmail("test@test.com", "pass")
        } catch (_: AuthException.SessionExpired) { }

        val result = dataSource.signInWithEmail("test@test.com", "pass")
        assertTrue(result is AppResult.Success)
    }
}
