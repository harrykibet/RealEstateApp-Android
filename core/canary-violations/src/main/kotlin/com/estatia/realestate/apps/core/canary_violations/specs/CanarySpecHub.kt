package com.estatia.realestate.apps.core.canary_violations.specs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

import com.estatia.realestate.apps.core.canary_violations.adversarial.*

/**
 * Hub to ensure all specs are visible to the compiler and analyzer.
 */
public val heartbeat: String = "DELIBERATE ARCHITECTURAL VIOLATIONS" // [CANARY:POSITIVE:LintCanaryActive]

@Composable
public fun CanarySpecHub(
    api: PositiveFailureRepository,
    state: PositiveStateViewModel,
    complexity: Law030_Positive_Spec,
    perf: Law011_Performance_Positive_Spec,
    security: Law010_Positive_Spec,
    adversary: AliasingViewModel,
    trickyRepo: FqnBypassRepository,
    reflection: PrivateBypassRepository,
    delegateAdversary: DelegateAdversary,
    erasureAdversary: ErasureAdversary,
    deepAdversary: DeepNestingUseCase,
    extensionTarget: ExtensionTargetRepo
) {
    // API
    api.getRawData()
    
    // Compose
    PositiveComposeSpec().ViolatingComposable(PositiveRepository(), mutableStateOf(0))
    PositiveComposeUiSpec().PositiveCanary()
    Law025_Compose_Positive_Spec().PositiveCanary()
    
    // State
    state.mutate()
    
    // Complexity
    complexity.r1.mutate()
    
    // Performance
    perf.PositiveCanary()
    
    // Environment
    Environment_Positive_Spec().now()

    // Adversaries
    adversary.leak()
    trickyRepo.doWork()
    ReflectionAdversary(reflection)
    delegateAdversary.trickyState
    erasureAdversary.getDataHacked()
    deepAdversary.superDeep()
    extensionTarget.leakedData()
}
