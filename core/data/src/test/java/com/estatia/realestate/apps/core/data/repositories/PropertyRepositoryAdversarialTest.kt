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
import com.estatia.realestate.apps.core.data.mappers.remote.RemotePropertyMapper
import com.estatia.realestate.apps.core.testing.assertions.assertError
import com.estatia.realestate.apps.core.testing.assertions.assertSuccess
import com.estatia.realestate.apps.core.testing.fake.source.FakePropertyRemoteDataSource
import com.estatia.realestate.apps.core.testing.fixtures.PropertyFixtures
import com.estatia.realestate.apps.core.testing.chaos.database.DatabaseBehavior
import com.estatia.realestate.apps.core.testing.coroutine.TestScheduler
import com.estatia.realestate.apps.core.testing.lifecycle.launchAndDestroy
import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

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

    @Test
    fun `uploadProperty propagates cancellation mid-flight using test scheduler`() = runTest {
        val property = PropertyFixtures.single()
        val scheduler = TestScheduler()
        
        // Use a mock for this test to control suspension
        val mockRemote = mockk<IPropertyRemoteDatasource>()
        val adversarialRepo = PropertyRepository(
            localDataSource, mockRemote, userRepository, metricsTracker, 
            engagementRepository, contentSafetyService, exceptionTranslator
        )

        coEvery { mockRemote.uploadProperty(any(), any(), any(), any()) } coAnswers {
            scheduler.release("reached_remote")
            delay(10.seconds) // Hang
            AppResult.Success("prop_id")
        }

        launchAndDestroy(scheduler, "reached_remote") {
            adversarialRepo.uploadProperty(property, emptyList(), emptyList())
        }
        
        // Verify we reached the remote call before cancellation
        coVerify { mockRemote.uploadProperty(any(), any(), any(), any()) }
    }

    @Test
    fun `fetchPropertiesPaginated honors pageSize and cursor in fake implementation`() = runTest {
        // Given: 10 properties in the fake source
        repeat(10) { i ->
            val p = PropertyFixtures.build(id = "p$i").copy(createdAt = 1000L + i)
            fakeRemoteSource.uploadProperty(
                RemotePropertyMapper.toEntity(p),
                mockk(relaxed = true),
                emptyList(),
                emptyList()
            )
        }

        // When: Fetch first page of 3
        val page1 = repository.fetchPropertiesPaginated(null, null, 3).assertSuccess()
        
        // Then: 3 items, has cursor
        assertEquals(3, page1.properties.size)
        assertNotNull(page1.cursor)

        // When: Fetch second page using cursor from first page
        val page2 = repository.fetchPropertiesPaginated(null, page1.cursor, 3).assertSuccess()
        
        // Then: 3 more items, no overlap with page 1
        assertEquals(3, page2.properties.size)
        val p1Ids = page1.properties.map { it.id.value }.toSet()
        val p2Ids = page2.properties.map { it.id.value }.toSet()
        assertTrue("Pages should not overlap", p1Ids.intersect(p2Ids).isEmpty())
    }
}
