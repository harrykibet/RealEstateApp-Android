plugins {
    alias(libs.plugins.estatia.android.feature)
    alias(libs.plugins.estatia.firebase)
}

android {
    namespace = "com.estatia.realestate.apps.feature.auth"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.firebase.auth)

    implementation(libs.play.services.auth)

    implementation(libs.bundles.navigation)

    implementation(libs.androidx.credential.manager)
    implementation(libs.androidx.credential.manager.play.services)
    implementation(libs.google.id)
    implementation(libs.play.services.auth)

    implementation(libs.eventbus)

    implementation(projects.core.model)
    implementation(projects.core.common)
}
