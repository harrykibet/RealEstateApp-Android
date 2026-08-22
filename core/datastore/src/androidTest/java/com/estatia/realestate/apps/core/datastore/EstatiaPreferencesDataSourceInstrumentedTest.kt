package com.estatia.realestate.apps.core.datastore

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class EstatiaPreferencesDataSourceInstrumentedTest {

    private lateinit var testScope: CoroutineScope
    private lateinit var dataSource: EstatiaPreferencesDataSource
    private lateinit var testFile: File

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        testScope = CoroutineScope(Dispatchers.IO + Job())
        testFile = context.dataStoreFile("test_user_preferences.pb")
        
        val dataStore = DataStoreFactory.create(
            serializer = UserPreferencesSerializer(),
            scope = testScope
        ) { testFile }
        
        dataSource = EstatiaPreferencesDataSource(dataStore)
    }

    @After
    fun cleanup() {
        testScope.cancel()
        testFile.delete()
    }

    @Test
    fun setPropertyLiked_updatesUserData() = runBlocking {
        dataSource.setPropertyLiked("prop_1", true)
        val userData = dataSource.userData.first()
        assertEquals(setOf("prop_1"), userData.likedProperties)
        
        dataSource.setPropertyLiked("prop_1", false)
        val updatedData = dataSource.userData.first()
        assertEquals(emptySet<String>(), updatedData.likedProperties)
    }

    @Test
    fun setIsMuted_updatesUserData() = runBlocking {
        dataSource.setIsMuted(true)
        val userData = dataSource.userData.first()
        assertEquals(true, userData.isMuted)
    }
}
