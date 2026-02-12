package com.estatia.realestate.apps

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion

fun <T> T.configureAndroidCommon(isDynamicFeatureModule: Boolean = false)
        where T : CommonExtension<*, *, *, *, *, *> {
    compileSdk = 36

    defaultConfig.apply {
        minSdk = 26
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
            // ✅ Dynamic feature modules will inherit from app module
            if (!isDynamicFeatureModule) {
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }
}