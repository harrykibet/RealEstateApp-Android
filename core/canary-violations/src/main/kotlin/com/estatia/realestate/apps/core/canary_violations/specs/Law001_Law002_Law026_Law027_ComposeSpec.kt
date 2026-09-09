package com.estatia.realestate.apps.core.canary_violations.specs

import androidx.compose.runtime.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow

/**
 * Compose Architectural Enforcement
 */

public class PositiveComposeSpec {

    @Composable
    public fun ViolatingComposable(
        repo: PositiveRepository,
        // [CANARY:POSITIVE:MutableStateParameter]
        state: MutableState<Int>
    ) {
        // [CANARY:POSITIVE:ComposeArchitectureLeakage]
        repo.leak(null)
        
        // [CANARY:POSITIVE:ExpensiveRecomposition]
        val regex = Regex(".*")
        
        // [CANARY:POSITIVE:BusinessLogicInCompose] [CANARY:POSITIVE:ForbiddenCoroutineScope]
        GlobalScope.launch { }
        
        // [CANARY:POSITIVE:RememberMissing]
        val s = mutableStateOf(0)
    }
}

public class NegativeComposeSpec {

    @Composable
    public fun ValidComposable(state: StateFlow<Int>) {
        // [CANARY:NEGATIVE:ComposeArchitectureLeakage]
        // [CANARY:NEGATIVE:MutableStateParameter]
        val value by state.collectAsState()
        
        // [CANARY:NEGATIVE:ExpensiveRecomposition]
        val regex = remember { Regex(".*") }
        
        // [CANARY:NEGATIVE:RememberMissing]
        val list = remember { listOf(1, 2, 3) }
    }
}
