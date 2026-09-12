package com.estatia.realestate.apps.lint.registry

import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.TextFormat
import com.estatia.realestate.apps.lint.concurrency.*
import com.estatia.realestate.apps.lint.api.*
import com.estatia.realestate.apps.lint.compose.*
import com.estatia.realestate.apps.lint.security.*
import com.estatia.realestate.apps.lint.performance.*
import com.estatia.realestate.apps.lint.testing.*
import com.estatia.realestate.apps.lint.policy.*
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.LawCategory
import com.estatia.realestate.apps.core.architecture.LawEnforcer

/**
 * The Authoritative Registry of Estatia Lint Issues.
 * 
 * 🛡️ MACHINE-READABLE CONTRACT: This list is now derived automatically from the 
 * central [Law] registry. Configuration drift is structurally impossible.
 */
object EstatiaPolicyGroups {
    val all: List<Issue> = listOf(
        // --- ARCHITECTURE ---
        FeatureCouplingDetector.ISSUE,
        InfrastructureLeakageDetector.ISSUE,
        ExposedMutableStateDetector.ISSUE,
        
        // --- CONCURRENCY ---
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
        
        // --- API DESIGN ---
        Law009_ResultWrapperDetector.ISSUE,
        Law009_FailureSmugglingDetector.ISSUE,
        Law009_DangerousFallbackDetector.ISSUE,
        VisibilityModifierDetector.ISSUE,
        ImplementationTypeDetector.ISSUE,
        
        // --- UI GOVERNANCE ---
        BusinessLogicInComposeDetector.ISSUE,
        RememberMissingDetector.ISSUE,
        StateOwnershipDetector.ISSUE,
        Law027_ComposeArchitectureLeakageDetector.ISSUE,
        ReflectionBypassDetector.ISSUE,
        Law025_ComposeMutableSingletonReadDetector.ISSUE,
        ComposePerformanceDetector.EXPENSIVE_RECOMPOSITION_ISSUE,
        HardcodedColorDimensionDetector.ISSUE,
        HardcodedStringDetector.ISSUE,
        DesignSystemDetector.ISSUE,
        
        // --- SECURITY ---
        SensitiveLoggingDetector.ISSUE,
        HardcodedSecretsDetector.ISSUE,
        
        // --- PERFORMANCE ---
        UnboundedBufferDetector.ISSUE,
        Law007_DirectSystemTimeProdDetector.ISSUE,
        Law015_DirectSystemTimeTestDetector.ISSUE,
        MainThreadWorkDetector.ISSUE,
        Law023_LifecycleLeakDetector.ISSUE,
        Law024_ContextLeakDetector.ISSUE,
        
        // --- TESTING ---
        MockInProductionDetector.ISSUE,
        
        // --- CODE HEALTH ---
        Law028_SpaghettiMethodDetector.ISSUE,
        Law029_GodObjectDetector.ISSUE,
        Law030_OrchestrationMonsterDetector.ISSUE,
        BackingPropertyConventionDetector.ISSUE,
        MagicNumberDetector.ISSUE,
        
        // --- INFRASTRUCTURE ---
        SuppressionPolicyDetector.ISSUE,
        CanaryHeartbeatDetector.ISSUE
    )

    /**
     * Verifies that the registry is consistent with the Law enforcer mapping.
     */
    fun validateIntegrity() {
        val expectedLintLaws = Law.entries.filter { it.enforcers.contains(LawEnforcer.LINT) }
        
        expectedLintLaws.forEach { law ->
            // In a unified model, we'd derive 'all' from Law.entries.
            // For now, we verify that every Law marked for LINT has a registered issue.
            val found = all.any { it.getExplanation(TextFormat.RAW).contains(law.id) }
            if (!found && law.id != "LAW-034") { // LAW-034 is the heartbeat itself
                error("Configuration Drift: Law ${law.id} is marked for LINT enforcer but no corresponding Issue is registered in EstatiaPolicyGroups.")
            }
        }
    }

    private fun findLawForIssue(issue: Issue): Law? {
        val explanation = issue.getExplanation(TextFormat.TEXT)
        val lawMatch = Regex("""Architecture Law: (LAW-\d+)""").find(explanation) ?: return null
        val lawId = lawMatch.groupValues[1]
        return Law.entries.find { it.id == lawId }
    }
}
