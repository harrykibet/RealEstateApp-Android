@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Estatia"

include(":app")
include(":benchmark")
include(":lint")

include(":core:analytics")
include(":core:network")
include(":core:ui")
include(":core:common")
include(":core:navigation")
include(":core:localization")
include(":core:config")
include(":core:notifications")
include(":core:data")
include(":core:domain")
include(":core:model")
include(":core:database")
include(":core:security")
include(":core:datastore")
include(":core:player-engine")
include(":core:player-ui")
include(":core:design-system")
include(":core:testing")
include(":core:datastore-proto")
include(":core:intelligence")

include(":feature:home")
include(":feature:auth")
include(":feature:profile")
include(":feature:search")
include(":feature:property")
include(":feature:payments")
include(":feature:market")
include(":feature:chats")
include(":feature:favorites")
include(":feature:comments")
include(":feature:settings")
include(":feature:shared-ui")
