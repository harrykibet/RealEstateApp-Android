plugins {
    alias(libs.plugins.estatia.android.core)
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
    implementation(projects.core.domain)
}