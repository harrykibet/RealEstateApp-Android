pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
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
include(":data")
include(":feature_home")
include(":feature_auth")
include(":ui_components")
include(":feature_profile")
include(":feature_explore")
include(":feature_property")
include(":network")
include(":domain")
include(":ai_ml")
include(":feature_payments")
include(":feature_marketplace")
include(":feature_notifications")
include(":feature_chats")
include(":feature_favorites")
include(":localization")
include(":security")
