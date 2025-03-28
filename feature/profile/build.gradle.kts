plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.room)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.firebase)
    alias(libs.plugins.realestateapp.hilt)
}

android {
    namespace = "com.application.real_estate_app.feature_profile"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(platform(libs.compose.bom))
    implementation(libs.material3)

    implementation(libs.firebase.auth)

    implementation(libs.bundles.navigation)

    implementation(libs.eventbus)

    implementation(projects.core.common)
    implementation(projects.core.data)
}
