plugins {
    alias(libs.plugins.estatia.android.feature)
    alias(libs.plugins.estatia.firebase)
}

android {
    namespace = "com.estatia.realestate.apps.feature.profile"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.firebase.auth)

    implementation(libs.bundles.navigation)

    implementation(libs.eventbus)
}
