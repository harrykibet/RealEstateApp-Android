plugins {
    `kotlin-dsl` // Enables Kotlin for buildSrc (relies on embedded Kotlin)
    kotlin("jvm") version "2.1.10" // Use the same version as your project
}

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(libs.kotlin.stdlib)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}