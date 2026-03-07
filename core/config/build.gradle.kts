plugins {
    alias(libs.plugins.estatia.android.core)
    alias(libs.plugins.estatia.firebase)
}

android {
    namespace = "com.estatia.realestate.apps.core.config"
}

dependencies {
    implementation(libs.firebase.config)
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.model)
}