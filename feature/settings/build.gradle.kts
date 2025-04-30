plugins {
    alias(libs.plugins.realestateapp.android.feature)
    alias(libs.plugins.org.jetbrains.dokka)
}

android {
    namespace = "com.application.real_estate_app.feature.feature_settings"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
}
