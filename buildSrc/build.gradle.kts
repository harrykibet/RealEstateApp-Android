plugins {
    `kotlin-dsl` // Enables Kotlin for buildSrc (relies on embedded Kotlin)
    kotlin("jvm") version PluginVersions.kotlin // Use the same version as your project
}

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(CoreDeps.kotlinStdLib)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(AndroidConfig.jvmTarget.toInt()))
    }
}