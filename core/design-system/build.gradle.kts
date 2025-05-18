plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.android.compose)
    alias(libs.plugins.realestateapp.hilt)
}

android {
    namespace = "com.application.real_estate_app.core_design_system"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.navigationSuite)

    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)

    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(libs.media3.ui)
}