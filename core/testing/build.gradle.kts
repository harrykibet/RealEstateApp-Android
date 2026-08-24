plugins {
    alias(libs.plugins.estatia.android.core)
    alias(libs.plugins.estatia.android.compose)
}

android {
    namespace = "com.estatia.realestate.apps.core.testing"
    
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    testFixtures {
        enable = true
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.network)

    testFixturesApi(projects.core.model)
    testFixturesApi(projects.core.common)
    testFixturesApi(projects.core.network)
    testFixturesApi(libs.kotlinx.coroutines.test)
    testFixturesApi(platform(libs.androidx.compose.bom))
    testFixturesApi(libs.androidx.compose.runtime)
}