package com.estatia.realestate.apps.core.network.sources.firebase

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.common.interfaces.IClock
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.SubCollections.LIKED_PROPERTIES
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.USERS
import com.estatia.realestate.apps.core.network.core.ExponentialRetryPolicy
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.testing_network.chaos.ChaosNetworkClient
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import com.estatia.realestate.apps.core.testing.chaos.server.ServerScenario
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FirestorePropertiesTest {

    private lateinit var database: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var networkChaos: NetworkChaosController
    private lateinit var firestoreProperties: FirestoreProperties

    @Before
    fun setup() {
        database = mockk()
        storage = mockk()
        networkChaos = NetworkChaosController()
        
        val exceptionMapper = mockk<IExceptionMapper>(relaxed = true)
        // Ensure it maps SocketTimeoutException to something retryable for the policy
        every { exceptionMapper.map(any()) } answers {
            val throwable = it.invocation.args[0] as Throwable
            if (throwable is java.net.SocketTimeoutException) com.estatia.realestate.apps.core.common.exceptions.NetworkException.Timeout
            else com.estatia.realestate.apps.core.common.exceptions.NetworkException.ConnectionFailed
        }

        val clock = mockk<IClock>(relaxed = true)
        val retryPolicy = ExponentialRetryPolicy(exceptionMapper, clock)

        val networkClient = ChaosNetworkClient(
            networkChaos = networkChaos,
            exceptionMapper = exceptionMapper,
            retryPolicy = retryPolicy
        )
        
        val metricsTracker = mockk<com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker>(relaxed = true)
        firestoreProperties = FirestoreProperties(database, storage, networkClient, metricsTracker)
    }

    @Test
    fun `fetchLikedProperties handles transient network timeouts`() = runTest {
        // 🧪 Scenario: Timeout then Success
        networkChaos.script(NetworkBehavior.Timeout, NetworkBehavior.Success)
        
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        
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
        
        coEvery { likedTask.await() } returns likedSnapshot
        every { likedSnapshot.documents } returns emptyList()

        // 🧪 The scenario is now driven by the production retry implementation.
        // It should catch the first Timeout and retry, hitting Success on the second attempt.
        val result = firestoreProperties.fetchLikedProperties(userId)
        
        assert(result is AppResult.Success)

        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    @Test
    fun `fetchLikedProperties handles malformed JSON server chaos`() = runTest {
        // 🧪 Chaos Scenario: Server returns malformed response
        networkChaos.setServerScenario(ServerScenario.MalformedJson)
        
        val result = firestoreProperties.fetchLikedProperties("user123")
        
        assert(result is AppResult.Error)
    }

    private fun createMockPropertyDoc(id: String): QueryDocumentSnapshot {
        val doc = mockk<QueryDocumentSnapshot>()
        val property = PropertyEntityModel(id = id)
        every { doc.toObject(PropertyEntityModel::class.java) } returns property
        return doc
    }
}
