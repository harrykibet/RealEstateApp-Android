plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.payments"
}

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.material)

    implementation(libs.bundles.lifecycle)
}
