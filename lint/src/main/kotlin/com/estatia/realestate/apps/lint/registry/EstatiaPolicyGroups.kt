package com.estatia.realestate.apps.lint.registry

import com.estatia.realestate.apps.lint.concurrency.*
import com.estatia.realestate.apps.lint.api.*
import com.estatia.realestate.apps.lint.compose.*
import com.estatia.realestate.apps.lint.security.*
import com.estatia.realestate.apps.lint.performance.*
import com.estatia.realestate.apps.lint.testing.*
import com.estatia.realestate.apps.lint.policy.*

object ArchitectureIssues {
    val all = listOf(
        SuppressionPolicyDetector.ISSUE
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
        Law019_SecretConcurrencyDetector.ISSUE,
        Law020_UnusedAsyncDetector.ISSUE,
        Law021_MisplacedExceptionHandlerDetector.ISSUE
    )
}

object ApiIssues {
    val all = listOf(
        Law009_ResultWrapperDetector.ISSUE,
        Law009_FailureSmugglingDetector.ISSUE,
        Law009_DangerousFallbackDetector.ISSUE,
        VisibilityModifierDetector.ISSUE
    )
}

object ComposeIssues {
    val all = listOf(
        BusinessLogicInComposeDetector.ISSUE,
        RememberMissingDetector.ISSUE,
        StateOwnershipDetector.ISSUE,
        Law027_ComposeArchitectureLeakageDetector.ISSUE,
        Law025_ComposeMutableSingletonReadDetector.ISSUE,
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
        Law023_LifecycleLeakDetector.ISSUE,
        Law024_ContextLeakDetector.ISSUE
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
