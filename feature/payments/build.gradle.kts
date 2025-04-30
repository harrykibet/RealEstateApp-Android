plugins {
    alias(libs.plugins.realestateapp.android.feature)
}

android {
    namespace = "com.application.real_estate_app.feature_payments"
}

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.material)

    implementation(libs.bundles.lifecycle)
}
