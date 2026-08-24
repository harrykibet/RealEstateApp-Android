package com.estatia.realestate.apps.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.estatia.realestate.apps.core.database.SearchDatabase
import com.estatia.realestate.apps.core.database.entities.SearchHistoryEntity
import com.estatia.realestate.apps.core.testing.clock.TestClock
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SearchHistoryDaoTest {

    private lateinit var searchHistoryDao: SearchHistoryDao
    private lateinit var db: SearchDatabase
    private val testClock = TestClock(System.currentTimeMillis())

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, SearchDatabase::class.java
        ).build()
        searchHistoryDao = db.searchHistoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeAndReadSearchQuery() = runBlocking {
        val query = SearchHistoryEntity(id = 1, query = "Nairobi", timestamp = testClock.currentTimeMillis())
        searchHistoryDao.insertSearchQuery(query)
        val history = searchHistoryDao.getSearchHistory()
        assertEquals(1, history.size)
        assertEquals("Nairobi", history[0].query)
    }

    @Test
    fun maintainSearchHistoryLimit() = runBlocking {
        for (i in 1..15) {
            testClock.advanceBy(1000L)
            searchHistoryDao.insertSearchQuery(
                SearchHistoryEntity(id = i, query = "Query $i", timestamp = testClock.currentTimeMillis())
            )
        }
        searchHistoryDao.maintainSearchHistoryLimit()
        val history = searchHistoryDao.getSearchHistory()
        assertEquals(10, history.size)
        assertEquals("Query 15", history[0].query)
    }
}
