plugins {
    alias(libs.plugins.estatia.android.config)
    alias(libs.plugins.estatia.android.testing)
    alias(libs.plugins.estatia.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.estatia.realestate.apps.core.model"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.kotlinx.serialization.json)
}