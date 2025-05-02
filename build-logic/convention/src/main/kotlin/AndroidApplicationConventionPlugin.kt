import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        pluginManager.apply("com.application.real_estate_app.android.config")
        pluginManager.apply("com.application.real_estate_app.android.testing")
        pluginManager.apply("com.application.real_estate_app.android.compose")
        pluginManager.apply("com.application.real_estate_app.hilt")
        pluginManager.apply("androidx.baselineprofile")
        pluginManager.apply("com.application.real_estate_app.sonarqube")
        pluginManager.apply("com.application.real_estate_app.android.packaging")
        pluginManager.apply( "com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
        pluginManager.apply("com.application.real_estate_app.android.application.firebase")


        extensions.configure<AppExtension>("android") {
            compileSdkVersion(35)

            defaultConfig {
                applicationId = "com.application.real_estate_app"
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
