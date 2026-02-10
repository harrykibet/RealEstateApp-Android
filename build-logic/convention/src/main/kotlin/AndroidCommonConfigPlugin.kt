import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
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
                        configureAndroidCommon()
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
            pluginManager.apply("com.google.devtools.ksp")

            // ✅ Configure KotlinOptions properly
            extensions.configure<KotlinAndroidProjectExtension> {
                jvmToolchain(17) // Sets JVM target
            }

            tasks.withType<DokkaGenerateTask>().configureEach {
                outputDirectory.set(layout.buildDirectory.dir("dokka"))
            }
        }
    }

    // ✅ Extract common Android configurations
    private fun <T> T.configureAndroidCommon() where T : CommonExtension<*, *, *, *, *, *> {
        compileSdk = 36

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
                // ✅ Dynamic feature modules will inherit from app module
                if (!isDynamicFeatureModule) {
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }
        }
    }
}
