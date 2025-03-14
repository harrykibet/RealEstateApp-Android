plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.room)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.hilt)
}

android {
    namespace = "com.application.real_estate_app.security"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.security.crypto.ktx)

    // Google Cloud Dependencies (KMS & Secret Manager)
    implementation(platform(libs.google.cloud.bom)) // Version alignment for Google Cloud libraries
    implementation(libs.google.cloud.kms)
    implementation(libs.google.cloud.secretmanager)

    implementation(projects.core)

    implementation(libs.bundles.bouncy)

    implementation(libs.caffeine)

    implementation(libs.bundles.analytics)
}
