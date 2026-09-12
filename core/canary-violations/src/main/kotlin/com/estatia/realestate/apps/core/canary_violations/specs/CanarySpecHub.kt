package com.estatia.realestate.apps.core.canary_violations.specs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_002.*
import com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_005.*
import com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_008.*
import com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_012.*
import com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_027.*

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
    adversaryHub1: AdversaryHub1,
    adversaryHub2: AdversaryHub2
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

    // Adversaries Delegated
    adversaryHub1.trigger()
    adversaryHub2.trigger()
}

public class AdversaryHub1(
    public val l2t2: SemanticExposedViewModel,
    public val l5t2: SemanticScopeAdversary,
    public val l8t2: DeepNestingAdversary,
    public val l8t2ext: SemanticAbstractionAdversary,
    public val l12t2: SemanticThreadSafetyAdversary,
    public val l27t1: DirectRepository,
    public val l27t2: ReflectionRepository
) {
    @Composable
    public fun trigger() {
        l2t2.aliasedState
        l5t2.leak()
        l8t2.superDeep()
        l8t2ext.leakedExtension()
        l12t2.mutate()
        SyntacticDataLeakage(l27t1)
        SemanticReflectionLeakage(l27t2)
    }
}

public class AdversaryHub2(
    public val l27t3: SecretRepository,
    public val l2t3: DynamicExposedViewModel,
    public val l5t3: DynamicScopeAdversary,
    public val l8t3: DynamicAbstractionAdversary,
    public val l12t3: DynamicThreadSafetyAdversary
) {
    @Composable
    public fun trigger() {
        DynamicReflectionLeakage(l27t3)
        l2t3.getSecretState()
        l5t3.reflect()
        l8t3.getErasedData()
        l12t3.hack()
    }
}
