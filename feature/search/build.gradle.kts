plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.android.compose)
    alias(libs.plugins.realestateapp.hilt)
}

android {
    namespace = "com.application.real_estate_app.feature_search"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.bundles.play)

    implementation(libs.bundles.navigation)

    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.ui)
    implementation(projects.core.common)
}
