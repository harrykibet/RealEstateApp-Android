plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.firebase)
    alias(libs.plugins.realestateapp.hilt)
    alias(libs.plugins.realestateapp.sonarqube)
    alias(libs.plugins.realestateapp.android.packaging)
    alias(libs.plugins.realestateapp.android.compose)
    alias(libs.plugins.com.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.application.real_estate_app"

    defaultConfig {
        applicationId = "com.application.real_estate_app"
        versionCode = 1
        targetSdk = 36
        versionName = "1.0"
    }

    signingConfigs {
            getByName("debug") {
                storeFile = rootProject.file("AppKeyStore/debug.keystore")
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }


        create("release") {
            storeFile = rootProject.file("AppKeyStore/keystore.jks")
            storePassword = "2001birth"
            keyAlias = "key0"
            keyPassword = "2001birth"
        }
    }


    buildTypes {
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("release")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
        release{
            signingConfig = signingConfigs.getByName("release")
            // Ensure Baseline Profile is fresh for release builds.
            baselineProfile.automaticGenerationDuringBuild = true
        }
    }
    dynamicFeatures += setOf(":legal", ":compliance")

    hilt {
        enableAggregatingTask = true
    }
}

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.core.splashscreen)

    implementation(libs.bundles.lifecycle)

    implementation(libs.firebase.config)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.appcheck.playintegrity)

    implementation(libs.bundles.play)

    implementation(libs.eventbus)

    implementation(libs.bundles.bouncy)

    implementation(libs.bundles.navigation)
    implementation(libs.androidx.profileinstaller)

    androidTestImplementation(libs.hilt.android.testing)

    implementation(libs.glide)
    ksp(libs.glide.compiler)

    baselineProfile(projects.benchmark.baselineprofile)

    implementation(projects.core.ui)
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.testing)
    implementation(projects.core.security)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.analytics)
    implementation(projects.core.designSystem)
    implementation(projects.core.notifications)
    implementation(projects.core.datastoreProto)


    implementation(projects.feature.auth)
    implementation(projects.feature.home)
    implementation(projects.feature.chats)
    implementation(projects.feature.player)
    implementation(projects.feature.market)
    implementation(projects.feature.search)
    implementation(projects.feature.service)
    implementation(projects.feature.profile)
    implementation(projects.feature.comments)
    implementation(projects.feature.property)
    implementation(projects.feature.settings)
    implementation(projects.feature.payments)
    implementation(projects.feature.favorites)
    implementation(projects.feature.intelligence)

    implementation(projects.lint)
    implementation(projects.localization)
}

baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false

    // Make use of Dex Layout Optimizations via Startup Profiles
    dexLayoutOptimization = true
}


