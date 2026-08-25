package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.core.database.interfaces.ISearchLocalDataSource
import com.estatia.realestate.apps.core.domain.common.IExceptionTranslator
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import com.estatia.realestate.apps.core.testing.coroutine.TestScheduler
import com.estatia.realestate.apps.core.testing.coroutine.runConcurrent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class SearchRepositoryConcurrencyTest {

    private lateinit var remoteDataSource: ISearchRemoteDataSource
    private lateinit var searchLocalDataSource: ISearchLocalDataSource
    private lateinit var propertyLocalDataSource: IPropertyLocalDataSource
    private lateinit var engagementRepository: IEngagementRepository
    private lateinit var exceptionTranslator: IExceptionTranslator
    private lateinit var repository: SearchRepository
    private val scheduler = TestScheduler()

    @Before
    fun setup() {
        remoteDataSource = mockk()
        searchLocalDataSource = mockk(relaxed = true)
        propertyLocalDataSource = mockk(relaxed = true)
        engagementRepository = mockk(relaxed = true)
        val metricsTracker = mockk<com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker>(relaxed = true)
        exceptionTranslator = mockk(relaxed = true)
        
        repository = SearchRepository(
            remoteDataSource,
            searchLocalDataSource,
            propertyLocalDataSource,
            engagementRepository,
            metricsTracker,
            exceptionTranslator
        )
    }

    @Test
    fun `concurrent searches maintain cache integrity using specialized concurrent runner`() = runTest {
        val query1 = "Nairobi"
        val query2 = "Mombasa"
        
        coEvery { remoteDataSource.searchProperties(query1, any()) } coAnswers {
            AppResult.Success(emptyList())
        }
        
        coEvery { remoteDataSource.searchProperties(query2, any()) } coAnswers {
            AppResult.Success(emptyList())
        }

        launch {
            delay(10.milliseconds)
            scheduler.release("query1_start")
        }

        runConcurrent(
            first = { repository.searchProperties(query1, 20) },
            second = { repository.searchProperties(query2, 20) },
            scheduler = scheduler,
            synchronizationPoint = "query1_start"
        )

        coVerify(exactly = 1) { searchLocalDataSource.saveSearchQuery(query1) }
        coVerify(exactly = 1) { searchLocalDataSource.saveSearchQuery(query2) }
        
        coVerify(exactly = 1) { searchLocalDataSource.cacheSearchResult(query1, any()) }
        coVerify(exactly = 1) { searchLocalDataSource.cacheSearchResult(query2, any()) }
    }
}
