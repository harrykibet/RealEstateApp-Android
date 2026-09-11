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
            "com.estatia.realestate.apps.core.datastore.EstatiaPreferencesDataSource",
            "android.content.Context",
            "android.os.PowerManager",
            "android.hardware.display.DisplayManager",
            "androidx.work.WorkerParameters",
            "io.micrometer.core.instrument.MeterRegistry",
            "androidx.datastore.core.DataStore",
            "com.estatia.realestate.apps.core.config.datasource.AssetConfigDataSource",
            "com.estatia.realestate.apps.core.config.parser.ConfigParser",
            "com.estatia.realestate.apps.core.config.runtime.ConfigStateHolder"
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
            "HiltAndroidApp", "Contract", "Utility", "Mapper", "Policy", "Foundation", 
            "UiPrimitive", "BatteryState", "NetworkState", "EnvironmentState", 
            "AnalyticsState", "AuthState", "PlayerState", "UiAction", "UiEvent",
            "UiScreen", "UiComponent", "UiPrimitiveFunction", "UiRoute"
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

        /**
         * Verifies that the claimed identity matches the structural truth.
         */
        val RoleInvariants = mapOf(
            "Repository" to Invariant(pathContains = "/core/data/|/core/localization/|/app/"),
            "DataSource" to Invariant(pathContains = "/core/network/|/core/database/|/core/config/|/core/data/|/core/datastore/|/core/security/|/core/player-engine/|/app/"),
            "UseCase" to Invariant(pathContains = "/core/domain/"),
            "DomainModel" to Invariant(pathContains = "/core/domain/|/core/model/|/core/analytics/|/core/common/|/core/player-engine/"),
            "EntityModel" to Invariant(pathContains = "/core/network/|/core/database/|/core/data/", mustBeData = true),
            "ViewModelMarker" to Invariant(mustInheritFrom = "androidx.lifecycle.ViewModel"),
            "Contract" to Invariant(mustBeInterface = true),
            "AppEntryPoint" to Invariant(pathContains = "/app/|/feature/"),
            "Coordinator" to Invariant(pathContains = "/core/|/feature/"),
            "Manager" to Invariant(pathContains = "/core/|/app/"),
            "UiState" to Invariant(mustBeDataSealedOrValue = true),
            "Mapper" to Invariant(pathContains = "/core/data/|/core/network/|/core/database/"),
            "Foundation" to Invariant(pathContains = "/core/common/|/core/localization/|/app/|/core/analytics/|/core/datastore/|/core/player-engine/|/core/security/|/core/config/"),
            "Policy" to Invariant(pathContains = "/core/network/|/core/player-engine/|/core/common/"),
            "UiPrimitive" to Invariant(pathContains = "/core/design-system/|/core/ui/|/core/player-ui/"),
            "BatteryState" to Invariant(mustBeDataSealedOrValue = true, pathContains = "/core/common/"),
            "NetworkState" to Invariant(mustBeDataSealedOrValue = true, pathContains = "/core/model/"),
            "EnvironmentState" to Invariant(mustBeDataSealedOrValue = true, pathContains = "/core/model/"),
            "AnalyticsState" to Invariant(mustBeDataSealedOrValue = true, pathContains = "/core/model/|/core/analytics/"),
            "AuthState" to Invariant(mustBeDataSealedOrValue = true, pathContains = "/core/common/|/core/domain/|/core/model/|/feature/auth/"),
            "PlayerState" to Invariant(mustBeDataSealedOrValue = true, pathContains = "/core/player-engine/|/core/player-ui/"),
            "UiAction" to Invariant(mustBeDataSealedOrValue = true, pathContains = "/feature/"),
            "UiEvent" to Invariant(mustBeDataSealedOrValue = true, pathContains = "/feature/"),
            "UiScreen" to Invariant(mustBeComposable = true, pathContains = "/feature/|/app/"),
            "UiComponent" to Invariant(mustBeComposable = true, pathContains = "/feature/|/core/ui/|/core/player-ui/|/app/"),
            "UiPrimitiveFunction" to Invariant(mustBeComposable = true, pathContains = "/core/design-system/|/core/ui/|/app/"),
            "UiRoute" to Invariant(mustBeComposable = true, pathContains = "/feature/|/app/")
        )
    }

    data class Invariant(
        val pathContains: String? = null,
        val mustInheritFrom: String? = null,
        val mustBeInterface: Boolean = false,
        val mustBeData: Boolean = false,
        val mustBeDataSealedOrValue: Boolean = false,
        val mustBeComposable: Boolean = false
    )

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
