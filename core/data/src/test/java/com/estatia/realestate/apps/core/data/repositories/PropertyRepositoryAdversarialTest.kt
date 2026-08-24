package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.common.exceptions.PropertyException
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.domain.common.IContentSafetyService
import com.estatia.realestate.apps.core.domain.common.IExceptionTranslator
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import com.estatia.realestate.apps.core.model.engagement.SafetyResult
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import com.estatia.realestate.apps.core.testing.assertions.assertError
import com.estatia.realestate.apps.core.testing.assertions.assertSuccess
import com.estatia.realestate.apps.core.testing.fixtures.PropertyFixtures
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PropertyRepositoryAdversarialTest {

    private lateinit var localDataSource: IPropertyLocalDataSource
    private lateinit var remoteDataSource: IPropertyRemoteDatasource
    private lateinit var userRepository: IUserRepository
    private lateinit var metricsTracker: IMetricsTracker
    private lateinit var engagementRepository: IEngagementRepository
    private lateinit var contentSafetyService: IContentSafetyService
    private lateinit var exceptionTranslator: IExceptionTranslator
    private lateinit var repository: PropertyRepository

    @Before
    fun setup() {
        localDataSource = mockk(relaxed = true)
        remoteDataSource = mockk()
        userRepository = mockk(relaxed = true)
        metricsTracker = mockk(relaxed = true)
        engagementRepository = mockk(relaxed = true)
        contentSafetyService = mockk(relaxed = true)
        exceptionTranslator = mockk(relaxed = true)
        
        repository = PropertyRepository(
            localDataSource,
            remoteDataSource,
            userRepository,
            metricsTracker,
            engagementRepository,
            contentSafetyService,
            exceptionTranslator
        )
    }

    @Test
    fun `uploadProperty blocks and returns error when content safety flags description`() = runTest {
        val property = PropertyFixtures.single().copy(description = "Abusive content")
        
        // 🧪 Chaos: Safety service flags the text
        coEvery { contentSafetyService.validateText(any()) } returns SafetyResult.Flagged("Abusive language detected", 0.99f)

        val result = repository.uploadProperty(property, emptyList(), emptyList())

        val error = result.assertError()
        assert(error is PropertyException.SafetyViolation)
        assertEquals("Description: Abusive language detected", error.message)
        
        // Verify remote upload was NEVER called
        coVerify(exactly = 0) { remoteDataSource.uploadProperty(any(), any(), any(), any()) }
    }

    @Test
    fun `fetchPropertiesPaginated falls back to cache when network times out`() = runTest {
        // 🧪 Chaos: Remote fetch times out
        coEvery { remoteDataSource.fetchPropertiesPaginated(any(), any(), any()) } returns 
            AppResult.Error(NetworkException.Timeout)
        
        // Local cache has data
        val cachedEntities = listOf(mockk<com.estatia.realestate.apps.core.database.entities.PropertyCacheEntity>(relaxed = true))
        coEvery { localDataSource.getCachedProperties() } returns AppResult.Success(cachedEntities)

        val result = repository.fetchPropertiesPaginated(null, null, 20)

        // Then: Result is Success (fallback happened)
        val page = result.assertSuccess()
        assertEquals(1, page.properties.size)
        
        // Verify metrics tracked the timeout
        verify { metricsTracker.trackDuration("property.fetch_paginated.duration", any()) }
    }

    @Test
    fun `fetchLikedProperties uses local user data when remote fails`() = runTest {
        // 🧪 Chaos: Remote fails with 503
        coEvery { remoteDataSource.fetchLikedProperties(any()) } returns AppResult.Error(NetworkException.ServerError(503))
        
        // Local user has liked IDs
        val mockUserData = mockk<com.estatia.realestate.apps.core.model.user.UserData>(relaxed = true) {
            every { likedProperties } returns setOf("liked_1", "liked_2")
        }
        every { userRepository.userData } returns kotlinx.coroutines.flow.flowOf(mockUserData)
        
        // Local cache has the properties
        val cached = listOf(mockk<com.estatia.realestate.apps.core.database.entities.PropertyCacheEntity>(relaxed = true))
        coEvery { localDataSource.getCachedPropertiesByIds(any()) } returns AppResult.Success(cached)

        val result = repository.fetchLikedProperties("user_1")

        // Then: Success via robust offline fallback
        val properties = result.assertSuccess()
        assertEquals(1, properties.size)
    }

    @Test
    fun `fetchPropertiesPaginated serves from cache when not stale and cursor is null`() = runTest {
        // Cache is fresh
        coEvery { localDataSource.isCacheStale(any()) } returns AppResult.Success(false)
        val cached = listOf(mockk<com.estatia.realestate.apps.core.database.entities.PropertyCacheEntity>(relaxed = true))
        coEvery { localDataSource.getCachedProperties() } returns AppResult.Success(cached)

        val result = repository.fetchPropertiesPaginated(null, null, 20)

        // Result from cache
        result.assertSuccess()
        
        // Verify remote was NEVER called (preserving bandwidth)
        coVerify(exactly = 0) { remoteDataSource.fetchPropertiesPaginated(any(), any(), any()) }
    }
}
