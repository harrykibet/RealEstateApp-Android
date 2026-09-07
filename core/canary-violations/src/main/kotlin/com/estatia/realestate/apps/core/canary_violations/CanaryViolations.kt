package com.estatia.realestate.apps.core.canary_violations

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.estatia.realestate.apps.core.common.annotations.Repository
import com.estatia.realestate.apps.core.common.annotations.ViewModelMarker
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.DomainLeakageCarrier
import com.estatia.realestate.apps.core.domain.DomainCouplingCarrier
import com.estatia.realestate.apps.feature.home.HomeCoupling
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
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

    // Violation: Returns raw String (non-suspend, but we want to catch complex logic)
    // Actually, LAW-009 is now a CONVENTION and exempts trivial simple types.
    // Let's use a non-trivial type to trigger it.
    fun getRawData(): List<String> = emptyList()
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
    // Violation: Reading var from object
    val x = CanaryConfig.mutableValue
}

// LAW-018: ViewModel SSoT & LAW-002: State Ownership
@ViewModelMarker
class BadViewModel : ViewModel() {
    // LAW-018 Violation: Multiple public StateFlows (Multiple Authorities)
    val state1: StateFlow<Int> = MutableStateFlow(0)
    val state2: StateFlow<String> = MutableStateFlow("")
    
    // LAW-002 Violation: Exposing mutable state container
    val mutableState = MutableStateFlow(0)

    // ✅ VALID: Other Flows for events/navigation are permitted under refined LAW-018
    val navigationEvents: Flow<String> = flow { }
    
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

/**
 * Canary Hub: Wires all classes together to ensure they are present in the compiled artifact
 * and reachable by the Lint graph.
 */
@Composable
fun CanaryHub(
    repo: CanaryRepository,
    viewModel: BadViewModel,
    logger: LeakyLogger,
    concurrency: ConcurrencyViolation,
    dispatcher: DispatcherViolation
) {
    // Wire everything
    LeakyComposable(repo)
    LeakySingletonRead()
    
    // Use properties to ensure they aren't optimized away
    println(MissingVisibilityClass().toString())
    println(viewModel.state1.value)
    
    // Trigger module coupling detectors via usage
    println(DomainLeakageCarrier().toString())
    println(DomainCouplingCarrier().toString())
    println(HomeCoupling().toString())
    
    logger.log("fake_password")
}
