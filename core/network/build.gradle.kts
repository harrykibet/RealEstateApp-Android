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

    implementation(platform(libs.google.cloud.bom))
    implementation(libs.google.cloud.kms)
    implementation(libs.google.cloud.secretmanager)

    implementation(libs.bundles.bouncy)
    implementation(libs.security.crypto.ktx)

    implementation(libs.caffeine)

    implementation(libs.bundles.analytics)

    implementation(libs.bundles.firebase)



    implementation(libs.play.services.maps)

    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.model)
}