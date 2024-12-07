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
include(":feature-home")
include(":feature-auth")
include(":ui-components")
include(":feature-profile")
include(":feature_explore")
include(":feature-property")
include(":network")
include(":domain")
include(":machine_learning")
