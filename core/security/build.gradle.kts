plugins {
    alias(libs.plugins.estatia.android.core)
}

android {
    namespace = "com.estatia.realestate.apps.core.security"

    defaultConfig {

        val mapsApiKey = project.findProperty("MAPS_API_KEY")
            ?: error(
                """
                MAPS_API_KEY is missing.
                Add it to gradle.properties or pass:
                -PMAPS_API_KEY=<your_key>
                """.trimIndent()
            )

        buildConfigField(
            "String",
            "MAPS_API_KEY",
            "\"$mapsApiKey\""
        )
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.security.crypto.ktx)

    implementation(projects.core.common)

    implementation(libs.bundles.bouncy)
}