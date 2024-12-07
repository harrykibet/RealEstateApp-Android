plugins {
    `kotlin-dsl` // Enables Kotlin for buildSrc
    kotlin("jvm") version "1.9.10" // Match this with your project Kotlin version
}

repositories {
    mavenCentral()
    google()
    maven { url = uri("https://jitpack.io") } // If you need JitPack
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}
