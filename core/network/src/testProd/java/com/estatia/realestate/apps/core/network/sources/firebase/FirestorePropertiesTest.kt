package com.estatia.realestate.apps.core.network.sources.firebase

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.PROPERTIES
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.SubCollections.LIKED_PROPERTIES
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.USERS
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.testing.assertions.assertSuccess
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FirestorePropertiesTest {

    private lateinit var database: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var networkClient: INetworkClient
    private lateinit var firestoreProperties: FirestoreProperties
    private val chaosController = NetworkChaosController()

    @Before
    fun setup() {
        database = mockk()
        storage = mockk()
        networkClient = mockk()
        firestoreProperties = FirestoreProperties(database, storage, networkClient)

        coEvery {
            networkClient.execute<Any?>(any(), any())
        } coAnswers {
            val apiCall = secondArg<suspend () -> Any?>()
            try {
                AppResult.Success(apiCall())
            } catch (e: Exception) {
                AppResult.Error(NetworkException.Unknown(e))
            }
        }
    }

    @Test
    fun `fetchLikedProperties handles transient network timeouts`() = runTest {
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        
        // 🧪 Scripted Chaos: 1. Timeout -> 2. Success
        chaosController.script(NetworkBehavior.Timeout, NetworkBehavior.Success)
        
        val userId = "user123"
        val likedCollRef = mockk<CollectionReference>()
        val likedSnapshot = mockk<QuerySnapshot>()
        
        every { database.collection(USERS) } returns mockk {
            every { document(userId) } returns mockk {
                every { collection(LIKED_PROPERTIES) } returns likedCollRef
            }
        }

        val likedTask = mockk<Task<QuerySnapshot>>()
        every { likedCollRef.get() } returns likedTask
        
        var attempt = 0
        coEvery { likedTask.await() } coAnswers {
            attempt++
            if (attempt == 1) throw IOException("Timeout (Chaos)")
            likedSnapshot
        }
        every { likedSnapshot.documents } returns emptyList()

        // Verify that the repository handles the internal exception from await() 
        // when called through networkClient.execute (simulated retry behavior would happen here)
        val result = firestoreProperties.fetchLikedProperties(userId)
        
        // If the real networkClient doesn't retry, it should be an Error
        assert(result is AppResult.Error)

        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    private fun createMockPropertyDoc(id: String): QueryDocumentSnapshot {
        val doc = mockk<QueryDocumentSnapshot>()
        val property = PropertyEntityModel(id = id)
        every { doc.toObject(PropertyEntityModel::class.java) } returns property
        return doc
    }
}
