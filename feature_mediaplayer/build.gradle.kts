plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.room)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.firebase)
    alias(libs.plugins.realestateapp.hilt)
}

android {
    namespace = "com.application.real_estate_app.feature_mediaplayer"
}

dependencies {
    implementation(libs.bundles.media3)

    implementation(projects.core)

    implementation(libs.firebase.analytics)
}
