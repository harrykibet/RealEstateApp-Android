plugins {
    alias(libs.plugins.estatia.android.core)
}

android {
    namespace = "com.estatia.realestate.apps.core.intelligence"
}

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.material)

    implementation(libs.bundles.lifecycle)

    implementation(libs.bundles.mlkit)
    implementation(libs.kotlinx.coroutines.play.services)
    
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.common)

    testImplementation(testFixtures(projects.core.testing))
    androidTestImplementation(testFixtures(projects.core.testing))
}
