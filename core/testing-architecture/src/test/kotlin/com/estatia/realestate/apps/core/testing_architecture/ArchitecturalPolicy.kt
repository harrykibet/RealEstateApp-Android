package com.estatia.realestate.apps.core.testing_architecture

/**
 * The single source of truth for Estatia architectural boundaries.
 * 
 * Rules defined here are enforced by Konsist and (where possible) Gradle module graphs.
 */
object ArchitecturalPolicy {

    /**
     * Infrastructure packages that must never leak into pure layers (Domain, Model).
     */
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

    /**
     * Packages allowed in the Domain layer (pure Kotlin/Java + safe abstractions).
     */
    val DomainAllowedPackages = setOf(
        "kotlin.",
        "kotlinx.coroutines.",
        "javax.inject.",
        "com.estatia.realestate.apps.core.model.",
        "com.estatia.realestate.apps.core.common.",
        "androidx.annotation.",
        "com.estatia.realestate.apps.core.domain." // Self
    )

    /**
     * Specific rules for each architectural layer.
     */
    object Layers {
        val Domain = Layer("domain", "..core.domain..")
        val Model = Layer("model", "..core.model..")
        val Feature = Layer("feature", "..feature..")
        val ViewModel = Layer("viewmodel", "..ViewModel")
        val BusinessLogic = Layer("business", "..Repository", listOf("..UseCase", "..Service"))
    }

    data class Layer(val name: String, val packagePattern: String, val alternativePatterns: List<String> = emptyList())

    /**
     * Technical debt that is baselined and currently ignored by Konsist tests.
     * New violations will still be caught.
     */
    object TechnicalDebt {
        val ComplexityBudget = setOf(
            "PropertyCacheEntity",
            "PlayerTuningConfig",
            "MarketItem",
            "EnvironmentState",
            "ListingUiModel",
            "PropertyDomainModel",
            "PropertyUpdateFields",
            "ServiceProvider",
            "UserData",
            "UserDomainModel",
            "PropertyEntityModel",
            "ServiceProviderEntity",
            "UserEntityModel",
            "ExceptionMapper",
            "PlaybackOrchestrator",
            "PlayerManager",
            "MediaCacheWarmer",
            "AddPropertyDraft"
        )

        val FeatureIsolation = setOf(
            "ChatUiState.kt",
            "ChatViewModel.kt",
            "ChatScreen.kt",
            "CommentsUiState.kt",
            "CommentSheetContent.kt",
            "MarketUiState.kt",
            "MarketViewModel.kt",
            "MarketScreen.kt",
            "PaymentsUiState.kt",
            "PaymentsViewModel.kt",
            "PaymentsNavigation.kt",
            "PaymentsScreen.kt",
            "PaymentsViewModelTest.kt"
        )
    }
}
