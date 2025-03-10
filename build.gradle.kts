plugins {
    alias(libs.plugins.com.android.application)  apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android)  apply false
    alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.com.google.devtools.ksp) apply false
    alias(libs.plugins.com.google.gms.google.services) apply false
    alias(libs.plugins.com.google.firebase.crashlytics) apply false
    alias(libs.plugins.androidx.navigation.safeargs.kotlin) apply false
    alias(libs.plugins.com.google.dagger.hilt.android) apply false
    alias(libs.plugins.androidx.room)  apply false
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
                                resources.excludes.addAll(listOf( "META-INF/DEPENDENCIES",
                                    "META-INF/INDEX.LIST",
                                    "META-INF/versions/9/OSGI-INF/MANIFEST.MF",
                                    "META-INF/versions/11/OSGI-INF/MANIFEST.MF",
                                    "META-INF/COPYRIGHT.txt",
                                    "/META-INF/{AL2.0,LGPL2.1}"))
                            }
                        }
                    }
                }
            }
        }
    }
}
