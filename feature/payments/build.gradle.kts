plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.payments"
}

dependencies {
    // projects.core.* are automatically included by the feature plugin

    testImplementation(testFixtures(projects.core.testingNetwork))
    androidTestImplementation(testFixtures(projects.core.testingNetwork))
}
