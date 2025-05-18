plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.application.real_estate_app.core_model"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.kotlinx.serialization.json)
}