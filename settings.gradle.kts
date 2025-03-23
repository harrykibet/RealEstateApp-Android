pluginManagement {
    includeBuild("build-logic")
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
include(":localization")

include(":feature:home")
include(":feature:auth")
include(":feature:profile")
include(":feature:search")
include(":feature:property")
include(":feature:intelligence")
include(":feature:payments")
include(":feature:market")
include(":feature:chats")
include(":feature:favorites")
include(":feature:comments")
include(":feature:settings")
include(":feature:service")
include(":feature:player")

include(":benchmark")
include(":benchmark:baselineprofile")

include(":core:analytics")
include(":core:network")
include(":core:ui")
include(":core:common")
include(":core:utils")
include("core:notifications")
include(":core:data")
include(":core:domain")
include(":core:model")
include(":core:interfaces")
include(":core:database")
include(":core:security")
include(":core:datastore")
include(":core:designsystem")
include(":core:testing")
include(":core:datastore-proto")

include(":compliance")
include(":legal")
include(":lint")
