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

    implementation(projects.core.common)

    implementation(libs.bundles.bouncy)

    implementation(libs.caffeine)

    implementation(libs.bundles.analytics)
}
