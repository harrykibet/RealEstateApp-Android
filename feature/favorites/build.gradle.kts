 plugins {
    alias(libs.plugins.estatia.android.feature)
    alias(libs.plugins.estatia.firebase)
}

android {
    namespace = "com.estatia.realestate.apps.feature.favorites"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.swiperefreshlayout)
    
    implementation(libs.bundles.navigation)

    implementation(projects.core.model)
    implementation(projects.core.domain)
}
