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
                            // 🛡️ High-tier builds enforce cross-module dependencies
                            checkDependencies = true
                            checkTestSources = true
                            warningsAsErrors = true
                            abortOnError = true
                            disable.add("IconLauncherShape")
                            disable.add("IconLocation")
                            
                            // Workaround for GradleDetector crash in some environments
                            disable.add("UseTomlInstead")
                            disable.add("GradlePluginVersion")
                            disable.add("GradleDependency")
                            disable.add("GradleDeprecated")
                            disable.add("GradleDeprecatedConfiguration")
                            disable.add("OutdatedLibrary")
                            disable.add("GradleDetector")
                        }
                    }
                pluginManager.hasPlugin("com.android.library") ->
                    extensions.configure<com.android.build.api.dsl.LibraryExtension> {
                        lint {
                            baseline = file("${project.rootDir}/lint-baseline.xml")
                            // 🛡️ Critical modules require cross-module analysis
                            checkDependencies = (path == ":core:security" || path == ":core:player-engine")
                            checkTestSources = true
                            warningsAsErrors = true
                            abortOnError = true
                            
                            // Ratchet Policy: Any new issues fail the build. 
                            // Existing issues are grandfathered via baseline.
                            disable.add("IconLauncherShape")
                            disable.add("IconLocation")
                            
                            // Workaround for GradleDetector crash in some environments
                            disable.add("UseTomlInstead")
                            disable.add("GradlePluginVersion")
                            disable.add("GradleDependency")
                            disable.add("GradleDeprecated")
                            disable.add("GradleDeprecatedConfiguration")
                            disable.add("OutdatedLibrary")
                            disable.add("GradleDetector")
                        }
                    }
            }

            dependencies {
                add("lintChecks", project(":lint"))
            }
        }
    }
}
