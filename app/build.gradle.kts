plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.firebase)
    alias(libs.plugins.realestateapp.hilt)
    alias(libs.plugins.realestateapp.sonarqube)
    alias(libs.plugins.realestateapp.android.packaging)
    alias(libs.plugins.com.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    namespace = "com.application.real_estate_app"

    defaultConfig {
        applicationId = "com.application.real_estate_app"
        versionCode = 1
        targetSdk = 35
        versionName = "1.0"
    }

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

    androidTestImplementation(libs.hilt.android.testing)

    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(projects.core.interfaces)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.common)
    implementation(projects.core.notifications)
    implementation(projects.core.analytics)
    implementation(projects.core.network)
    implementation(projects.core.utils)
    implementation(projects.core.ui)
    implementation(projects.core.domain)


    implementation(projects.localization)
    implementation(projects.security)

    implementation(projects.feature.service)
    implementation(projects.feature.settings)
    implementation(projects.feature.player)
    implementation(projects.feature.intelligence)
    implementation(projects.feature.payments)
    implementation(projects.feature.market)
    implementation(projects.feature.favorites)
    implementation(projects.feature.chats)
    implementation(projects.feature.comments)
    implementation(projects.feature.property)
    implementation(projects.feature.auth)
    implementation(projects.feature.home)
    implementation(projects.feature.search)
    implementation(projects.feature.profile)
}


