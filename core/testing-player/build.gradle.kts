plugins {
    alias(libs.plugins.estatia.android.core)
}

android {
    namespace = "com.estatia.realestate.apps.core.testing.player"
    
    testFixtures {
        enable = true
    }
}

dependencies {
    implementation(projects.core.playerEngine)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.common)
    
    testFixturesApi(testFixtures(projects.core.testing))
    testFixturesApi(projects.core.playerEngine)
    testFixturesApi(libs.bundles.media3)
    testFixturesApi(projects.core.domain)
    testFixturesApi(projects.core.model)
    testFixturesApi(projects.core.common)
}
