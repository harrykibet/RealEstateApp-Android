package com.estatia.realestate.apps.core.canary_violations

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import com.estatia.realestate.apps.core.common.annotations.Repository
import com.estatia.realestate.apps.core.common.annotations.ViewModelMarker
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.feature.auth.viewModels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * DELIBERATE ARCHITECTURAL VIOLATIONS
 * This class exists to verify that our static analysis (Lint/KSP) correctly detects 
 * regressions when running against a real multi-module codebase.
 */

// LAW-008: Missing Visibility Modifier (Kotlin default public)
class MissingVisibilityClass {
    fun missingVisibilityMethod() {}
}

// LAW-009: Missing Result Wrapper
@Repository
class CanaryRepository : Runnable { // LAW-008: Must implement interface
    override fun run() {}

    // Violation: Returns raw String
    fun getRawData(): String = "Data"
}

// LAW-027: Compose Architecture Leakage
@Composable
fun LeakyComposable(repo: CanaryRepository) {
    // Violation: Direct repository call
    repo.getRawData()
    
    // LAW-001: Business Logic in Compose
    // Violation: launch in Composable
    @Suppress("OPT_IN_USAGE")
    GlobalScope.launch { } 
}

// LAW-025: Mutable Singleton Read in Compose
object CanaryConfig {
    var mutableValue = 0
}

@Composable
fun LeakySingletonRead() {
    val x = CanaryConfig.mutableValue
}

// LAW-018: ViewModel SSoT & LAW-016: State Ownership
@ViewModelMarker
class BadViewModel : androidx.lifecycle.ViewModel() {
    // LAW-018 Violation: Multiple public StateFlows
    val state1: StateFlow<Int> = MutableStateFlow(0)
    val state2: StateFlow<String> = MutableStateFlow("")
    
    // LAW-016 Violation: Exposing mutable state
    val mutableState = MutableStateFlow(0)
    
    // LAW-023: Lifecycle Leak
    var leakedActivity: Activity? = null
}

// LAW-010: Sensitive Logging
class LeakyLogger {
    fun log(password: String) {
        Log.d("AUTH", "User password is: ${"$"}{password}")
    }
}

// LAW-019: Secret Concurrency
class ConcurrencyViolation {
    val scope = CoroutineScope(Dispatchers.Main)
    
    suspend fun doWork() {
        // Violation: launching on external scope in suspend function
        scope.launch { }
    }
}

// LAW-006: Hardcoded Dispatcher
class DispatcherViolation {
    fun run() {
        // Violation: Direct use of Dispatchers.IO
        CoroutineScope(Dispatchers.IO).launch { }
    }
}

// LAW-003: Infrastructure Leakage & LAW-004: Feature Coupling
// Note: We use the classes directly in properties to trigger the detectors that check imports/types
class LeakageCarrier {
    val leakedDataSource: IPropertyLocalDataSource? = null
    val leakedViewModel: LoginViewModel? = null
}
