package com.estatia.realestate.apps

enum class FlavorDimension {
    env // Environment axis
}

enum class EstatiaFlavor(
    val dimension: FlavorDimension,
    val applicationIdSuffix: String? = null,
    val versionNameSuffix: String? = null
) {
    demo(
        dimension = FlavorDimension.env,
        applicationIdSuffix = ".demo",
        versionNameSuffix = "-demo"
    ),
    prod(
        dimension = FlavorDimension.env
    )
}
