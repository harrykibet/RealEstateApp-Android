import com.android.build.api.dsl.ApplicationExtension
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
            pluginManager.apply("com.android.library") // For feature modules
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.dokka")
            pluginManager.apply("dagger.hilt.android.plugin")

            extensions.configure<ApplicationExtension> {
                compileSdk = 35

                defaultConfig {
                    minSdk = 26
                    targetSdk = 35
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    vectorDrawables.useSupportLibrary = true
                }

                buildFeatures.viewBinding = true

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
            }

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
}
