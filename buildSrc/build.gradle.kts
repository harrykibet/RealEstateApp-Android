plugins {
    `kotlin-dsl` // Enables Kotlin for buildSrc (relies on embedded Kotlin)
    kotlin("jvm") version "2.0.0" // Use the same version as your project
}

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Set to Java 17 (or your required version)
    }
}