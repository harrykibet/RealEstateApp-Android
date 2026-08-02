plugins {
    alias(libs.plugins.estatia.android.core)
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

    implementation(projects.core.common)
    implementation(projects.core.model)

    implementation(libs.bundles.bouncy)
}