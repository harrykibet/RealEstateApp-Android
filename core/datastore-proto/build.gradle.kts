plugins {
    alias(libs.plugins.estatia.android.config)
    alias(libs.plugins.estatia.android.flavors)
    alias(libs.plugins.estatia.android.testing)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.estatia.realestate.apps.core.datastore.proto"
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    generateProtoTasks {
        all().configureEach {
            builtins {

                register("java") {
                    option("lite")
                }

                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    api(libs.protobuf.kotlin.lite)
}
