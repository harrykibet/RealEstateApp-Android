plugins {
    alias(libs.plugins.estatia.android.core)
}

android {
    namespace = "com.estatia.realestate.apps.core.intelligence"
}

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.material)

    implementation(libs.bundles.lifecycle)

    implementation(libs.bundles.mlkit)
    
    implementation(projects.core.domain)
    implementation(projects.core.common)
}
