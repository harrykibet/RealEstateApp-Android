import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.AppExtension
import com.estatia.realestate.apps.configureFlavors
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        pluginManager.apply("com.estatia.realestate.apps.android.config")
        pluginManager.apply("com.estatia.realestate.apps.android.testing")
        pluginManager.apply("com.estatia.realestate.apps.android.compose")
        pluginManager.apply("com.estatia.realestate.apps.hilt")
        pluginManager.apply("androidx.baselineprofile")
        pluginManager.apply("com.estatia.realestate.apps.sonarqube")
        pluginManager.apply("com.estatia.realestate.apps.android.packaging")
        pluginManager.apply( "com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
        pluginManager.apply("com.estatia.realestate.apps.android.application.firebase")


        extensions.configure<AppExtension>("android") {
            compileSdkVersion(35)

            defaultConfig {
                applicationId = "com.estatia.realestate.apps"
                minSdk = 26
                targetSdk = 35
                versionCode = 1
                versionName = "1.0"
            }

            signingConfigs {
                getByName("debug") {
                    storeFile = rootProject.file("AppKeyStore/debug.keystore")
                    storePassword = "android"
                    keyAlias = "androiddebugkey"
                    keyPassword = "android"
                }

                create("release") {
                    storeFile = rootProject.file("AppKeyStore/keystore.jks")
                    storePassword = "2001birth"
                    keyAlias = "key0"
                    keyPassword = "2001birth"
                }
            }

            buildTypes {
                getByName("release") {
                    isMinifyEnabled = false
                    signingConfig = signingConfigs.getByName("release")
                }
                getByName("debug") {
                    signingConfig = signingConfigs.getByName("debug")
                }
            }
        }
    }
}
