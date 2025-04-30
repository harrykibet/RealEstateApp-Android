plugins {
    alias(libs.plugins.realestateapp.android.feature)
}

android {
    namespace = "com.application.real_estate_app.feature_chats"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
}
