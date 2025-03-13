import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidCommonConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val isAppModule = plugins.hasPlugin("com.android.application")

            if (isAppModule) {
                pluginManager.apply("com.android.application") // ✅ Apply correct plugin
                extensions.configure<ApplicationExtension> {
                    configureAndroidCommon()
                }
            } else {
                pluginManager.apply("com.android.library") // ✅ Apply correct plugin
                extensions.configure<LibraryExtension> {
                    configureAndroidCommon()
                    defaultConfig {
                        consumerProguardFiles("consumer-rules.pro") // ✅ Only for library modules
                    }
                }
            }

            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.dokka")
            pluginManager.apply("dagger.hilt.android.plugin")

            // ✅ Configure KotlinOptions properly
            extensions.configure<KotlinAndroidProjectExtension> {
                jvmToolchain(17) // Sets JVM target
            }

            // Ensure Hilt aggregation task is enabled
            extensions.configure<KspExtension> {
                arg("dagger.hilt.android.plugin.enableAggregatingTask", "true")
            }

            tasks.withType<DokkaTask>().configureEach {
                outputDirectory.set(layout.buildDirectory.dir("dokka"))
            }
        }
    }

    // ✅ Extract common Android configurations
    private fun <T> T.configureAndroidCommon() where T : CommonExtension<*, *, *, *, *, *> {
        compileSdk = 35

        defaultConfig.apply {
            minSdk = 26
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables.useSupportLibrary = true
        }

        buildFeatures.viewBinding = true

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }
}
