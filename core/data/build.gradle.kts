plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.firebase)
    alias(libs.plugins.realestateapp.hilt)
}

android {
    namespace = "com.application.real_estate_app.core_data"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.bundles.firebase)

    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.network)
}