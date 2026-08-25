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

    testImplementation(testFixtures(projects.core.testingNetwork))
    testImplementation(testFixtures(projects.core.testingPlayer))
    androidTestImplementation(testFixtures(projects.core.testingNetwork))
    androidTestImplementation(testFixtures(projects.core.testingPlayer))
}
