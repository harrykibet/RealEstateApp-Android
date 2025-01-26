
@file:Suppress("ConstPropertyName")
object ProjectModules {
    const val core = ":core"
    const val featureHome = ":feature_home"
    const val featureProperty = ":feature_property"
    const val featureAuth = ":feature_auth"
    const val featureSearch = ":feature_search"
    const val featureProfile = ":feature_profile"
    const val uiComponents = ":ui_components"
    const val featureAnalytics = ":feature_analytics"
    const val app = ":app"
    const val localization = ":localization"
    const val security = ":security"
    const val featureIntelligence = ":feature_intelligence"
    const val featurePayments = ":feature_payments"
    const val featureMarketPlace = ":feature_marketplace"
    const val featureFavorites = ":feature_favorites"
    const val featureChats = ":feature_chats"
    const val featureNotifications = ":feature_notifications"
    const val featureComments = ":feature_comments"
    const val featureSettings = ":feature_settings"
    const val featureService = ":feature_service"

    val allProjectModules = listOf(
        core,
        featureAnalytics,
        featureChats,
        featureComments,
        featureFavorites,
        featureHome,
        featureIntelligence,
        featureMarketPlace,
        featurePayments,
        featureNotifications,
        featureProperty,
        featureAuth,
        featureSearch,
        featureProfile,
        uiComponents,
        localization,
        security,
        featureService,
        featureSettings
    )
}