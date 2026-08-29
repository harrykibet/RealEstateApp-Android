package com.estatia.realestate.apps

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

/**
 * Common Android configuration shared across application, library,
 * and dynamic feature modules.
 *
 * AGP 8+ disables BuildConfig generation by default for libraries,
 * therefore it must be explicitly enabled.
 */

private fun configureCommonBuildFeatures(
    extension: Any
) {
    when (extension) {
        is ApplicationExtension -> {
            extension.buildFeatures {
                buildConfig = true
            }
        }

        is LibraryExtension -> {
            extension.buildFeatures {
                buildConfig = true
            }
        }

        is DynamicFeatureExtension -> {
            extension.buildFeatures {
                buildConfig = true
            }
        }
    }
}

private fun ApplicationExtension.applyCommon(
    project: Project,
    isDynamicFeature: Boolean = false
) {
    compileSdk = 37

    configureCommonBuildFeatures(this)

    testOptions {
        managedDevices {
            allDevices.register<ManagedVirtualDevice>("pixel2Api34") {
                device = "Pixel 2"
                apiLevel = 34
                systemImageSource = "aosp"
            }
        }
    }

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true

        // Required for AppAuth (AWS Cognito / OIDC) even in libraries/tests
        manifestPlaceholders["appAuthRedirectScheme"] = "com.estatia.realestate.apps.auth"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false

            if (!isDynamicFeature) {
                proguardFiles(
                    getDefaultProguardFile(
                        "proguard-android-optimize.txt"
                    )
                )
                if (project.file("proguard-rules.pro").exists()) {
                    proguardFiles("proguard-rules.pro")
                }
            }
        }
    }
}

private fun LibraryExtension.applyCommon(
    project: Project,
    isDynamicFeature: Boolean = false
) {
    compileSdk = 37

    configureCommonBuildFeatures(this)

    testOptions {
        managedDevices {
            allDevices.register<ManagedVirtualDevice>("pixel2Api34") {
                device = "Pixel 2"
                apiLevel = 34
                systemImageSource = "aosp"
            }
        }
    }

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true

        // Required for AppAuth (AWS Cognito / OIDC) even in libraries/tests
        manifestPlaceholders["appAuthRedirectScheme"] = "com.estatia.realestate.apps.auth"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false

            if (!isDynamicFeature) {
                proguardFiles(
                    getDefaultProguardFile(
                        "proguard-android-optimize.txt"
                    )
                )
                if (project.file("proguard-rules.pro").exists()) {
                    proguardFiles("proguard-rules.pro")
                }
            }
        }
    }
}

private fun DynamicFeatureExtension.applyCommon(
    project: Project,
    isDynamicFeature: Boolean = true
) {
    compileSdk = 37

    configureCommonBuildFeatures(this)

    testOptions {
        managedDevices {
            allDevices.register<ManagedVirtualDevice>("pixel2Api34") {
                device = "Pixel 2"
                apiLevel = 34
                systemImageSource = "aosp"
            }
        }
    }

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
}


// Public API

fun ApplicationExtension.configureAndroidCommon(
    project: Project,
    isDynamicFeatureModule: Boolean = false
) = applyCommon(project, isDynamicFeatureModule)


fun LibraryExtension.configureAndroidCommon(
    project: Project,
    isDynamicFeatureModule: Boolean = false
) = applyCommon(project, isDynamicFeatureModule)


fun DynamicFeatureExtension.configureAndroidCommon(
    project: Project,
    isDynamicFeatureModule: Boolean = true
) = applyCommon(project, isDynamicFeatureModule)
