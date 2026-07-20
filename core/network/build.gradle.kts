plugins {
    alias(libs.plugins.estatia.android.core)
    alias(libs.plugins.estatia.firebase)
}

android {
    namespace = "com.estatia.realestate.apps.core.network"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation(libs.bundles.bouncy)
    implementation(libs.security.crypto.ktx)

    implementation(libs.caffeine)

    implementation(libs.bundles.analytics)

    implementation(libs.bundles.firebase)

    implementation(projects.core.common)
    implementation(projects.core.config)
    implementation(projects.core.model)
}