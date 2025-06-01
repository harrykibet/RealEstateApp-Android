plugins {
    alias(libs.plugins.estatia.android.config)
    alias(libs.plugins.estatia.android.testing)
    alias(libs.plugins.estatia.hilt)
}

android {
    namespace = "com.estatia.realestate.apps.core.notifications"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(projects.core.model)
}
