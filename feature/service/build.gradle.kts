plugins {
    alias(libs.plugins.realestateapp.android.feature)
}

android {
    namespace = "com.application.real_estate_app.feature_service"
}
dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(projects.core.model)
}
