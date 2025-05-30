plugins {
    alias(libs.plugins.estatia.android.config)
    alias(libs.plugins.estatia.android.testing)
    alias(libs.plugins.estatia.hilt)
}

android {
    namespace = "com.estatia.realestate.apps.core.security"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.security.crypto.ktx)

    implementation(projects.core.common)

    implementation(libs.bundles.bouncy)
}
