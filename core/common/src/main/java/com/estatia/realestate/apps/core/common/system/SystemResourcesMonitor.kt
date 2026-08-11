package com.estatia.realestate.apps.core.common.system

import android.content.BroadcastReceiver
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.PowerManager
import androidx.core.content.getSystemService
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitors system resources like memory pressure and application lifecycle visibility.
 */
@Singleton
class SystemResourcesMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) : ComponentCallbacks2, DefaultLifecycleObserver {

    private val powerManager = context.getSystemService<PowerManager>()

    private val _memoryTrimLevel = MutableStateFlow(0)
    val memoryTrimLevel: StateFlow<Int> = _memoryTrimLevel.asStateFlow()

    private val _isAppVisible = MutableStateFlow(true)
    val isAppVisible: StateFlow<Boolean> = _isAppVisible.asStateFlow()

    private val _isInteractive = MutableStateFlow(powerManager?.isInteractive ?: true)
    val isInteractive: StateFlow<Boolean> = _isInteractive.asStateFlow()

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_ON -> _isInteractive.value = true
                Intent.ACTION_SCREEN_OFF -> _isInteractive.value = false
            }
        }
    }

    init {
        context.registerComponentCallbacks(this)
        
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        context.registerReceiver(screenReceiver, filter)

        // ProcessLifecycleOwner tracks the whole app's lifecycle
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    // --- ComponentCallbacks2 ---

    override fun onTrimMemory(level: Int) {
        _memoryTrimLevel.value = level
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // No-op
    }

    override fun onLowMemory() {
        _memoryTrimLevel.value = ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL
    }

    // --- DefaultLifecycleObserver ---

    override fun onStart(owner: LifecycleOwner) {
        _isAppVisible.value = true
    }

    override fun onStop(owner: LifecycleOwner) {
        _isAppVisible.value = false
    }
}
