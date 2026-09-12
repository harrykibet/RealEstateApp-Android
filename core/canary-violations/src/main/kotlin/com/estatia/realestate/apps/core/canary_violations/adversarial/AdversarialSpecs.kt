package com.estatia.realestate.apps.core.canary_violations.adversarial

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.estatia.realestate.apps.core.architecture.annotations.Identity
import com.estatia.realestate.apps.core.architecture.annotations.Ui
import java.io.Serializable
import java.util.ArrayList
import java.lang.reflect.Proxy

/**
 * 🕵️ ADVERSARIAL SPECS
 * These specimens are designed to "game" the meta-system using shadowing, 
 * aliasing, and other obfuscation techniques.
 */

// --- SHADOWING (Testing False Positives) ---

public class FakeMutableStateFlow<T>(public val value: T) // Safe: Local dummy

@Identity.ViewModelMarker
public class ShadowingViewModel : ViewModel() {
    // [CANARY:NEGATIVE:ExposedMutableState]
    // Should NOT be flagged if resolver is using FQN.
    public val state: FakeMutableStateFlow<Int> = FakeMutableStateFlow(0)
}

public class HashMap<K, V> // Safe: Local dummy (pretending to be thread-safe)

@Identity.Manager
public class ShadowingManager {
    // [CANARY:NEGATIVE:ThreadSafetyViolation]
    // Should NOT be flagged if resolver identifies it's not java.util.HashMap.
    public val cache: HashMap<String, String> = HashMap()
}

// --- ALIASING (Testing False Negatives) ---

public typealias HiddenMutableState<T> = MutableStateFlow<T>
public typealias HiddenGlobalScope = GlobalScope

@Identity.ViewModelMarker
public class AliasingViewModel : ViewModel() {
    // [CANARY:POSITIVE:ExposedMutableState:BLOCK:mutable state]
    // Should be caught if resolver expands type aliases.
    public val state: HiddenMutableState<Int> = MutableStateFlow(0)
    
    public fun leak() {
        // [CANARY:POSITIVE:ForbiddenCoroutineScope:BLOCK:GlobalScope]
        HiddenGlobalScope.launch { }
    }
}

// --- FQN BYPASS (Testing Import reliance) ---

@Identity.Repository
public class FqnBypassRepository {
    public fun doWork() {
        // [CANARY:POSITIVE:ForbiddenCoroutineScope:BLOCK:GlobalScope]
        // Bypasses import checks.
        GlobalScope.launch { }
    }
}

// --- DELEGATION OBFUSCATION ---

@Identity.ViewModelMarker
public class DelegateAdversary : ViewModel() {
    // [CANARY:POSITIVE:ExposedMutableState:BLOCK:mutable state]
    // Hiding mutable state behind a 'lazy' delegate.
    public val trickyState: MutableStateFlow<Int> by lazy { MutableStateFlow(0) }
}

// --- TYPE ERASURE BYPASS ---

@Identity.Repository
public class ErasureAdversary {
    // [CANARY:POSITIVE:ImplementationTypeInPublicApi:BLOCK:ArrayList]
    // Returning Any to hide implementation type from simple return-type checks.
    public fun getDataHacked(): Any = ArrayList<String>()
}

// --- REFLECTION (The Ultimate Adversary) ---

@Identity.Repository
public class PrivateBypassRepository {
    private fun privateAction() { println("hacked") }
}

@Composable
@Ui.UiScreen
public fun ReflectionAdversary(repo: PrivateBypassRepository) {
    // [CANARY:POSITIVE:ReflectionBypass:BLOCK:getDeclaredMethod]
    // Accessing repository in UI via reflection
    repo.javaClass.getDeclaredMethod("privateAction").invoke(repo)
    
    // [CANARY:POSITIVE:ReflectionBypass:BLOCK:newProxyInstance]
    // Using dynamic proxy to bypass direct call visitors
    val proxy = Proxy.newProxyInstance(
        repo.javaClass.classLoader,
        arrayOf(Serializable::class.java),
        { _, _, _ -> repo.javaClass.getDeclaredMethod("privateAction").invoke(repo) }
    )
}

// --- TRANSITIVE LEAKAGE ---

@Identity.Contract
public interface TrickyContract {
    // Leak hidden in a nested object within a contract
    public object Constants {
        // [CANARY:POSITIVE:ImplementationTypeInPublicApi:BLOCK:ArrayList]
        public val CACHE: ArrayList<String> = ArrayList()
    }
}

// --- SEMANTIC AMBIGUITY ---

@Identity.UseCase
public class DeepNestingUseCase {
    // [CANARY:POSITIVE:ImplementationTypeInPublicApi:BLOCK:ArrayList]
    // Deeply nested generics
    public fun superDeep(): List<Map<String, Set<ArrayList<Int>>>> = emptyList()
}

public class RepositorySuffixButNotARepo {
    // [CANARY:NEGATIVE:ImplementationTypeInPublicApi]
    // Name inference would fail here (Suffix match but not annotated).
    public fun getRaw(): ArrayList<String> = ArrayList()
}

// --- EXTENSION LEAKAGE ---

@Identity.Repository
public class ExtensionTargetRepo

// [CANARY:POSITIVE:ImplementationTypeInPublicApi:BLOCK:ArrayList]
// Public extension exposing infrastructure on a Repository
public fun ExtensionTargetRepo.leakedData(): ArrayList<String> = ArrayList()

// --- INDIRECT MUTATION (Testing Concurrency Precision) ---

@Identity.Manager
public class IndirectMutationManager {
    private val safeList: MutableList<String> = ArrayList()
    
    public fun doTrickyMutation() {
        // [CANARY:POSITIVE:ThreadSafetyViolation:BLOCK:ArrayList]
        // Mutation via standard apply block
        safeList.apply { add("hacked") }
        
        // [CANARY:POSITIVE:ThreadSafetyViolation:BLOCK:ArrayList]
        // Mutation via direct operator
        safeList += "also hacked"
    }
}
