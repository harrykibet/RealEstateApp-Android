package com.estatia.realestate.apps.lint.registry

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.estatia.realestate.apps.lint.architecture.*
import com.estatia.realestate.apps.lint.concurrency.*
import com.estatia.realestate.apps.lint.api.*
import com.estatia.realestate.apps.lint.compose.*
import com.estatia.realestate.apps.lint.security.*
import com.estatia.realestate.apps.lint.performance.*
import com.estatia.realestate.apps.lint.testing.*

class EstatiaIssueRegistry : IssueRegistry() {
    override val issues = listOf(
        // Architecture
        PackageBoundaryDetector.ISSUE,
        InfrastructureLeakageDetector.ISSUE,
        ModuleDependencyDetector.FEATURE_COUPLING_ISSUE,
        ModuleDependencyDetector.IMPLEMENTATION_LEAKAGE_ISSUE,
        
        // Concurrency
        CoroutineCancellationDetector.ISSUE,
        ForbiddenScopeDetector.FORBIDDEN_SCOPE_ISSUE,
        DispatcherInjectionDetector.ISSUE,
        PublicApiContractDetector.MUTABLE_STATE_ISSUE,
        PublicApiContractDetector.BACKING_PROPERTY_CONVENTION_ISSUE,
        PublicApiContractDetector.IMPLEMENTATION_LEAK_ISSUE,
        ThreadSafetyDetector.ISSUE,
        ChaosSynchronizationDetector.ISSUE,
        ConfinementDetector.ISSUE,
        UnsafeStateCollectionDetector.ISSUE,
        StructuredConcurrencyDetector.SECRET_CONCURRENCY_ISSUE,
        StructuredConcurrencyDetector.UNUSED_ASYNC_ISSUE,
        StructuredConcurrencyDetector.MISPLACED_HANDLER_ISSUE,
        
        // API Design
        MissingResultWrapperDetector.ISSUE,
        
        // Compose
        BusinessLogicInComposeDetector.ISSUE,
        RememberMissingDetector.ISSUE,
        StateOwnershipDetector.ISSUE,
        HardcodedColorDimensionDetector.ISSUE,
        HardcodedStringDetector.ISSUE,
        DesignSystemDetector.ISSUE,
        
        // Security
        SensitiveLoggingDetector.ISSUE,
        
        // Performance
        UnboundedBufferDetector.ISSUE,
        DirectSystemTimeDetector.ISSUE,
        
        // Testing
        MockInProductionDetector.ISSUE
    )

    override val api: Int = CURRENT_API

    override val minApi: Int = 12

    override val vendor: Vendor = Vendor(
        vendorName = "Estatia Engineering",
        feedbackUrl = "https://github.com/estatia/realestate/issues",
        contact = "https://github.com/estatia/realestate"
    )
}
