plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.application.real_estate_app.ui_components"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Core Android Libraries
    implementation(Libs.coreKtx)               // Kotlin extensions for Android core components
    implementation(Libs.appCompat)             // Backward-compatible Android UI components
    implementation(Libs.material)              // Material Design UI components

    // Testing Libraries
    testImplementation(Libs.junit)             // JUnit for unit testing
    androidTestImplementation(Libs.testExtJUnit)  // JUnit extensions for Android
    androidTestImplementation(Libs.espressoCore)  // Espresso for UI testing
}