package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.core.database.interfaces.ISearchLocalDataSource
import com.estatia.realestate.apps.core.domain.common.IExceptionTranslator
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import com.estatia.realestate.apps.core.testing.coroutine.TestScheduler
import com.estatia.realestate.apps.core.testing.coroutine.runConcurrent
import io.mockk.*
import kotlinx.coroutines.delay
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
        exceptionTranslator = mockk(relaxed = true)
        
        repository = SearchRepository(
            remoteDataSource,
            searchLocalDataSource,
            propertyLocalDataSource,
            engagementRepository,
            exceptionTranslator
        )
    }

    @Test
    fun `concurrent searches maintain cache integrity using scheduler`() = runTest {
        val query1 = "Nairobi"
        val query2 = "Mombasa"
        
        coEvery { remoteDataSource.searchProperties(query1, any()) } coAnswers {
            scheduler.awaitPoint("query1_fetching")
            AppResult.Success(emptyList())
        }
        
        coEvery { remoteDataSource.searchProperties(query2, any()) } coAnswers {
            AppResult.Success(emptyList())
        }

        runConcurrent(
            { repository.searchProperties(query1, 20) },
            { 
                delay(10.milliseconds) 
                repository.searchProperties(query2, 20) 
            }
        )

        scheduler.release("query1_fetching")

        coVerify(exactly = 1) { searchLocalDataSource.saveSearchQuery(query1) }
        coVerify(exactly = 1) { searchLocalDataSource.saveSearchQuery(query2) }
        
        coVerify(exactly = 1) { searchLocalDataSource.cacheSearchResult(query1, any()) }
        coVerify(exactly = 1) { searchLocalDataSource.cacheSearchResult(query2, any()) }
    }
}
