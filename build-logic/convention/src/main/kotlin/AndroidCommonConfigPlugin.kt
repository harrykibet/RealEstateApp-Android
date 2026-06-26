import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import com.estatia.realestate.apps.configureAndroidCommon
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateTask
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidCommonConfigPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val isAppModule = plugins.hasPlugin("com.android.application")
            val isDynamicFeatureModule = plugins.hasPlugin("com.android.dynamic-feature")

            when {
                isAppModule -> {
                    extensions.configure<ApplicationExtension> {
                        configureAndroidCommon()
                    }
                }
                isDynamicFeatureModule -> {
                    extensions.configure<DynamicFeatureExtension> {
                        configureAndroidCommon(isDynamicFeatureModule = true)
                    }
                }
                else -> {
                    pluginManager.apply("com.android.library")
                    extensions.configure<LibraryExtension> {
                        configureAndroidCommon()
                        defaultConfig {
                            consumerProguardFiles("consumer-rules.pro")
                        }
                    }
                }
            }

            // ✅ REMOVED: pluginManager.apply("org.jetbrains.kotlin.android")
            // AGP 9.x bundles Kotlin support — applying this plugin explicitly is now fatal.

            pluginManager.apply("org.jetbrains.dokka")

            // AGP 9.x registers KotlinAndroidProjectExtension via its built-in Kotlin integration.
            // Use withPlugin on "com.android.base" (always present for Android modules) as the
            // safe lifecycle hook, since there's no "org.jetbrains.kotlin.android" to hook on.
            pluginManager.withPlugin("com.android.base") {
                extensions.configure<KotlinAndroidProjectExtension> {
                    jvmToolchain(17)
                }
            }

            tasks.withType<DokkaGenerateTask>().configureEach {
                outputDirectory.set(layout.buildDirectory.dir("dokka"))
            }
        }
    }
}