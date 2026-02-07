plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.auth"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.bundles.navigation)

    implementation(projects.core.model)
    implementation(projects.core.common)
}
