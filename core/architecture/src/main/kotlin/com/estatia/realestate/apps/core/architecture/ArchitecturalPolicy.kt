package com.estatia.realestate.apps.core.architecture

/**
 * The single source of truth for Estatia architectural boundaries.
 * 
 * 🛡️ METADATA INTEGRITY: This policy is organized by Law ID to ensure that 
 * technical constants are semantically linked to architectural intent.
 */
object ArchitecturalPolicy {

    /**
     * LAW-003: Infrastructure leakage policy.
     */
    object Law003 {
        val InfrastructurePackages = setOf(
            "retrofit2",
            "androidx.room",
            "com.google.firebase",
            "okhttp3",
            "com.amplifyframework",
            "android.view",
            "android.widget",
            "androidx.compose",
            "com.estatia.realestate.apps.core.network",
            "com.estatia.realestate.apps.core.database",
            "com.estatia.realestate.apps.core.datastore"
        )
    }

    /**
     * LAW-004: Feature isolation policy.
     */
    object Law004 {
        val AllowedCouplingPackages = setOf(
            ".shared_ui",
            ".navigation",
            ".core.ui"
        )
    }

    /**
     * LAW-008 & LAW-032: Layer purity definitions.
     */
    object Layers {
        val Domain = Layer("domain", "..core.domain..", allowed = setOf(
            "kotlin.", "kotlinx.coroutines.", "javax.inject.", 
            "com.estatia.realestate.apps.core.model.",
            "com.estatia.realestate.apps.core.common.",
            "androidx.annotation.",
            "com.estatia.realestate.apps.core.domain."
        ))
        
        val Model = Layer("model", "..core.model..", allowed = setOf(
            "kotlin.", "kotlinx.coroutines.", "javax.inject.", 
            "com.estatia.realestate.apps.core.model.",
            "com.estatia.realestate.apps.core.common.",
            "androidx.annotation.",
            "kotlinx.parcelize.",
            "android.os.Parcelable"
        ))

        val Feature = Layer("feature", "..feature..")
        val ViewModel = Layer("viewmodel", "..ViewModel")
        val BusinessLogic = Layer("business", "..Repository", listOf("..UseCase", "..Service"))
    }

    /**
     * LAW-030: Constructor orchestration limits.
     */
    object Law030 {
        val AllowedInfrastructureInConstructors = setOf(
            "kotlinx.serialization.json.Json",
            "com.estatia.realestate.apps.core.datastore.EstatiaPreferencesDataSource"
        )
    }

    /**
     * LAW-041: Mandatory Architectural Identity.
     */
    object Law041 {
        val RecognizedArchitecturalAnnotations = setOf(
            "Repository", "Service", "DataSource", "ViewModelMarker", "UseCase", "Manager", 
            "ChaosComponent", "Coordinator", "Helper", "ErrorMapper", "DomainModel", 
            "EntityModel", "AppEntryPoint", "UiState", "Module", "AndroidEntryPoint", 
            "HiltAndroidApp", "Contract"
        )

        val FoundationModules = setOf(
            "core/architecture",
            "core/ksp-architecture",
            "core/testing-architecture",
            "core/canary-violations",
            "lint",
            "build-logic",
            "benchmark"
        )
    }

    data class Layer(
        val name: String, 
        val packagePattern: String, 
        val alternativePatterns: List<String> = emptyList(),
        val allowed: Set<String> = emptySet()
    )

    /**
     * Technical debt that is baselined and currently ignored by Konsist tests.
     */
    object TechnicalDebt {
        val ComplexityBudget = setOf(
            "PropertyCacheEntity", "PlayerTuningConfig", "MarketItem", "EnvironmentState",
            "ListingUiModel", "PropertyDomainModel", "PropertyUpdateFields", "ServiceProvider",
            "UserData", "UserDomainModel", "PropertyEntityModel", "ServiceProviderEntity",
            "UserEntityModel", "ExceptionMapper", "PlaybackOrchestrator", "PlayerManager",
            "MediaCacheWarmer", "AddPropertyDraft"
        )

        val FeatureIsolation = setOf(
            "ChatUiState.kt", "ChatViewModel.kt", "ChatScreen.kt", "CommentsUiState.kt",
            "CommentSheetContent.kt", "MarketUiState.kt", "MarketViewModel.kt", "MarketScreen.kt",
            "PaymentsUiState.kt", "PaymentsViewModel.kt", "PaymentsNavigation.kt",
            "PaymentsScreen.kt", "PaymentsViewModelTest.kt"
        )
    }
}
