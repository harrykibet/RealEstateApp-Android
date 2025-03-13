plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.com.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    alias(libs.plugins.org.jetbrains.dokka)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.firebase)
    alias(libs.plugins.realestateapp.hilt)
    alias(libs.plugins.realestateapp.sonarqube)
    alias(libs.plugins.realestateapp.android.packaging)
}

android {
    namespace = "com.application.real_estate_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.application.real_estate_app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    hilt {
        enableAggregatingTask = true
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.dokkaHtml.configure {
        outputDirectory.set(layout.buildDirectory.dir("dokka"))
    }

    kotlinOptions.jvmTarget = "17"
}

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.core.splashscreen)

    // Lifecycle Dependencies
    implementation(libs.bundles.lifecycle)

    // Firebase Dependencies
    implementation(libs.firebase.config)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.appcheck.playintegrity)

    // Google Play Services
    implementation(libs.bundles.play)

    // EventBus
    implementation(libs.eventbus)

    // Security Dependencies
    implementation(libs.bundles.bouncy)

    // Navigation Dependencies
    implementation(libs.bundles.navigation)

    // Hilt Dependencies
    androidTestImplementation(libs.hilt.android.testing)

    // Glide Dependencies
    implementation(libs.glide)
    ksp(libs.glide.compiler)

    // Project Modules
    implementation(projects.core)
    implementation(projects.uiComponents)
    implementation(projects.localization)
    implementation(projects.security)
    implementation(projects.featureService)
    implementation(projects.featureSettings)
    implementation(projects.featureAnalytics)
    implementation(projects.featureMediaplayer)
    implementation(projects.featureIntelligence)
    implementation(projects.featurePayments)
    implementation(projects.featureMarketplace)
    implementation(projects.featureFavorites)
    implementation(projects.featureChats)
    implementation(projects.featureNotifications)
    implementation(projects.featureComments)
    implementation(projects.featureProperty)
    implementation(projects.featureAuth)
    implementation(projects.featureHome)
    implementation(projects.featureSearch)
    implementation(projects.featureProfile)
}


