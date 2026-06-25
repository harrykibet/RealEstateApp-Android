import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationConventionPlugin : Plugin<Project> {

    /** Helper to read Gradle properties or environment variables, or fail fast if missing */
    private fun Project.requireSecret(name: String): String =
        providers.gradleProperty(name).orNull
            ?: providers.environmentVariable(name).orNull
            ?: error(
                "Required secret '$name' was not found in Gradle properties or environment variables."
            )

    override fun apply(target: Project) = with(target) {

        pluginManager.apply("com.android.application")
        pluginManager.apply("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
        pluginManager.apply("com.estatia.realestate.apps.hilt")
        pluginManager.apply("com.estatia.realestate.apps.android.config")
        pluginManager.apply("com.estatia.realestate.apps.android.flavors")
        pluginManager.apply("com.estatia.realestate.apps.android.application.firebase")
        pluginManager.apply("com.estatia.realestate.apps.android.testing")
        pluginManager.apply("com.estatia.realestate.apps.android.compose")
        pluginManager.apply("androidx.baselineprofile")
        pluginManager.apply("com.estatia.realestate.apps.android.packaging")

        extensions.configure<ApplicationExtension>("android") {

            compileSdk { version = release(36) }

            buildFeatures {
                buildConfig = true
            }

            defaultConfig {
                applicationId = "com.estatia.realestate.apps"
                minSdk = 26
                targetSdk = 36
                versionCode = 1
                versionName = "1.0"
            }

            // Signing configuration driven entirely by Gradle properties
            signingConfigs {

                getByName("debug") {
                    storeFile = rootProject.file("AppKeyStore/debug.keystore")
                    storePassword = target.requireSecret("DEBUG_STORE_PASSWORD")
                    keyAlias = target.requireSecret("DEBUG_KEY_ALIAS")
                    keyPassword = target.requireSecret("DEBUG_KEY_PASSWORD")
                }

                create("release") {
                    storeFile = rootProject.file("AppKeyStore/keystore.jks")
                    storePassword = target.requireSecret("RELEASE_STORE_PASSWORD")
                    keyAlias = target.requireSecret("RELEASE_KEY_ALIAS")
                    keyPassword = target.requireSecret("RELEASE_KEY_PASSWORD")
                }
            }

            // Build types
            buildTypes {
                getByName("debug") {
                    signingConfig = signingConfigs.getByName("debug")
                    isMinifyEnabled = false
                }

                getByName("release") {
                    signingConfig = signingConfigs.getByName("release")
                    isMinifyEnabled = true
                }
            }
        }
    }
}