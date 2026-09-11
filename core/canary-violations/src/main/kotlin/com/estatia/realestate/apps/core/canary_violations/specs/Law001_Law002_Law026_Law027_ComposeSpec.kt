package com.estatia.realestate.apps.core.canary_violations.specs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Repository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Compose Architectural Enforcement
 */

public class PositiveComposeSpec {

    @Composable
    public fun ViolatingComposable(
        repo: PositiveRepository,
        // [CANARY:POSITIVE:MutableStateParameter:ERROR:Mutable state passed]
        state: MutableState<Int>
    ) {
        // [CANARY:POSITIVE:ComposeArchitectureLeakage:ERROR:Direct call to architectural component]
        repo.leak(null)
        
        // [CANARY:POSITIVE:ExpensiveRecomposition:WARN:Expensive object created]
        val regex = Regex(".*")
        
        // [CANARY:POSITIVE:BusinessLogicInCompose:WARN:Business logic detected] [CANARY:POSITIVE:ForbiddenCoroutineScope:BLOCK:Unmanaged CoroutineScope]
        GlobalScope.launch { }
        
        // [CANARY:POSITIVE:RememberMissing:ERROR:State creation not wrapped]
        val s = mutableStateOf(0)
    }
}

public class NegativeComposeSpec {
    @Composable
    public fun SafeComposable(state: Int) {
        // [CANARY:NEGATIVE:MutableStateParameter]
        // [CANARY:NEGATIVE:ComposeArchitectureLeakage]
        // [CANARY:NEGATIVE:ExpensiveRecomposition]
        val regex = remember { Regex(".*") }
        
        // [CANARY:NEGATIVE:BusinessLogicInCompose]
        // [CANARY:NEGATIVE:ForbiddenCoroutineScope]
        
        // [CANARY:NEGATIVE:RememberMissing]
        val s = remember { mutableStateOf(0) }
        
        // [CANARY:NEGATIVE:HardcodedDesignValue]
        val list = remember { listOf(1, 2, 3) }
    }
}
