plugins {
    alias(libs.plugins.realestateapp.android.feature)
}

android {
    namespace = "com.application.real_estate_app.feature_search"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.bundles.play)
    implementation(libs.maps.compose)


    implementation(libs.bundles.navigation)

    implementation(projects.core.model)
}
