plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.room)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.firebase)
    alias(libs.plugins.realestateapp.hilt)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    namespace = "com.application.real_estate_app.feature_home"
}

dependencies {

    implementation(libs.bundles.android)

    implementation(platform(libs.compose.bom))
    implementation(libs.material3)

    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.media3.exoplayer.hls)

    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)

    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.ui)

    implementation(libs.bundles.navigation)

    implementation(libs.bundles.lifecycle)
}
