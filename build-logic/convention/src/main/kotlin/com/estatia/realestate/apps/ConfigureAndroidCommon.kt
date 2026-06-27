package com.estatia.realestate.apps

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion

/**
 * AGP 9.x removed type parameters from CommonExtension and pulled
 * compileSdk / defaultConfig / compileOptions / buildTypes down to
 * concrete subtypes. Configure each arm separately.
 */

private fun applyCompileOptions(extension: Any) {
    // compileOptions is still on CommonExtension in 9.x as a method, but
    // sourceCompatibility/targetCompatibility are accessed via the block.
    // Use when-cast to keep it DRY.
    when (extension) {
        is ApplicationExtension -> extension.applyCommon()
        is LibraryExtension -> extension.applyCommon()
        is DynamicFeatureExtension -> extension.applyCommon(isDynamicFeature = true)
    }
}

private fun ApplicationExtension.applyCommon(isDynamicFeature: Boolean = false) {
    compileSdk = 37
    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }
}

private fun LibraryExtension.applyCommon(isDynamicFeature: Boolean = false) {
    compileSdk = 37
    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }
}

private fun DynamicFeatureExtension.applyCommon(isDynamicFeature: Boolean = true) {
    compileSdk = 37
    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Public entry points — called from AndroidCommonConfigPlugin
fun ApplicationExtension.configureAndroidCommon(isDynamicFeatureModule: Boolean = false) =
    applyCommon(isDynamicFeatureModule)

fun LibraryExtension.configureAndroidCommon(isDynamicFeatureModule: Boolean = false) =
    applyCommon(isDynamicFeatureModule)

fun DynamicFeatureExtension.configureAndroidCommon(isDynamicFeatureModule: Boolean = true) =
    applyCommon(isDynamicFeatureModule)