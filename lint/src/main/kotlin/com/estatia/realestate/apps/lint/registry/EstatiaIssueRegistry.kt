package com.estatia.realestate.apps.lint.registry

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.estatia.realestate.apps.lint.concurrency.*
import com.estatia.realestate.apps.lint.api.*
import com.estatia.realestate.apps.lint.compose.*
import com.estatia.realestate.apps.lint.security.*
import com.estatia.realestate.apps.lint.performance.*
import com.estatia.realestate.apps.lint.testing.*
import com.estatia.realestate.apps.lint.policy.*

/**
 * The central authority for Estatia engineering rules.
 */
class EstatiaIssueRegistry : IssueRegistry() {
    
    override val issues = listOf(
        // Architecture
        SuppressionPolicyDetector.ISSUE,
        ModuleDependencyDetector.FEATURE_COUPLING_ISSUE,
        ModuleDependencyDetector.IMPLEMENTATION_LEAKAGE_ISSUE,
        CanaryHeartbeatDetector.ISSUE,
        
        // Concurrency
        CoroutineCancellationDetector.ISSUE,
        ForbiddenScopeDetector.FORBIDDEN_SCOPE_ISSUE,
        DispatcherInjectionDetector.ISSUE,
        ThreadSafetyDetector.ISSUE,
        ChaosSynchronizationDetector.ISSUE,
        ConfinementDetector.ISSUE,
        UnsafeStateCollectionDetector.ISSUE,
        Law019_SecretConcurrencyDetector.ISSUE,
        Law020_UnusedAsyncDetector.ISSUE,
        Law021_MisplacedExceptionHandlerDetector.ISSUE,
        
        // API
        Law009_ResultWrapperDetector.ISSUE,
        Law009_FailureSmugglingDetector.ISSUE,
        Law009_DangerousFallbackDetector.ISSUE,
        VisibilityModifierDetector.ISSUE,
        
        // Compose
        BusinessLogicInComposeDetector.ISSUE,
        RememberMissingDetector.ISSUE,
        StateOwnershipDetector.ISSUE,
        Law027_ComposeArchitectureLeakageDetector.ISSUE,
        Law025_ComposeMutableSingletonReadDetector.ISSUE,
        ComposePerformanceDetector.EXPENSIVE_RECOMPOSITION_ISSUE,
        HardcodedColorDimensionDetector.ISSUE,
        HardcodedStringDetector.ISSUE,
        DesignSystemDetector.ISSUE,
        
        // Security
        SensitiveLoggingDetector.ISSUE,
        HardcodedSecretsDetector.ISSUE,
        
        // Performance
        UnboundedBufferDetector.ISSUE,
        Law007_DirectSystemTimeProdDetector.ISSUE,
        Law015_DirectSystemTimeTestDetector.ISSUE,
        MainThreadWorkDetector.ISSUE,
        Law023_LifecycleLeakDetector.ISSUE,
        Law024_ContextLeakDetector.ISSUE,
        
        // Testing
        MockInProductionDetector.ISSUE,
        
        // Code Health
        MagicNumberDetector.ISSUE,
        Law028_SpaghettiMethodDetector.ISSUE,
        Law029_GodObjectDetector.ISSUE,
        Law030_OrchestrationMonsterDetector.ISSUE
    )

    /**
     * Pinning the API version ensures cross-environment compatibility.
     * Value 16 corresponds to Lint API 31.4.0 (AGP 8.4+).
     */
    override val api: Int = 16

    override val minApi: Int = 12

    override val vendor: Vendor = Vendor(
        vendorName = "Estatia Engineering",
        feedbackUrl = "https://github.com/estatia/realestate/issues",
        contact = "https://github.com/estatia/realestate"
    )
}
