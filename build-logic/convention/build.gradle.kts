
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl` // Enables Kotlin for build-logic (relies on embedded Kotlin)
}

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradle.plugin)
    compileOnly(libs.firebase.crashlytics.gradle.plugin)
    compileOnly(libs.firebase.performance.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.dokka.gradle.plugin)
    compileOnly(libs.room.gradle.plugin)
    compileOnly(libs.sonarqube.gradle.plugin)
    implementation(libs.truth)
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
            id = libs.plugins.estatia.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.estatia.android.room.get().pluginId
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidFirebase") {
            id = libs.plugins.estatia.firebase.get().pluginId
            implementationClass = "FirebaseConventionPlugin"
        }
        register("sonarqube") {
            id = libs.plugins.estatia.sonarqube.get().pluginId
            implementationClass = "SonarQubeConventionPlugin"
        }
        register("androidPackaging") {
            id = libs.plugins.estatia.android.packaging.get().pluginId
            implementationClass = "AndroidPackagingConventionPlugin"
        }
        register("androidTesting") {
            id = libs.plugins.estatia.android.testing.get().pluginId
            implementationClass = "TestingConventionPlugin"
        }
        register("androidConfig") {
            id = libs.plugins.estatia.android.config.get().pluginId
            implementationClass = "AndroidCommonConfigPlugin"
        }
        register("androidCompose") {
            id = libs.plugins.estatia.android.compose.get().pluginId
            implementationClass = "ComposeConventionPlugin"
        }
        register("dynamicFeature") {
            id = libs.plugins.estatia.android.dynamic.feature.get().pluginId
            implementationClass = "DynamicFeatureConventionPlugin"
        }
        register("androidApplication") {
            id = libs.plugins.estatia.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidBenchmark") {
            id = libs.plugins.estatia.android.benchmark.get().pluginId
            implementationClass = "AndroidBenchmarkConventionPlugin"
        }
        register("androidFeature") {
            id = libs.plugins.estatia.android.feature.get().pluginId
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidFlavors") {
            id = libs.plugins.estatia.android.flavors.get().pluginId
            implementationClass = "AndroidFlavorsConventionPlugin"
        }
        register("androidCore") {
            id = libs.plugins.estatia.android.core.get().pluginId
            implementationClass = "AndroidCoreConventionPlugin"
        }
    }
}