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
include("core:notifications")
include(":core:data")
include(":core:domain")
include(":core:model")
include(":core:database")
include(":core:security")
include(":core:datastore")
include(":core:design-system")
include(":core:testing")
include(":core:datastore-proto")

include(":compliance")
include(":legal")
include(":lint")
