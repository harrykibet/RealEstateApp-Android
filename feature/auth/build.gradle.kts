plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.firebase)
    alias(libs.plugins.realestateapp.android.room)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.hilt)
}

android {
    namespace = "com.application.real_estate_app.feature_auth"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)

    implementation(libs.play.services.auth)

    implementation(libs.bundles.navigation)

    implementation(libs.eventbus)

    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.common)
}
