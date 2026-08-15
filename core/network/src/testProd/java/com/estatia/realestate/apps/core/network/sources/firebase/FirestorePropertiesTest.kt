package com.estatia.realestate.apps.core.network.sources.firebase

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.PROPERTIES
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.SubCollections.LIKED_PROPERTIES
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.USERS
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FirestorePropertiesTest {

    private lateinit var database: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var networkClient: INetworkClient
    private lateinit var firestoreProperties: FirestoreProperties

    @BeforeEach
    fun setup() {
        database = mockk()
        storage = mockk()
        networkClient = mockk()
        firestoreProperties = FirestoreProperties(database, storage, networkClient)

        // Mock networkClient.execute to run the block and return success
        coEvery {
            networkClient.execute<Any?>(any(), any())
        } coAnswers {
            val apiCall = secondArg<suspend () -> Any?>()
            try {
                AppResult.Success(apiCall())
            } catch (e: Exception) {
                AppResult.Error(com.estatia.realestate.apps.core.common.exceptions.NetworkException.Unknown(e))
            }
        }
    }

    @Test
    fun `fetchLikedProperties chunks IDs into groups of 30`() = runTest {
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")

        val userId = "user123"
        val propertyIds = (1..65).map { "prop_$it" }

        val userDocRef = mockk<DocumentReference>()
        val likedCollRef = mockk<CollectionReference>()
        val likedSnapshot = mockk<QuerySnapshot>()
        val likedDocs = propertyIds.map { id ->
            val doc = mockk<QueryDocumentSnapshot>()
            every { doc.id } returns id
            doc
        }

        every { database.collection(USERS) } returns mockk {
            every { document(userId) } returns userDocRef
        }
        every { userDocRef.collection(LIKED_PROPERTIES) } returns likedCollRef

        val likedTask = mockk<Task<QuerySnapshot>>()
        every { likedCollRef.get() } returns likedTask
        coEvery { likedTask.await() } returns likedSnapshot
        every { likedSnapshot.documents } returns likedDocs

        val propertiesCollRef = mockk<CollectionReference>()
        every { database.collection(PROPERTIES) } returns propertiesCollRef

        // Mocking the whereIn calls. We expect 3 calls (30, 30, 5)
        val chunk1 = propertyIds.subList(0, 30)
        val chunk2 = propertyIds.subList(30, 60)
        val chunk3 = propertyIds.subList(60, 65)

        val query1 = mockk<Query>()
        val query2 = mockk<Query>()
        val query3 = mockk<Query>()

        every { propertiesCollRef.whereIn(FieldPath.documentId(), chunk1) } returns query1
        every { propertiesCollRef.whereIn(FieldPath.documentId(), chunk2) } returns query2
        every { propertiesCollRef.whereIn(FieldPath.documentId(), chunk3) } returns query3

        val task1 = mockk<Task<QuerySnapshot>>()
        val task2 = mockk<Task<QuerySnapshot>>()
        val task3 = mockk<Task<QuerySnapshot>>()

        every { query1.get() } returns task1
        every { query2.get() } returns task2
        every { query3.get() } returns task3

        val snapshot1 = mockk<QuerySnapshot>()
        val snapshot2 = mockk<QuerySnapshot>()
        val snapshot3 = mockk<QuerySnapshot>()

        coEvery { task1.await() } returns snapshot1
        coEvery { task2.await() } returns snapshot2
        coEvery { task3.await() } returns snapshot3

        val propDocs1 = chunk1.map { createMockPropertyDoc(it) }
        val propDocs2 = chunk2.map { createMockPropertyDoc(it) }
        val propDocs3 = chunk3.map { createMockPropertyDoc(it) }

        every { snapshot1.documents } returns propDocs1
        every { snapshot2.documents } returns propDocs2
        every { snapshot3.documents } returns propDocs3

        val result = firestoreProperties.fetchLikedProperties(userId)

        assertTrue(result is AppResult.Success)
        val properties = (result as AppResult.Success<List<PropertyEntityModel>>).data
        assertEquals(65, properties.size)
        assertEquals("prop_1", properties[0].id)
        assertEquals("prop_65", properties[64].id)

        verify(exactly = 1) { propertiesCollRef.whereIn(FieldPath.documentId(), chunk1) }
        verify(exactly = 1) { propertiesCollRef.whereIn(FieldPath.documentId(), chunk2) }
        verify(exactly = 1) { propertiesCollRef.whereIn(FieldPath.documentId(), chunk3) }

        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    @Test
    fun `fetchLikedProperties with no liked IDs returns empty list`() = runTest {
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")

        val userId = "user123"

        val userDocRef = mockk<DocumentReference>()
        val likedCollRef = mockk<CollectionReference>()
        val likedSnapshot = mockk<QuerySnapshot>()

        every { database.collection(USERS) } returns mockk {
            every { document(userId) } returns userDocRef
        }
        every { userDocRef.collection(LIKED_PROPERTIES) } returns likedCollRef

        val likedTask = mockk<Task<QuerySnapshot>>()
        every { likedCollRef.get() } returns likedTask
        coEvery { likedTask.await() } returns likedSnapshot
        every { likedSnapshot.documents } returns emptyList()

        val result = firestoreProperties.fetchLikedProperties(userId)

        assertTrue(result is AppResult.Success)
        val properties = (result as AppResult.Success<List<PropertyEntityModel>>).data
        assertEquals(0, properties.size)

        verify(exactly = 0) { database.collection(PROPERTIES) }

        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    private fun createMockPropertyDoc(id: String): QueryDocumentSnapshot {
        val doc = mockk<QueryDocumentSnapshot>()
        val property = PropertyEntityModel(id = id)
        every { doc.toObject(PropertyEntityModel::class.java) } returns property
        return doc
    }
}
