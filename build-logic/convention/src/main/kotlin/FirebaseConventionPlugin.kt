import com.android.build.api.dsl.ApplicationExtension
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude
import utils.libs

class FirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val isAppModule = pluginManager.hasPlugin("com.android.application")

            // Apply Firebase-related plugins only in the app module
            if (isAppModule) {
                pluginManager.apply("com.google.gms.google-services")
                pluginManager.apply("com.google.firebase.crashlytics")
                pluginManager.apply("com.google.firebase.firebase-perf")
            }

            // Exclude problematic dependencies
            configurations.all {
                exclude(group = "com.google.protobuf", module = "protobuf-javalite")
                exclude(group = "com.google.firebase", module = "protolite-well-known-types")
            }

            dependencies {
                // Apply Firebase BOM for all modules
                val bom = libs.findLibrary("firebase.bom").get()
                "implementation"(platform(bom))

                // Apply Firebase dependencies only in the app module
                if (isAppModule) {
                    "implementation"(libs.findLibrary("firebase.analytics").get())
                    "implementation"(libs.findLibrary("firebase.perf").get())
                    "implementation"(libs.findLibrary("firebase.crashlytics").get())
                }
            }

            // Configure Crashlytics only in the app module
            if (isAppModule) {
                extensions.configure<ApplicationExtension> {
                    buildTypes.configureEach {
                        configure<CrashlyticsExtension> {
                            mappingFileUploadEnabled = true
                        }
                    }
                }
            }
        }
    }
}
