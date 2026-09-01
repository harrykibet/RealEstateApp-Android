package com.estatia.realestate.apps.core.testing_network.fake.source

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.network.db_entities.PropertyContactEntity
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.testing.chaos.database.DatabaseBehavior
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakePropertyRemoteDataSourceTest {

    private lateinit var dataSource: FakePropertyRemoteDataSource

    @Before
    fun setup() {
        dataSource = FakePropertyRemoteDataSource()
    }

    @Test
    fun `upload and get property success`() = runTest {
        val property = createMockProperty("id_1")
        val contact = PropertyContactEntity("user_1", "Agent", null, null)

        val result = dataSource.uploadProperty(property, contact, emptyList(), emptyList())
        assertTrue(result is AppResult.Success)
        val id = (result as AppResult.Success).data
        assertEquals("id_1", id)

        val getResult = dataSource.getPropertyById(id)
        assertTrue(getResult is AppResult.Success)
        assertEquals("id_1", (getResult as AppResult.Success).data.id)
    }

    @Test
    fun `fetchPropertiesPaginated correctly handles pages and cursors`() = runTest {
        // Given: 5 properties with sequential timestamps
        (1..5).forEach { i ->
            dataSource.uploadProperty(
                createMockProperty("id_$i", createdAt = i.toLong()),
                PropertyContactEntity("u", "A", null, null),
                emptyList(), emptyList()
            )
        }

        // When: Fetch first page of 2 (Sorted by descending createdAt: 5, 4)
        val page1Result = dataSource.fetchPropertiesPaginated(null, null, 2)
        val page1 = (page1Result as AppResult.Success).data
        assertEquals(2, page1.items.size)
        assertEquals("id_5", page1.items[0].id)
        assertEquals("id_4", page1.items[1].id)
        assertNotNull(page1.nextCursor)
        assertEquals(4L, page1.nextCursor?.timestamp)

        // When: Fetch second page using cursor
        val page2Result = dataSource.fetchPropertiesPaginated(null, page1.nextCursor, 2)
        val page2 = (page2Result as AppResult.Success).data
        assertEquals(2, page2.items.size)
        assertEquals("id_3", page2.items[0].id)
        assertEquals("id_2", page2.items[1].id)
        assertNotNull(page2.nextCursor)

        // When: Fetch last page
        val page3Result = dataSource.fetchPropertiesPaginated(null, page2.nextCursor, 2)
        val page3 = (page3Result as AppResult.Success).data
        assertEquals(1, page3.items.size)
        assertEquals("id_1", page3.items[0].id)
        assertNull("Cursor should be null on last partial page", page3.nextCursor)
    }

    @Test
    fun `setNextBehavior injects chaos and auto-resets`() = runTest {
        dataSource.setNextBehavior(DatabaseBehavior.Unavailable)

        val result = dataSource.getPropertyById("any")
        assertTrue(result is AppResult.Error)
        assertTrue((result as AppResult.Error).exception is DatabaseException.Unavailable)

        // Verifies auto-reset to Success
        val nextResult = dataSource.getPropertyById("any")
        assertTrue("Behavior should reset to Success after one use", nextResult is AppResult.Error)
        // (Still Error because ID not found, but NOT DatabaseException.Unavailable)
        assertTrue((nextResult as AppResult.Error).exception is DatabaseException.NotFound)
    }

    private fun createMockProperty(id: String, createdAt: Long = 0L) = PropertyEntityModel(
        id = id,
        title = "Title $id",
        description = "Desc",
        createdAt = createdAt,
        category = "HOUSE",
        price = 100.0,
        address = "Address",
        latitude = 0.0,
        longitude = 0.0,
        thumbnailUrl = "url",
        images = emptyList(),
        videos = emptyList(),
        amenities = emptyList()
    )
}
