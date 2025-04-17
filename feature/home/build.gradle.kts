plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.hilt)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    namespace = "com.application.real_estate_app.feature_home"
}

dependencies {

    implementation(libs.bundles.android)

    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.ui)

    implementation(libs.bundles.navigation)

    implementation(libs.bundles.lifecycle)
}
