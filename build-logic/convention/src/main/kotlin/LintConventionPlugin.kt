import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class LintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.hasPlugin("com.android.application") ->
                    extensions.configure<com.android.build.api.dsl.ApplicationExtension> {
                        lint {
                            baseline = file("${project.rootDir}/lint-baseline.xml")
                            checkDependencies = false
                            checkTestSources = true
                            warningsAsErrors = true
                            abortOnError = true
                            disable.add("TrustAllX509TrustManager")
                            disable.add("IconLauncherShape")
                            disable.add("GradleDependency")
                            disable.add("UnusedResources")
                            disable.add("IconLocation")
                            disable.add("UnsafeOptInUsageError") // Suppress noise for initial baseline
                            
                            // Workaround for GradleDetector crash in some environments
                            disable.add("UseTomlInstead")
                            disable.add("GradlePluginVersion")
                        }
                    }
                pluginManager.hasPlugin("com.android.library") ->
                    extensions.configure<com.android.build.api.dsl.LibraryExtension> {
                        lint {
                            baseline = file("${project.rootDir}/lint-baseline.xml")
                            checkDependencies = false
                            checkTestSources = true
                            warningsAsErrors = true
                            abortOnError = true
                            
                            // Ratchet Policy: Any new issues fail the build. 
                            // Existing issues are grandfathered via baseline.
                            disable.add("TrustAllX509TrustManager")
                            disable.add("IconLauncherShape")
                            disable.add("GradleDependency")
                            disable.add("UnusedResources")
                            disable.add("IconLocation")
                            disable.add("UnsafeOptInUsageError") // Suppress noise for initial baseline
                            
                            // Workaround for GradleDetector crash in some environments
                            disable.add("UseTomlInstead")
                            disable.add("GradlePluginVersion")
                        }
                    }
            }

            dependencies {
                add("lintChecks", project(":lint"))
            }
        }
    }
}
