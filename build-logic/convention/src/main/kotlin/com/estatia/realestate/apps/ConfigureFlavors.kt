package com.estatia.realestate.apps

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.ProductFlavor
import com.android.build.api.dsl.TestExtension

// AGP 9.x: flavorDimensions / productFlavors removed from CommonExtension.
// Provide typed overloads for app, library, and test modules.

fun configureFlavors(
    extension: ApplicationExtension,
    flavorConfigurationBlock: ProductFlavor.(flavor: EstatiaFlavor) -> Unit = {}
) {
    extension.apply {
        FlavorDimension.entries.forEach { flavorDimensions += it.name }
        productFlavors {
            EstatiaFlavor.entries.forEach { flavor ->
                register(flavor.name) {
                    dimension = flavor.dimension.name
                    flavorConfigurationBlock(this, flavor)
                    (this).let {
                        flavor.applicationIdSuffix?.let { s -> applicationIdSuffix = s }
                        flavor.versionNameSuffix?.let { s -> versionNameSuffix = s }
                    }
                }
            }
        }
    }
}

fun configureFlavors(
    extension: LibraryExtension,
    flavorConfigurationBlock: ProductFlavor.(flavor: EstatiaFlavor) -> Unit = {}
) {
    extension.apply {
        FlavorDimension.entries.forEach { flavorDimensions += it.name }
        productFlavors {
            EstatiaFlavor.entries.forEach { flavor ->
                register(flavor.name) {
                    dimension = flavor.dimension.name
                    flavorConfigurationBlock(this, flavor)
                }
            }
        }
    }
}

fun configureFlavors(
    extension: TestExtension,
    flavorConfigurationBlock: ProductFlavor.(flavor: EstatiaFlavor) -> Unit = {}
) {
    extension.apply {
        FlavorDimension.entries.forEach { flavorDimensions += it.name }
        productFlavors {
            EstatiaFlavor.entries.forEach { flavor ->
                register(flavor.name) {
                    dimension = flavor.dimension.name
                    flavorConfigurationBlock(this, flavor)
                }
            }
        }
    }
}
