import com.android.build.api.dsl.ApplicationExtension
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude

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

            dependencies {
                // Apply Firebase BOM for all modules
                val bom = lib.findLibrary("firebase.bom").get()
                "implementation"(platform(bom))

                // Apply Firebase dependencies only in the app module
                if (isAppModule) {
                    "implementation"(lib.findLibrary("firebase.analytics").get())
                    "implementation"(lib.findLibrary("firebase.perf").get()) {
                        exclude(group = "com.google.protobuf", module = "protobuf-javalite")
                        exclude(group = "com.google.firebase", module = "protolite-well-known-types")
                    }
                    "implementation"(lib.findLibrary("firebase.crashlytics").get())
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
