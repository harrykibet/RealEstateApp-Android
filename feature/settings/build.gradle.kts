plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.settings"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(projects.core.designSystem)
    implementation(projects.core.ui)
    implementation(projects.core.analytics)
    implementation(projects.core.domain)
}
