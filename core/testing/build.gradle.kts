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

    implementation(projects.core.network)
    implementation(projects.core.domain)

    testFixturesApi(projects.core.model)
    testFixturesApi(projects.core.common)
    testFixturesApi(projects.core.network)
    testFixturesApi(projects.core.domain)
    testFixturesApi(libs.kotlinx.coroutines.test)
    testFixturesApi(libs.junit.junit)
    testFixturesApi(libs.hilt.android.testing)
    testFixturesApi(libs.hilt.android)
    testFixturesApi(platform(libs.androidx.compose.bom))
    testFixturesApi(libs.androidx.compose.runtime)
}