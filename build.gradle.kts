plugins {
    id(Plugins.kotlinAndroid) version PluginVersions.kotlin apply false
    id(Plugins.kotlinJvm) version PluginVersions.kotlin apply false
    id(Plugins.kapt) version PluginVersions.kotlin apply false
    id(Plugins.androidLibrary) version PluginVersions.androidGradle apply false
    id(Plugins.googleServices) version PluginVersions.googleServices apply false
    id(Plugins.firebaseCrashlytics) version PluginVersions.firebaseCrashlytics apply false
    id(Plugins.navigationSafeArgs) version PluginVersions.navigationSafeArgs apply false
    id(Plugins.hilt) version PluginVersions.hilt apply false
    id(Plugins.room) version PluginVersions.room apply false
    id(Plugins.dokka) version PluginVersions.dokka apply false
    id(Plugins.secretsPlugin) version PluginVersions.secretsPlugin apply false
    id(Plugins.sonarQube) version PluginVersions.sonarQube apply false
}

allprojects {
    subprojects {
        configurations.all {
            resolutionStrategy.eachDependency {
                if (requested.group == "com.google.protobuf") {
                    useVersion(Versions.protobuf) // Force latest Protobuf version
                }
            }

            exclude(group = "com.google.protobuf", module = "protobuf-javalite")
            exclude(group = "com.google.firebase", module = "protolite-well-known-types")
        }

        afterEvaluate {
            if (plugins.hasPlugin(Plugins.androidApplication) || plugins.hasPlugin(Plugins.androidLibrary)) {
                extensions.findByType<com.android.build.gradle.internal.dsl.BaseAppModuleExtension>()?.apply {
                    packaging.resources.excludes.addAll(Packaging.excludes)
                }
            }
        }

        apply(plugin = Plugins.sonarQube)

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
