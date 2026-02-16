package com.estatia.realestate.apps.core.common.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class BatteryState {
    data class Normal(
        val level: Int,
        val isCharging: Boolean
    ) : BatteryState()

    data class Throttled(
        val level: Int,
        val isCharging: Boolean,
        val thermalStatus: Int
    ) : BatteryState()
}


@Singleton
class BatteryOptimizationManager @Inject constructor(
    private val context: Context,
    private val powerManager: PowerManager
) : IBatteryManager {
    companion object {
        private const val LOW_BATTERY_THRESHOLD = 20
        private const val CRITICAL_THERMAL_THRESHOLD = 3 // ThermalStatus.SEVERE
    }

    private val batteryStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateBatteryStatus(intent)
        }
    }

    // Battery state tracking
    private var currentBatteryLevel = 100
    private var isCharging = false
    private var thermalStatus = 0

    init {
        context.registerReceiver(
            batteryStatusReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
    }

    override fun shouldThrottlePerformance(): Boolean {
        return when {
            isInBatterySaverMode() -> true
            currentBatteryLevel < LOW_BATTERY_THRESHOLD && !isCharging -> true
            thermalStatus >= CRITICAL_THERMAL_THRESHOLD -> true
            else -> false
        }
    }

    override fun getRecommendedQualityLevel(maxQuality: Int): Int {
        return if (shouldThrottlePerformance()) {
            when {
                thermalStatus >= CRITICAL_THERMAL_THRESHOLD -> maxQuality / 4
                currentBatteryLevel < 10 -> maxQuality / 2
                else -> (maxQuality * 0.75).toInt()
            }
        } else {
            maxQuality
        }
    }

    override fun scheduleBackgroundTask(task: Runnable, delay: Long) {
        if (shouldDeferBackgroundTasks()) {
            WorkManager.getInstance(context)
                .enqueue(
                    OneTimeWorkRequest.Builder(BatteryAwareWorker::class.java)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .build())
        } else {
            Handler(Looper.getMainLooper()).postDelayed(task, delay)
        }
    }

    private fun getCurrentBatteryState(): BatteryState {
        return if (shouldThrottlePerformance()) {
            BatteryState.Throttled(
                level = currentBatteryLevel,
                isCharging = isCharging,
                thermalStatus = thermalStatus
            )
        } else {
            BatteryState.Normal(
                level = currentBatteryLevel,
                isCharging = isCharging
            )
        }
    }

    override fun observeBatteryState(): Flow<BatteryState> = callbackFlow {

        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                updateBatteryStatus(intent)
                trySend(getCurrentBatteryState())
            }
        }

        val thermalListener =
            PowerManager.OnThermalStatusChangedListener { status ->
                thermalStatus = status
                trySend(getCurrentBatteryState())
            }

        // Register listeners
        context.registerReceiver(
            batteryReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            powerManager.addThermalStatusListener(
                ContextCompat.getMainExecutor(context),
                thermalListener
            )
        }

        // Emit initial state
        trySend(getCurrentBatteryState())

        awaitClose {
            context.unregisterReceiver(batteryReceiver)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                powerManager.removeThermalStatusListener(thermalListener)
            }
        }
    }


    private fun updateBatteryStatus(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        currentBatteryLevel = (level / scale.toFloat() * 100).toInt()

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
    }

    private fun isInBatterySaverMode(): Boolean {
        return powerManager.isPowerSaveMode
    }

    private fun shouldDeferBackgroundTasks(): Boolean {
        return currentBatteryLevel < 30 && !isCharging
    }

    override fun cleanup() {
        context.unregisterReceiver(batteryStatusReceiver)
    }

    class BatteryAwareWorker(
        context: Context,
        workerParams: WorkerParameters
    ) : Worker(context, workerParams) {
        override fun doWork(): Result {
            // Implement battery-optimized background tasks
            return Result.success()
        }
    }
}