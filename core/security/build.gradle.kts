plugins {
    alias(libs.plugins.estatia.android.core)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.estatia.realestate.apps.core.security"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        // Infrastructure for secure key injection via BuildConfig
        // Add future keys using: buildConfigField("String", "KEY_NAME", "\"value\"")
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.security.crypto.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.model)

    implementation(libs.bundles.bouncy)

    testImplementation(testFixtures(projects.core.testing))
    androidTestImplementation(testFixtures(projects.core.testing))
}