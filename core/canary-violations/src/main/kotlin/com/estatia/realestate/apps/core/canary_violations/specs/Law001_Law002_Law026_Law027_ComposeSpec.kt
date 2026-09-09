package com.estatia.realestate.apps.core.canary_violations.specs

import androidx.compose.runtime.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow

/**
 * LAW-001: Business Logic in Compose
 * LAW-002: State Ownership
 * LAW-026: Expensive Recomposition
 * LAW-027: Architecture Leakage
 */

public class Law001_002_026_027_Positive_Compose {

    @Composable
    public fun PositiveCanary(
        repo: Law009_Positive_Repository,
        // [CANARY:POSITIVE:MutableStateParameter]
        state: MutableState<Int>
    ) {
        // [CANARY:POSITIVE:ComposeArchitectureLeakage]
        repo.getRawData()
        
        // [CANARY:POSITIVE:ExpensiveRecomposition]
        val regex = Regex(".*")
        
        // [CANARY:POSITIVE:BusinessLogicInCompose] [CANARY:POSITIVE:ForbiddenCoroutineScope]
        GlobalScope.launch { }
        
        // [CANARY:POSITIVE:RememberMissing]
        val s = mutableStateOf(0)
    }
}

public class Law001_002_026_027_Negative_Compose {

    @Composable
    public fun NegativeCanary(state: StateFlow<Int>) {
        // [CANARY:NEGATIVE:ComposeArchitectureLeakage]
        val value by state.collectAsState()
        
        // [CANARY:NEGATIVE:ExpensiveRecomposition]
        val regex = remember { Regex(".*") }
        
        val list = remember { listOf(1, 2, 3) }
    }
}
