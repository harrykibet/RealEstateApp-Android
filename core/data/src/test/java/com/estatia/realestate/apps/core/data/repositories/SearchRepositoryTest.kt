package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.core.database.interfaces.ISearchLocalDataSource
import com.estatia.realestate.apps.core.domain.common.IExceptionTranslator
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import com.estatia.realestate.apps.core.testing.generators.SearchQueryGenerator
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SearchRepositoryTest {

    private lateinit var remoteDataSource: ISearchRemoteDataSource
    private lateinit var searchLocalDataSource: ISearchLocalDataSource
    private lateinit var propertyLocalDataSource: IPropertyLocalDataSource
    private lateinit var engagementRepository: IEngagementRepository
    private lateinit var metricsTracker: IMetricsTracker
    private lateinit var exceptionTranslator: IExceptionTranslator
    private lateinit var repository: SearchRepository

    @Before
    fun setup() {
        remoteDataSource = mockk()
        searchLocalDataSource = mockk(relaxed = true)
        propertyLocalDataSource = mockk(relaxed = true)
        engagementRepository = mockk(relaxed = true)
        metricsTracker = mockk(relaxed = true)
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
    fun `searchProperties saves query in history`() = runTest {
        val query = SearchQueryGenerator.generate()
        coEvery { remoteDataSource.searchProperties(any(), any()) } returns AppResult.Success(emptyList())

        repository.searchProperties(query, 20)

        coVerify { searchLocalDataSource.saveSearchQuery(query) }
    }
}
