package com.estatia.realestate.apps.core.testing_network.fake.source

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.testing.chaos.database.DatabaseBehavior
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeSearchRemoteDataSourceTest {

    private lateinit var dataSource: FakeSearchRemoteDataSource

    @Before
    fun setup() {
        dataSource = FakeSearchRemoteDataSource()
    }

    @Test
    fun `searchProperties filters by title and description`() = runTest {
        // Given
        dataSource.addProperty(createMockProperty("id_1", title = "Luxury Villa"))
        dataSource.addProperty(createMockProperty("id_2", description = "Cozy apartment in city"))
        dataSource.addProperty(createMockProperty("id_3", title = "Office Space"))

        // When: Search for "villa"
        val villaResult = dataSource.searchProperties("villa", 10)
        assertEquals(1, (villaResult as AppResult.Success).data.size)
        assertEquals("id_1", villaResult.data[0].id)

        // When: Search for "city"
        val cityResult = dataSource.searchProperties("city", 10)
        assertEquals(1, (cityResult as AppResult.Success).data.size)
        assertEquals("id_2", cityResult.data[0].id)

        // When: Search for something non-existent
        val noneResult = dataSource.searchProperties("castle", 10)
        assertEquals(0, (noneResult as AppResult.Success).data.size)
    }

    @Test
    fun `searchProperties respects limit`() = runTest {
        (1..10).forEach { i ->
            dataSource.addProperty(createMockProperty("id_$i", title = "Property $i"))
        }

        val result = dataSource.searchProperties("Property", 3)
        assertEquals(3, (result as AppResult.Success).data.size)
    }

    @Test
    fun `chaos behavior correctly maps and resets`() = runTest {
        dataSource.setNextBehavior(DatabaseBehavior.Locked)

        val result = dataSource.searchProperties("query", 10)
        assertTrue(result is AppResult.Error)
        val exception = (result as AppResult.Error).exception
        assertTrue(exception is DatabaseException.Unknown)
        assertTrue((exception as DatabaseException.Unknown).original.message!!.contains("locked"))

        // Verifies reset
        val nextResult = dataSource.searchProperties("query", 10)
        assertTrue(nextResult is AppResult.Success)
    }

    private fun createMockProperty(id: String, title: String = "", description: String = "") = PropertyEntityModel(
        id = id,
        title = title,
        description = description
    )
}
