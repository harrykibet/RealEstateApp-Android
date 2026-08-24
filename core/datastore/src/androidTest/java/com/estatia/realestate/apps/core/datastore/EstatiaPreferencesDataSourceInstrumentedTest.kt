package com.estatia.realestate.apps.core.datastore

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.estatia.realestate.apps.core.testing.assertions.assertEmits
import com.estatia.realestate.apps.core.testing.assertions.assertFirst
import io.mockk.mockk
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
        
        val metricsTracker = mockk<com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker>(relaxed = true)
        val logger = mockk<com.estatia.realestate.apps.core.common.interfaces.ILogger>(relaxed = true)
        dataSource = EstatiaPreferencesDataSource(dataStore, metricsTracker, logger)
    }

    @After
    fun cleanup() {
        testScope.cancel()
        testFile.delete()
    }

    @Test
    fun setPropertyLiked_updatesUserData() = runBlocking {
        dataSource.setPropertyLiked("prop_1", true)
        dataSource.userData.assertEmits { it.likedProperties.contains("prop_1") }
        
        dataSource.setPropertyLiked("prop_1", false)
        dataSource.userData.assertEmits { it.likedProperties.isEmpty() }
    }

    @Test
    fun setIsMuted_updatesUserData() = runBlocking {
        dataSource.setIsMuted(true)
        dataSource.userData.assertEmits { it.isMuted }
    }
}
