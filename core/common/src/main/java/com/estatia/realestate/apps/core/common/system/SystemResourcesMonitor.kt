package com.estatia.realestate.apps.core.common.system

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
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

    private val _memoryTrimLevel = MutableStateFlow(0)
    val memoryTrimLevel: StateFlow<Int> = _memoryTrimLevel.asStateFlow()

    private val _isAppVisible = MutableStateFlow(true)
    val isAppVisible: StateFlow<Boolean> = _isAppVisible.asStateFlow()

    init {
        context.registerComponentCallbacks(this)
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
