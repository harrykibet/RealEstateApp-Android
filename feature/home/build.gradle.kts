plugins {
    alias(libs.plugins.realestateapp.android.feature)
}

android {
    namespace = "com.application.real_estate_app.feature_home"
}

dependencies {

    implementation(libs.bundles.android)

    implementation(projects.core.model)
    implementation(projects.core.domain)

    implementation(libs.bundles.navigation)

    implementation(libs.bundles.lifecycle)
}
