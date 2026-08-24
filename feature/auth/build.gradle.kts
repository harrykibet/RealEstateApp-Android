plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.auth"
}

dependencies {

    implementation(libs.androidx.credential.manager)
    implementation(libs.google.id)

    implementation(libs.bundles.navigation)

    testImplementation(testFixtures(projects.core.testing))
    androidTestImplementation(testFixtures(projects.core.testing))
}
