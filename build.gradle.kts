// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    kotlin("android") version "2.0.0" apply false
    kotlin("jvm") version "2.0.0" apply false
    kotlin("kapt") version "2.0.0" apply false
    id("com.android.library") version "8.8.2" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.3" apply false
    id("androidx.navigation.safeargs") version "2.8.8" apply false
    id("com.google.dagger.hilt.android") version "2.55" apply false
    id("androidx.room") version "2.6.1" apply false
    id("org.jetbrains.dokka") version "1.9.0" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("org.sonarqube") version "6.0.1.5171" apply false // SonarQube
}

allprojects {
    subprojects {
        configurations.all {
            resolutionStrategy.eachDependency {
                if (requested.group == "com.google.protobuf") {
                    useVersion("4.29.0") // Force latest Protobuf
                }
            }

            exclude(group = "com.google.protobuf", module = "protobuf-javalite")
            exclude(group = "com.google.firebase", module = "protolite-well-known-types")
        }

        afterEvaluate {
            if (plugins.hasPlugin("com.android.application") || plugins.hasPlugin("com.android.library")) {
                extensions.findByType<com.android.build.gradle.internal.dsl.BaseAppModuleExtension>()?.apply {
                    packaging.resources.excludes.add("META-INF/DEPENDENCIES")
                }
            }
        }

        apply(plugin = "org.sonarqube")

        configure<org.sonarqube.gradle.SonarExtension> {
            properties {
                property("sonar.projectKey", "harrykibet_RealEstateApp2")
                property("sonar.organization", "harrykibet")
                property("sonar.host.url", "https://sonarcloud.io")
                property("sonar.sourceEncoding", "UTF-8")

                // For multi-module projects
                property("sonar.sources", "src/main")
                property("sonar.androidVariant", "release")
                property("sonar.exclusions", "**/build/**, **/generated/**")

                property("sonar.tests", "src/test")
                property(
                    "sonar.java.binaries",
                    layout.buildDirectory.dir("intermediates/javac/release/classes").get().asFile
                )
                property(
                    "sonar.junit.reportPaths",
                    layout.buildDirectory.dir("test-results/testDebugUnitTest").get().asFile
                )
                property(
                    "sonar.coverage.jacoco.xmlReportPaths",
                    layout.buildDirectory.file("reports/jacoco/testDebugUnitTestCoverage/testDebugUnitTestCoverage.xml")
                        .get().asFile
                )
            }
        }
    }
}
