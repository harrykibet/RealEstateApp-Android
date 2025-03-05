@Suppress("constPropertyName", "MemberVisibilityCanBePrivate")
object AndroidConfig {

    // SDK and App Config
    const val compileSdk = 35
    const val minSdk = 26
    const val targetSdk = 35
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
    const val namespace = "com.application.real_estate_app"
    const val coreNamespace = "$namespace.core"
    const val featureAnalyticsNamespace = "$namespace.feature_analytics"
    const val featureChatsNamespace = "$namespace.feature_chats"
    const val featureCommentsNamespace = "$namespace.feature_comments"
    const val featureFavoritesNamespace = "$namespace.feature_favorites"
    const val featureHomeNamespace = "$namespace.feature_home"
    const val featureProfileNamespace = "$namespace.feature_profile"
    const val featureSearchNamespace = "$namespace.feature_search"
    const val featureSettingsNamespace = "$namespace.feature_settings"
    const val uiComponentsNamespace = "$namespace.ui_components"
    const val featureAuthNamespace = "$namespace.feature_auth"
    const val featureIntelligenceNamespace = "$namespace.feature_intelligence"
    const val featureMarketplaceNamespace = "$namespace.feature_marketplace"
    const val featureMediaPlayerNamespace = "$namespace.feature_mediaplayer"
    const val featureNotificationsNamespace = "$namespace.feature_notifications"
    const val featurePaymentsNamespace = "$namespace.feature_payments"
    const val featurePropertyNamespace = "$namespace.feature_property"
    const val featureServiceNamespace = "$namespace.feature_service"
    const val localizationNamespace = "$namespace.feature_localization"
    const val securityNamespace = "$namespace.feature_security"
}
