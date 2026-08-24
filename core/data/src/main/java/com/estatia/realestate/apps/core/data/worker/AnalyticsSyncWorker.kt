package com.estatia.realestate.apps.core.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.estatia.realestate.apps.core.domain.analytics.IAnalyticsTracker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Background worker for synchronizing the analytics outbox with the remote server.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Atomically upload pending events from local storage to the backend.
 * - Concurrency: Thread-safe via WorkManager's worker execution context.
 * - Resilience: Implements exponential backoff via [Result.retry] for transient network failures.
 * - Lifecycle: Enforces a maximum of 3 retries before marking the batch as terminal failure.
 */
@HiltWorker
class AnalyticsSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val analyticsTracker: IAnalyticsTracker
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            analyticsTracker.syncEvents()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
