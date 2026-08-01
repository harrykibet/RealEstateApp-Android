 plugins {
    alias(libs.plugins.estatia.android.feature)
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
    implementation(projects.core.ui)
    implementation(projects.core.navigation)
    implementation(projects.core.playerUi)
    implementation(projects.core.playerEngine)

    implementation(libs.media3.common)
}
