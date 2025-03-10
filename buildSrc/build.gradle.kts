plugins {
    `kotlin-dsl` // Enables Kotlin for buildSrc (relies on embedded Kotlin)
    kotlin("jvm") version "2.0.0" // Use the same version as your project
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    compileOnly(libs.room.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradle.plugin)
    compileOnly(libs.sonarqube.gradle.plugin)
    compileOnly(libs.firebase.crashlytics.gradle.plugin)
    compileOnly(libs.firebase.performance.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    implementation(libs.truth)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("hilt") {
            id = libs.plugins.realestateapp.hilt.get().pluginId
            implementationClass = "convention.HiltConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.realestateapp.android.room.get().pluginId
            implementationClass = "convention.AndroidRoomConventionPlugin"
        }
        register("androidFirebase") {
            id = libs.plugins.realestateapp.android.application.firebase.get().pluginId
            implementationClass = "convention.AndroidApplicationFirebaseConventionPlugin"
        }
        register("sonarqube") {
            id = libs.plugins.realestateapp.sonarqube.get().pluginId
            implementationClass = "convention.SonarqubeConventionPlugin"
        }
    }
}