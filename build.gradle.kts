plugins {
    id("com.android.application") version "8.8.2" apply false
    id("com.android.library") version "8.8.2" apply false
    alias(libs.plugins.org.jetbrains.kotlin.android)  apply false
    alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.com.google.devtools.ksp) apply false
    alias(libs.plugins.com.google.gms.google.services) apply false
    alias(libs.plugins.com.google.firebase.crashlytics) apply false
    alias(libs.plugins.androidx.navigation.safeargs.kotlin) apply false
    alias(libs.plugins.com.google.dagger.hilt.android) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.org.jetbrains.dokka) apply false
    alias(libs.plugins.com.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
    alias(libs.plugins.org.sonarqube) apply false
}

allprojects {
    subprojects {
        configurations.all {
            resolutionStrategy.eachDependency {
                if (requested.group == "com.google.protobuf") {
                    useVersion("4.29.0") // Force latest Protobuf version
                }
            }

            exclude(group = "com.google.protobuf", module = "protobuf-javalite")
            exclude(group = "com.google.firebase", module = "protolite-well-known-types")
        }

        allprojects {
            subprojects {

                afterEvaluate {
                    if (plugins.hasPlugin("com.android.application") || plugins.hasPlugin("com.android.library")) {
                        extensions.findByType<com.android.build.gradle.BaseExtension>()?.apply {
                            packagingOptions {
                                resources.excludes.addAll(Packaging.excludes)
                            }
                        }
                    }
                }
            }
        }

        apply(plugin = "org.sonarqube")

        configure<org.sonarqube.gradle.SonarExtension> {
            properties {
                property("sonar.projectKey", SonarQubeConfig.projectKey)
                property("sonar.organization", SonarQubeConfig.organization)
                property("sonar.host.url", SonarQubeConfig.hostUrl)
                property("sonar.sourceEncoding", SonarQubeConfig.sourceEncoding)
                property("sonar.sources", SonarQubeConfig.sources)
                property("sonar.androidVariant", SonarQubeConfig.androidVariant)
                property("sonar.exclusions", SonarQubeConfig.exclusions)
                property("sonar.tests", SonarQubeConfig.tests)
                property("sonar.java.binaries", SonarQubeConfig.javaBinaries)
                property("sonar.junit.reportPaths", SonarQubeConfig.junitReportPaths)
                property("sonar.coverage.jacoco.xmlReportPaths", SonarQubeConfig.jacocoReportPaths)
            }
        }
    }
}
