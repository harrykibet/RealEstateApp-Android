plugins {
    alias(libs.plugins.realestateapp.android.feature)
    alias(libs.plugins.realestateapp.firebase)
}

android {
    namespace = "com.application.real_estate_app.feature_favorites"
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
