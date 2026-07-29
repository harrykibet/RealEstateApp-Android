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
                            checkDependencies = false
                            warningsAsErrors = true
                            abortOnError = true
                            disable.add("TrustAllX509TrustManager")
                            disable.add("IconLauncherShape")
                            disable.add("GradleDependency")
                            disable.add("UnusedResources")
                            disable.add("IconLocation")
                        }
                    }
                pluginManager.hasPlugin("com.android.library") ->
                    extensions.configure<com.android.build.api.dsl.LibraryExtension> {
                        lint {
                            checkDependencies = false
                            warningsAsErrors = true
                            abortOnError = true
                            disable.add("TrustAllX509TrustManager")
                            disable.add("IconLauncherShape")
                            disable.add("GradleDependency")
                            disable.add("UnusedResources")
                            disable.add("IconLocation")
                        }
                    }
            }

            dependencies {
                add("lintChecks", project(":lint"))
            }
        }
    }
}
