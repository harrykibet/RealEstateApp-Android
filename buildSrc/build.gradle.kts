plugins {
    `kotlin-dsl` // Enables Kotlin for buildSrc
    kotlin("jvm") version "1.9.10" // Match this with your project Kotlin version
    id("org.sonarqube") version "6.0.1.5171" // Use the latest version of SonarQube plugin
}

repositories {
    mavenCentral()
    google()
    maven { url = uri("https://jitpack.io") } // If you need JitPack
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}
