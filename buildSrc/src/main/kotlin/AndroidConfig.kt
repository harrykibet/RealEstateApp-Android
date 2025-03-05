@Suppress("constPropertyName", "MemberVisibilityCanBePrivate")
object AndroidConfig {

    // SDK and App Config
    const val compileSdk = 35
    const val minSdk = 26
    const val targetSdk = compileSdk
    const val applicationId = "com.application.real_estate_app"
    const val versionCode = 1
    const val versionName = "1.0"
    const val jvmTarget = "17"

    // Testing
    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    // Dokka
    const val dokkaPath = "dokka"

    // Proguard Files
    const val proguardOptimizationFile = "proguard-android-optimize.txt"
    const val proguardRulesFile = "proguard-rules.pro"
    const val proguardConsumerRulesFile = "consumer-rules.pro"

    // Namespaces
    const val appNamespace = applicationId
    const val coreNamespace = "$appNamespace.core"
    const val featureAnalyticsNamespace = "$appNamespace.feature_analytics"
    const val featureChatsNamespace = "$appNamespace.feature_chats"
    const val featureCommentsNamespace = "$appNamespace.feature_comments"
    const val featureFavoritesNamespace = "$appNamespace.feature_favorites"
    const val featureHomeNamespace = "$appNamespace.feature_home"
    const val featureProfileNamespace = "$appNamespace.feature_profile"
    const val featureSearchNamespace = "$appNamespace.feature_search"
    const val featureSettingsNamespace = "$appNamespace.feature_settings"
    const val uiComponentsNamespace = "$appNamespace.ui_components"
    const val featureAuthNamespace = "$appNamespace.feature_auth"
    const val featureIntelligenceNamespace = "$appNamespace.feature_intelligence"
    const val featureMarketplaceNamespace = "$appNamespace.feature_marketplace"
    const val featureMediaPlayerNamespace = "$appNamespace.feature_mediaplayer"
    const val featureNotificationsNamespace = "$appNamespace.feature_notifications"
    const val featurePaymentsNamespace = "$appNamespace.feature_payments"
    const val featurePropertyNamespace = "$appNamespace.feature_property"
    const val featureServiceNamespace = "$appNamespace.feature_service"
    const val localizationNamespace = "$appNamespace.feature_localization"
    const val securityNamespace = "$appNamespace.feature_security"
}
