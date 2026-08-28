package com.estatia.realestate.apps.feature.property

import android.net.Uri
import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.common.interfaces.IClock
import com.estatia.realestate.apps.core.data.repositories.PropertyRepository
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.intelligence.IMediaIntelligenceService
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import com.estatia.realestate.apps.core.testing.fake.analytics.FakeEngagementRepository
import com.estatia.realestate.apps.core.testing.fake.source.FakePropertyLocalDataSource
import com.estatia.realestate.apps.core.testing_network.chaos.ChaosNetworkClient
import com.estatia.realestate.apps.core.testing_network.fake.source.FakePropertyRemoteDataSource
import com.estatia.realestate.apps.feature.property.ui.uploads.viewModels.AddPropertyViewModel
import com.estatia.realestate.apps.feature.property.utils.PropertyData
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.SocketTimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class MediaUploadResilienceTest {

    private lateinit var repository: IPropertyRepository
    private lateinit var remoteDataSource: FakePropertyRemoteDataSource
    private lateinit var localDataSource: FakePropertyLocalDataSource
    private lateinit var networkChaos: NetworkChaosController
    private lateinit var authRepository: IAuthRepository
    private lateinit var intelligenceService: IMediaIntelligenceService
    private lateinit var viewModel: AddPropertyViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val propertyData = PropertyData()
    
    private var actualAttempts = 0

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } answers {
            val mock = mockk<Uri>(relaxed = true)
            every { mock.toString() } returns it.invocation.args[0] as String
            mock
        }

        networkChaos = NetworkChaosController()
        val exceptionMapper = mockk<com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper>()
        every { exceptionMapper.map(any()) } answers {
            val t = firstArg<Throwable>()
            when {
                t is NetworkException -> t
                t is SocketTimeoutException || t.message?.contains("timed out") == true -> NetworkException.Timeout
                else -> NetworkException.ConnectionFailed
            }
        }
        
        val clock = object : IClock {
            private var time = 0L
            override fun currentTimeMillis(): Long {
                val t = time
                time += 1000 
                return t
            }
        }
        
        val retryPolicy = com.estatia.realestate.apps.core.network.core.ExponentialRetryPolicy(exceptionMapper, clock)
        
        actualAttempts = 0
        val chaosNetworkClient = ChaosNetworkClient(
            networkChaos = networkChaos,
            exceptionMapper = exceptionMapper,
            retryPolicy = retryPolicy,
            onAttempt = { actualAttempts++ }
        )

        remoteDataSource = FakePropertyRemoteDataSource()
        
        val propertyRemoteSource = object : com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource by remoteDataSource {
            override suspend fun uploadProperty(
                property: com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel,
                contactInfo: com.estatia.realestate.apps.core.network.db_entities.PropertyContactEntity,
                imageUris: List<Uri>,
                videoUris: List<Uri>
            ): AppResult<String> {
                return chaosNetworkClient.execute(com.estatia.realestate.apps.core.network.core.RetryConfigs.IMAGE_UPLOAD) {
                    val result = remoteDataSource.uploadProperty(property, contactInfo, imageUris, videoUris)
                    when (result) {
                        is AppResult.Success -> result.data
                        is AppResult.Error -> throw result.exception
                    }
                }
            }
        }

        localDataSource = FakePropertyLocalDataSource()
        
        repository = PropertyRepository(
            localDataSource = localDataSource,
            remoteDataSource = propertyRemoteSource,
            userRepository = mockk<com.estatia.realestate.apps.core.domain.repository.IUserRepository>(relaxed = true),
            metricsTracker = mockk(relaxed = true),
            engagementRepository = FakeEngagementRepository(),
            contentSafetyService = mockk(relaxed = true),
            exceptionTranslator = mockk(relaxed = true),
            clock = clock
        )

        authRepository = mockk()
        intelligenceService = mockk(relaxed = true)
        val metricsTracker = mockk<com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker>(relaxed = true)
        val savedStateHandle = mockk<androidx.lifecycle.SavedStateHandle>(relaxed = true)
        every { savedStateHandle.get<com.estatia.realestate.apps.feature.property.utils.AddPropertyDraft>(any()) } returns null
        
        viewModel = AddPropertyViewModel(repository, authRepository, intelligenceService, metricsTracker, savedStateHandle, propertyData)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Uri::class)
    }

    @Test
    fun `upload retry succeeds after transient timeout`() = runTest {
        val userId = "user_1"
        every { authRepository.getCurrentUserId() } returns userId
        viewModel.updateTitle("Resilience Test")

        // 🧪 Script: Timeout -> Success
        networkChaos.script(NetworkBehavior.Timeout, NetworkBehavior.Success)

        var successId: String? = null
        viewModel.saveProperty(onFailure = { }, onSuccess = { successId = it })
        advanceUntilIdle()
        
        assertEquals("Expected exactly 2 attempts", 2, actualAttempts)
        assertTrue("Should have succeeded eventually", successId != null)
    }

    @Test
    fun `upload fails after exhausting all retries`() = runTest {
        val userId = "user_1"
        every { authRepository.getCurrentUserId() } returns userId
        viewModel.updateTitle("Persistent Failure")

        // Script 6 timeouts (max is 5)
        networkChaos.script(
            NetworkBehavior.Timeout,
            NetworkBehavior.Timeout,
            NetworkBehavior.Timeout,
            NetworkBehavior.Timeout,
            NetworkBehavior.Timeout,
            NetworkBehavior.Timeout
        )

        var caughtException: Exception? = null
        viewModel.saveProperty(onFailure = { caughtException = it }, onSuccess = {})
        advanceUntilIdle()

        assertEquals("Expected exactly 5 attempts", 5, actualAttempts)
        assertTrue("Expected Timeout exception", caughtException is NetworkException.Timeout)
    }

    @Test
    fun `upload does not retry on non-retryable error`() = runTest {
        val userId = "user_1"
        every { authRepository.getCurrentUserId() } returns userId
        
        // 401 Unauthorized is not retryable
        networkChaos.script(NetworkBehavior.HttpError(401))

        var caughtException: Exception? = null
        viewModel.saveProperty(onFailure = { caughtException = it }, onSuccess = {})
        advanceUntilIdle()

        assertEquals("Expected exactly 1 attempt", 1, actualAttempts)
        assertTrue("Expected ConnectionFailed (from mapping 401)", caughtException is NetworkException.ConnectionFailed)
    }
}
