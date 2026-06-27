import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class AndroidApplicationConventionPlugin : Plugin<Project> {
    private fun Project.optionalSecret(name: String): String? =
        providers.gradleProperty(name).orNull
            ?: providers.environmentVariable(name).orNull

    /**
     * Generates a debug keystore at the given path if it doesn't already exist.
     * Uses the same defaults as the Android SDK debug keystore so behavior is
     * identical to what Android Studio would generate automatically.
     */
    private fun ensureDebugKeystore(keystoreFile: File) {
        if (keystoreFile.exists()) return

        keystoreFile.parentFile?.mkdirs()

        // Delegate to keytool — available in any JDK, which Gradle already requires
        val keytoolPath = "${System.getProperty("java.home")}/bin/keytool"

        val process = ProcessBuilder(
            keytoolPath,
            "-genkeypair",
            "-keystore", keystoreFile.absolutePath,
            "-alias", "androiddebugkey",
            "-keyalg", "RSA",
            "-keysize", "2048",
            "-validity", "10000",
            "-keypass", "android",
            "-storepass", "android",
            "-dname", "CN=Android Debug,O=Android,C=US",
            "-storetype", "JKS"
        )
            .redirectErrorStream(true)
            .start()

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            val output = process.inputStream.bufferedReader().readText()
            error("Failed to generate debug keystore at ${keystoreFile.absolutePath}:\n$output")
        }
    }

    override fun apply(target: Project) = with(target) {

        pluginManager.apply("com.android.application")
        pluginManager.apply("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
        pluginManager.apply("com.estatia.realestate.apps.hilt")
        pluginManager.apply("com.estatia.realestate.apps.android.config")
        pluginManager.apply("com.estatia.realestate.apps.android.flavors")
        pluginManager.apply("com.estatia.realestate.apps.android.application.firebase")
        pluginManager.apply("com.estatia.realestate.apps.android.testing")
        pluginManager.apply("com.estatia.realestate.apps.android.compose")
        pluginManager.apply("com.estatia.realestate.apps.android.packaging")

        extensions.configure<ApplicationExtension>("android") {

            compileSdk { version = release(36) }

            buildFeatures {
                buildConfig = true
            }

            defaultConfig {
                applicationId = "com.estatia.realestate.apps"
                minSdk = 28
                targetSdk = 36
                versionCode = 1
                versionName = "1.0"
            }

            signingConfigs {

                // Debug: generate keystore at runtime if absent.
                // Fixed credentials match Android SDK defaults — safe to hardcode,
                // nothing sensitive, debug APKs are never shipped.
                getByName("debug") {
                    val debugKeystoreFile = rootProject.file("AppKeyStore/debug.keystore")
                    ensureDebugKeystore(debugKeystoreFile)

                    storeFile = debugKeystoreFile
                    storePassword = "android"
                    keyAlias = "androiddebugkey"
                    keyPassword = "android"
                }

                // Release: all values must come from CI/CD environment or local
                // gradle.properties (gitignored). Never committed to source control.
                // Config is skipped entirely if secrets are absent (local dev on
                // non-release builds won't need it).
                val releaseStoreFile = rootProject.file("AppKeyStore/keystore.jks")
                val releaseStorePassword = optionalSecret("RELEASE_STORE_PASSWORD")
                val releaseKeyAlias = optionalSecret("RELEASE_KEY_ALIAS")
                val releaseKeyPassword = optionalSecret("RELEASE_KEY_PASSWORD")

                if (releaseStoreFile.exists()
                    && releaseStorePassword != null
                    && releaseKeyAlias != null
                    && releaseKeyPassword != null
                ) {
                    create("release") {
                        storeFile = releaseStoreFile
                        storePassword = releaseStorePassword
                        keyAlias = releaseKeyAlias
                        keyPassword = releaseKeyPassword
                    }
                }
            }

            buildTypes {
                getByName("debug") {
                    signingConfig = signingConfigs.getByName("debug")
                    isMinifyEnabled = false
                }

                getByName("release") {
                    // Falls back to null signingConfig if release secrets absent —
                    // release builds will fail at assemble time, not at sync time.
                    // This allows local development and sync to work without CI secrets.
                    signingConfig = signingConfigs.findByName("release")
                    isMinifyEnabled = true
                }

                create("benchmark") {
                    initWith(buildTypes.getByName("release"))
                    signingConfig = signingConfigs.getByName("debug") // benchmark uses debug sig
                    matchingFallbacks += listOf("release")
                    isDebuggable = false
                }
            }
        }
    }
}