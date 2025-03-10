plugins {
    `kotlin-dsl` // Enables Kotlin for buildSrc (relies on embedded Kotlin)
    kotlin("jvm") version "2.0.0"
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android) // Use the same version as your project
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://jitpack.io")
}
dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}
