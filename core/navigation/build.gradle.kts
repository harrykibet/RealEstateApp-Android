plugins {
    alias(libs.plugins.estatia.android.core)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.estatia.realestate.apps.core.navigation"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.navigation)
}
