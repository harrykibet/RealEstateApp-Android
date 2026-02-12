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

    private var isDynamicFeatureModule: Boolean = false

    override fun apply(target: Project) {
        with(target) {
            val isAppModule = plugins.hasPlugin("com.android.application")
            isDynamicFeatureModule = plugins.hasPlugin("com.android.dynamic-feature")

            when {
                isAppModule -> {
                    extensions.configure<ApplicationExtension> {
                        configureAndroidCommon()
                    }
                }
                isDynamicFeatureModule -> {
                    extensions.configure<DynamicFeatureExtension> {
                        configureAndroidCommon(isDynamicFeatureModule)
                    }
                }
                else -> {
                    pluginManager.apply("com.android.library")
                    extensions.configure<LibraryExtension> {
                        configureAndroidCommon()
                        defaultConfig {
                            // ✅ Only for library modules
                            consumerProguardFiles("consumer-rules.pro")
                        }
                    }
                }
            }

            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.dokka")

            // ✅ Configure KotlinOptions properly
            extensions.configure<KotlinAndroidProjectExtension> {
                jvmToolchain(17) // Sets JVM target
            }

            tasks.withType<DokkaGenerateTask>().configureEach {
                outputDirectory.set(layout.buildDirectory.dir("dokka"))
            }
        }
    }
}
