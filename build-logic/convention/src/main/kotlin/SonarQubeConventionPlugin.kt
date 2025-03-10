import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.sonarqube.gradle.SonarExtension

class SonarQubeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.sonarqube")

            configure<SonarExtension> {
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
}