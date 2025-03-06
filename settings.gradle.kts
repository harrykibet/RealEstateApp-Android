pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("de.fayard.refreshVersions") version "0.60.5" // Ensure this is the latest version
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "RealEstateApp"

include(":app")
include(":core")
include(":feature_home")
include(":feature_auth")
include(":ui_components")
include(":feature_profile")
include(":feature_search")
include(":feature_property")
include(":feature_intelligence")
include(":feature_payments")
include(":feature_marketplace")
include(":feature_notifications")
include(":feature_chats")
include(":feature_favorites")
include(":localization")
include(":security")
include(":feature_comments")
include(":feature_settings")
include(":feature_service")
include(":feature_analytics")
include(":feature_mediaplayer")
