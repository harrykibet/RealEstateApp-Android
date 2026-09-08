package com.estatia.realestate.apps.core.canary_violations

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.estatia.realestate.apps.core.common.annotations.Repository
import com.estatia.realestate.apps.core.common.annotations.ViewModelMarker
import com.estatia.realestate.apps.core.common.annotations.AllowedArchitectureDependency
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.DomainLeakageCarrier
import com.estatia.realestate.apps.core.domain.DomainCouplingCarrier
import com.estatia.realestate.apps.feature.home.HomeCoupling
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Singleton

/**
 * DELIBERATE ARCHITECTURAL VIOLATIONS
 */

// LAW-008: Implementation Type in Public API
interface ICanaryRepo

@Repository
class CanaryRepository : ICanaryRepo {
    fun leakImplementation(retrofit: retrofit2.Retrofit?): String = ""
    
    fun getRawData(): List<String> = emptyList()
    
    fun smuggledReturn(): List<String> {
        try { return listOf("a") } catch (e: Exception) { return emptyList() } // FailureSmuggling
    }
    
    fun dangerousFallback(data: String?): List<String> {
        return data?.let { listOf(it) } ?: emptyList()
    }
}

// LAW-027: Compose Architecture Leakage
@Composable
fun LeakyComposable(repo: CanaryRepository, state: MutableState<Int>) {
    repo.getRawData() // ComposeArchitectureLeakage
    val list = listOf(1, 2, 3) // ExpensiveRecomposition
    GlobalScope.launch { } // BusinessLogicInCompose
}

// LAW-025: Mutable Singleton Read in Compose
object CanaryConfig {
    @JvmField
    var mutableValue = 0
}

@Composable
fun LeakySingletonRead() {
    val x = CanaryConfig.mutableValue // ComposeMutableSingletonRead
}

// LAW-011: Blocking Main Thread Work & Unbounded Buffer (UnboundedBuffer)
@Composable
fun BlockingComposable() {
    Thread.sleep(1000) // BlockingMainThreadWork
    val flow = MutableSharedFlow<Int>(replay = 101) // UnboundedBuffer (> 100)
    val flow2 = flow { emit(1) }.buffer() // UnboundedBuffer (no args)
}

// LAW-012: Unsafe collection (ThreadSafetyViolation), Unsafe State Collection (UnsafeStateCollection)
// LAW-018: ViewModel SSoT
// LAW-023: Lifecycle Leak (LifecycleLeak)
// LAW-024: Context Leak (ContextLeak)
// LAW-029: God Object (GodObjectFatal)
@ViewModelMarker
class BadViewModel : ViewModel() {
    val state1: StateFlow<Int> = MutableStateFlow(0)
    val state2: StateFlow<String> = MutableStateFlow("") // ViewModel SSoT Violation
    val mutableState = MutableStateFlow(0) 
    var unsafeMap = HashMap<String, String>() 
    var leakedActivity: Activity? = null 
    val collectedState = listOf(MutableStateFlow(0)) // UnsafeStateCollection
    
    // God Object Trigger (13 mutable states > 12)
    var m1 = 0; var m2 = 0; var m3 = 0; var m4 = 0; var m5 = 0; var m6 = 0
    var m7 = 0; var m8 = 0; var m9 = 0; var m10 = 0; var m11 = 0; var m12 = 0; var m13 = 0
    
    fun work() {
        unsafeMap.put("a", "b") // ThreadSafetyViolation
    }
}

// Trigger ContextLeak (LAW-024)
@Singleton
class MonsterComponent(val context: Context) { // ContextLeak
    fun spaghetti() { // SpaghettiMethodFatal
        if (true) { if (true) { if (true) { if (true) { if (true) { if (true) {
            println(12345) 
        } } } } } }
    }
}

// LAW-010: Security
class SecurityViolation {
    val apiKey = "AKIAIOSFODNN7EXAMPLE" // HardcodedSecrets
    
    fun log(password: String) {
        val secret = "super_secret_token" // HardcodedSecrets
        Log.d("AUTH", "User password is: $password") // SensitiveLogging
    }
}

// LAW-019, LAW-006, LAW-013, LAW-020, LAW-021
class ConcurrencyViolation : CoroutineScope {
    override val coroutineContext = Dispatchers.Main // HardcodedDispatcher
    
    suspend fun doWork() {
        launch { } // SecretConcurrency
        val deferred = async { 1 } // UnusedAsync
        withContext(Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) { // MisplacedCEH
        }
    }
    
    suspend fun zombieLoop() {
        while(true) { // MissingCoroutineCancellation
            println("zombie")
        }
    }
}

// LAW-030: Orchestration Monster
class MonsterConstructor(
    val r1: BadViewModel, val r2: BadViewModel, val r3: BadViewModel,
    val r4: BadViewModel, val r5: BadViewModel, val r6: BadViewModel,
    val r7: BadViewModel, val r8: BadViewModel, val r9: BadViewModel,
    val r10: BadViewModel
)

@Composable
fun DesignViolation() {
    Text(text = "Hardcoded", color = Color.Red) // HardcodedDesignValue
}

// LAW-007: Direct System Time (DirectSystemTimeUsage)
class TimeViolation {
    fun now() = System.currentTimeMillis() // DirectSystemTimeUsage
}

// LAW-015: Direct System Time in Test (DirectSystemTimeUsageInTest)
class CanaryTestClass {
    fun testTime() = System.currentTimeMillis() // DirectSystemTimeUsageInTest
}

// LAW-016: Mock in Production (MockInProduction)
class MockViolation {
    fun mock() = io.mockk.mockk<String>() // MockInProduction
}

/**
 * Canary Hub
 */
@Composable
fun CanaryHub(
    repo: CanaryRepository,
    viewModel: BadViewModel,
    monster: MonsterComponent,
    security: SecurityViolation,
    concurrency: ConcurrencyViolation,
    orchestrator: MonsterConstructor
) {
    LeakyComposable(repo, mutableStateOf(0))
    LeakySingletonRead()
    println(viewModel.state1.value)
    println(DomainLeakageCarrier().toString())
    println(DomainCouplingCarrier().toString())
    println(HomeCoupling().toString())
    monster.spaghetti()
    security.log("p")
    DesignViolation()
}
