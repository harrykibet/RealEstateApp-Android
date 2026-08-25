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
import com.estatia.realestate.apps.core.testing.fake.source.FakePropertyRemoteDataSource
import com.estatia.realestate.apps.core.testing.fixtures.PropertyFixtures
import com.estatia.realestate.apps.core.testing.chaos.database.DatabaseBehavior
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

    private val fakeRemoteSource get() = remoteDataSource as FakePropertyRemoteDataSource

    @Before
    fun setup() {
        localDataSource = mockk(relaxed = true)
        remoteDataSource = FakePropertyRemoteDataSource()
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
    }

    @Test
    fun `fetchLikedProperties uses local user data when remote fails via fake chaos`() = runTest {
        // 🧪 Chaos Scenario: Remote fails with Unavailable
        fakeRemoteSource.setNextBehavior(DatabaseBehavior.Unavailable)
        
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
}
