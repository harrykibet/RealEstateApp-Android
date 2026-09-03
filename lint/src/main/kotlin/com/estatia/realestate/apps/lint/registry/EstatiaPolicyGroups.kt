package com.estatia.realestate.apps.lint.registry

import com.estatia.realestate.apps.lint.architecture.*
import com.estatia.realestate.apps.lint.concurrency.*
import com.estatia.realestate.apps.lint.api.*
import com.estatia.realestate.apps.lint.compose.*
import com.estatia.realestate.apps.lint.security.*
import com.estatia.realestate.apps.lint.performance.*
import com.estatia.realestate.apps.lint.testing.*
import com.estatia.realestate.apps.lint.policy.*

object ArchitectureIssues {
    val all = listOf(
        PackageBoundaryDetector.ISSUE,
        InfrastructureLeakageDetector.ISSUE,
        ModuleDependencyDetector.FEATURE_COUPLING_ISSUE,
        ModuleDependencyDetector.IMPLEMENTATION_LEAKAGE_ISSUE,
        ResponsibilityBoundaryDetector.ISSUE,
        LayerDependencyDetector.ISSUE,
        SuppressionPolicyDetector.ISSUE,
        ComplexityBudgetDetector.CLASS_SIZE_FATAL,
        ComplexityBudgetDetector.CLASS_SIZE_ERROR,
        ComplexityBudgetDetector.CLASS_SIZE_WARNING,
        ComplexityBudgetDetector.METHOD_SIZE_FATAL,
        ComplexityBudgetDetector.METHOD_SIZE_ERROR,
        ComplexityBudgetDetector.METHOD_SIZE_WARNING,
        ComplexityBudgetDetector.CONSTRUCTOR_DEPENDENCY_ERROR,
        ComplexityBudgetDetector.CONSTRUCTOR_DEPENDENCY_WARNING
    )
}

object ConcurrencyIssues {
    val all = listOf(
        CoroutineCancellationDetector.ISSUE,
        ForbiddenScopeDetector.FORBIDDEN_SCOPE_ISSUE,
        DispatcherInjectionDetector.ISSUE,
        ThreadSafetyDetector.ISSUE,
        ChaosSynchronizationDetector.ISSUE,
        ConfinementDetector.ISSUE,
        UnsafeStateCollectionDetector.ISSUE,
        StructuredConcurrencyDetector.SECRET_CONCURRENCY_ISSUE,
        StructuredConcurrencyDetector.UNUSED_ASYNC_ISSUE,
        StructuredConcurrencyDetector.MISPLACED_HANDLER_ISSUE
    )
}

object ApiIssues {
    val all = listOf(
        PublicApiContractDetector.MUTABLE_STATE_ISSUE,
        PublicApiContractDetector.BACKING_PROPERTY_CONVENTION_ISSUE,
        PublicApiContractDetector.IMPLEMENTATION_LEAK_ISSUE,
        ErrorHandlingDetector.MISSING_WRAPPER_ISSUE,
        ErrorHandlingDetector.FAILURE_SMUGGLING_ISSUE,
        ErrorHandlingDetector.DANGEROUS_FALLBACK_ISSUE,
        VisibilityModifierDetector.ISSUE,
        ComplexityBudgetDetector.PARAMETER_COUNT_ERROR,
        ComplexityBudgetDetector.PARAMETER_COUNT_WARNING
    )
}

object ComposeIssues {
    val all = listOf(
        BusinessLogicInComposeDetector.ISSUE,
        RememberMissingDetector.ISSUE,
        StateOwnershipDetector.ISSUE,
        ComposeArchitectureDetector.ARCHITECTURE_LEAKAGE_ISSUE,
        ComposeArchitectureDetector.MUTABLE_SINGLETON_READ_ISSUE,
        ComposePerformanceDetector.EXPENSIVE_RECOMPOSITION_ISSUE,
        HardcodedColorDimensionDetector.ISSUE,
        HardcodedStringDetector.ISSUE,
        DesignSystemDetector.ISSUE
    )
}

object SecurityIssues {
    val all = listOf(
        SensitiveLoggingDetector.ISSUE,
        HardcodedSecretsDetector.ISSUE
    )
}

object PerformanceIssues {
    val all = listOf(
        UnboundedBufferDetector.ISSUE,
        DirectSystemTimeDetector.ISSUE,
        MainThreadWorkDetector.ISSUE,
        LifecycleLeakDetector.LEAK_ISSUE
    )
}

object TestingIssues {
    val all = listOf(
        MockInProductionDetector.ISSUE
    )
}

object CodeHealthIssues {
    val all = listOf(
        MagicNumberDetector.ISSUE
    )
}
