package com.estatia.realestate.apps

enum class FlavorDimension {
    Env // Environment axis
}

enum class EstatiaFlavor(
    val dimension: FlavorDimension,
    val applicationIdSuffix: String? = null,
    val versionNameSuffix: String? = null
) {
    Demo(
        dimension = FlavorDimension.Env,
        applicationIdSuffix = ".demo",
        versionNameSuffix = "-demo"
    ),
    Prod(
        dimension = FlavorDimension.Env
    )
}
