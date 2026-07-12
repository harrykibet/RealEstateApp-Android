package com.estatia.realestate.apps

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion

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
    isDynamicFeature: Boolean = false
) {
    compileSdk = 37

    configureCommonBuildFeatures(this)

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false

            if (!isDynamicFeature) {
                proguardFiles(
                    getDefaultProguardFile(
                        "proguard-android-optimize.txt"
                    ),
                    "proguard-rules.pro"
                )
            }
        }
    }
}

private fun LibraryExtension.applyCommon(
    isDynamicFeature: Boolean = false
) {
    compileSdk = 37

    configureCommonBuildFeatures(this)

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false

            if (!isDynamicFeature) {
                proguardFiles(
                    getDefaultProguardFile(
                        "proguard-android-optimize.txt"
                    ),
                    "proguard-rules.pro"
                )
            }
        }
    }
}

private fun DynamicFeatureExtension.applyCommon(
    isDynamicFeature: Boolean = true
) {
    compileSdk = 37

    configureCommonBuildFeatures(this)

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}


// Public API

fun ApplicationExtension.configureAndroidCommon(
    isDynamicFeatureModule: Boolean = false
) = applyCommon(isDynamicFeatureModule)


fun LibraryExtension.configureAndroidCommon(
    isDynamicFeatureModule: Boolean = false
) = applyCommon(isDynamicFeatureModule)


fun DynamicFeatureExtension.configureAndroidCommon(
    isDynamicFeatureModule: Boolean = true
) = applyCommon(isDynamicFeatureModule)