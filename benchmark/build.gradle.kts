plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.androidx.baselineprofile)
    alias(libs.plugins.estatia.android.flavors)
}

android {
    namespace = "com.estatia.realestate.apps.benchmark"

    compileSdk = 37

    defaultConfig {
        minSdk = 28
        targetSdk = 37
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.espresso.core)
    implementation(libs.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.junit.junit)
}