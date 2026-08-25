package com.estatia.realestate.apps.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.estatia.realestate.apps.core.database.PropertyDatabase
import com.estatia.realestate.apps.core.database.entities.PropertyCacheEntity
import com.estatia.realestate.apps.core.testing.clock.TestClock
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PropertyDaoTest {

    private lateinit var propertyCacheDao: PropertyCacheDao
    private lateinit var db: PropertyDatabase
    private lateinit var testClock: TestClock

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, PropertyDatabase::class.java
        ).build()
        propertyCacheDao = db.propertyCacheDao()
        testClock = TestClock(System.currentTimeMillis())
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun createTestProperty(id: String, timestamp: Long = testClock.currentTimeMillis()) = PropertyCacheEntity(
        id = id,
        title = "Modern Apartment",
        description = "A beautiful apartment in the city center",
        price = 500000.0,
        imageUrls = "[]",
        directVideoUrls = "[]",
        hlsUrls = "[]",
        videosAvailable = false,
        latitude = 40.7128,
        longitude = -74.0060,
        createdAt = timestamp,
        ownerId = "owner123",
        ownerName = "John Doe",
        contactPhone = "123456789",
        contactEmail = "john@example.com",
        county = "New York",
        active = true,
        viewsCount = 100,
        likesCount = 50,
        commentsCount = 10,
        sharesCount = 5
    )

    @Test
    fun daoHandlesVeryLargeDatasetScenariosSuccessfully() = runBlocking {
        val properties = (1..500).map { createTestProperty(it.toString()) }
        propertyCacheDao.insertAll(properties)
        
        val all = propertyCacheDao.getAll()
        assertEquals(500, all.size)
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadProperty() = runBlocking {
        val property = createTestProperty("1")
        propertyCacheDao.insertAll(listOf(property))
        val byId = propertyCacheDao.getById("1")
        assertEquals(property, byId)
    }

    @Test
    @Throws(Exception::class)
    fun clearAllProperties() = runBlocking {
        val property = createTestProperty("1")
        propertyCacheDao.insertAll(listOf(property))
        propertyCacheDao.clearAll()
        val all = propertyCacheDao.getAll()
        assertEquals(0, all.size)
    }
}
