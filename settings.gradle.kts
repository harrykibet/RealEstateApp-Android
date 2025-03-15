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

// Check if running on CI/CD
// Note: Add "CI=true" to your CI/CD workflow as an environment variable
val isCI = System.getenv("CI")?.toBoolean() ?: false

// Default JVM args (optimized for local development)
var jvmArgs = listOf(
    "-Dfile.encoding=UTF-8",
    "-XX:+UseG1GC",
    "-XX:SoftRefLRUPolicyMSPerMB=1",
    "-XX:ReservedCodeCacheSize=256m",  // Reduce to 256MB for local dev
    "-XX:+HeapDumpOnOutOfMemoryError",
    "-Xmx2g",  // Reduce max heap for local dev
    "-Xms1g",
    "-XX:MaxMetaspaceSize=512m",
    "-XX:+AlwaysPreTouch",
    "-XX:MaxGCPauseMillis=200",
    "-XX:ParallelGCThreads=2",  // Reduce parallel GC threads for local dev
    "-XX:ConcGCThreads=1"
)

// CI/CD optimized settings
if (isCI) {
    jvmArgs = listOf(
        "-Dfile.encoding=UTF-8",
        "-XX:+UseG1GC",
        "-XX:SoftRefLRUPolicyMSPerMB=1",
        "-XX:ReservedCodeCacheSize=512m",
        "-XX:+HeapDumpOnOutOfMemoryError",
        "-Xmx6g",
        "-Xms2g",
        "-XX:MaxMetaspaceSize=1g",
        "-XX:+AlwaysPreTouch",
        "-XX:MaxGCPauseMillis=200",
        "-XX:ParallelGCThreads=4",
        "-XX:ConcGCThreads=2"
    )
}

// Default Kotlin Daemon JVM args for local development
var kotlinJvmArgs = listOf(
    "-Dfile.encoding=UTF-8",
    "-XX:+UseG1GC",
    "-XX:SoftRefLRUPolicyMSPerMB=1",
    "-XX:ReservedCodeCacheSize=256m",  // Smaller for local dev
    "-XX:+HeapDumpOnOutOfMemoryError",
    "-Xmx2g",  // Lower max heap for local
    "-Xms1g",
    "-XX:MaxMetaspaceSize=512m",
    "-XX:+AlwaysPreTouch"
)

// CI/CD optimized settings
if (isCI) {
    kotlinJvmArgs = listOf(
        "-Dfile.encoding=UTF-8",
        "-XX:+UseG1GC",
        "-XX:SoftRefLRUPolicyMSPerMB=1",
        "-XX:ReservedCodeCacheSize=512m",
        "-XX:+HeapDumpOnOutOfMemoryError",
        "-Xmx6g",
        "-Xms2g",
        "-XX:MaxMetaspaceSize=1g",
        "-XX:+AlwaysPreTouch"
    )
}

// Apply Kotlin Daemon JVM arguments
gradle.startParameter.systemPropertiesArgs["kotlin.daemon.jvmargs"] = kotlinJvmArgs.joinToString(" ")

// Apply JVM arguments
gradle.startParameter.systemPropertiesArgs["org.gradle.jvmargs"] = jvmArgs.joinToString(" ")

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
include(":feature")
