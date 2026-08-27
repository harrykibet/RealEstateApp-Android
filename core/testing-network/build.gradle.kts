plugins {
    alias(libs.plugins.estatia.android.core)
}

android {
    namespace = "com.estatia.realestate.apps.core.testing.network"
    
    testFixtures {
        enable = true
    }
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.common)
    
    testFixturesApi(testFixtures(projects.core.testing))
    testFixturesApi(projects.core.network)
    testFixturesApi(projects.core.domain)
    testFixturesApi(projects.core.model)
    testFixturesApi(projects.core.common)
    testFixturesApi(libs.okhttp)
}
