package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.testing.fixtures.AuthFixtures
import com.estatia.realestate.apps.core.network.db_entities.NetworkUserEntity
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var remoteDataSource: IAuthRemoteDataSource
    private lateinit var metricsTracker: IMetricsTracker
    private lateinit var repository: AuthRepository

    @Before
    fun setup() {
        remoteDataSource = mockk()
        metricsTracker = mockk(relaxed = true)
        repository = AuthRepository(remoteDataSource, metricsTracker)
    }

    @Test
    fun `getCurrentUser returns mapped domain user from remote source`() {
        val fixtureUser = AuthFixtures.authenticatedUser()
        
        every { remoteDataSource.getCurrentUser() } returns NetworkUserEntity(
            userId = fixtureUser.userId,
            displayName = fixtureUser.displayName,
            email = fixtureUser.email,
            phoneNumber = fixtureUser.phoneNumber,
            photoUrl = fixtureUser.photoUrl,
            isEmailVerified = fixtureUser.isEmailVerified
        )

        val result = repository.getCurrentUser()
        
        assertEquals(fixtureUser.displayName, result?.displayName)
        assertEquals(fixtureUser.userId, result?.userId)
    }
}
