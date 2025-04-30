plugins {
    alias(libs.plugins.realestateapp.android.feature)
    alias(libs.plugins.realestateapp.firebase)
}

android {
    namespace = "com.application.real_estate_app.feature_profile"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.firebase.auth)

    implementation(libs.bundles.navigation)

    implementation(libs.eventbus)
}
