plugins {
    alias(libs.plugins.realestateapp.android.dynamic.feature)
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.testing)
}
android {
    namespace = "com.application.real_estate_app.legal"
}

dependencies {
    implementation(projects.app)
    implementation(libs.core.ktx)
}