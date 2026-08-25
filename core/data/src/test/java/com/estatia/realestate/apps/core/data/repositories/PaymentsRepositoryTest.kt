package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.interfaces.IPaymentsRemoteDataSource
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.model.feature.PaymentStatus
import com.estatia.realestate.apps.core.testing.generators.PaymentGenerator
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PaymentsRepositoryTest {

    private lateinit var remoteDataSource: IPaymentsRemoteDataSource
    private lateinit var metricsTracker: IMetricsTracker
    private lateinit var repository: PaymentsRepository

    @Before
    fun setup() {
        remoteDataSource = mockk()
        metricsTracker = mockk(relaxed = true)
        repository = PaymentsRepository(remoteDataSource, metricsTracker)
    }

    @Test
    fun `processPayment successful records metrics`() = runTest {
        val amount = PaymentGenerator.generateAmount()
        val method = PaymentGenerator.generateMethod()
        
        coEvery { remoteDataSource.processPayment(any(), any(), any()) } returns 
            AppResult.Success(PaymentStatus.SUCCESS)

        val result = repository.processPayment("ref_123", amount, "USD", method)

        assertEquals(AppResult.Success(PaymentStatus.SUCCESS), result)
        verify { metricsTracker.incrementCounter("payment.process.success") }
        verify { metricsTracker.trackDuration("payment.process.latency", any()) }
    }
}
