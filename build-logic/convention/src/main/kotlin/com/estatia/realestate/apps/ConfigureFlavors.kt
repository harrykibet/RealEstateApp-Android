package com.estatia.realestate.apps

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor


fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: EstatiaFlavor) -> Unit = {}
) {
    commonExtension.apply {

        // Register dimensions
        for (dimension in FlavorDimension.values()) {
            flavorDimensions += dimension.name
        }

        // Register flavors
        productFlavors {
            for (flavor in EstatiaFlavor.values()) {
                register(flavor.name) {
                    dimension = flavor.dimension.name

                    flavorConfigurationBlock(this, flavor)

                    // Only for Application modules
                    if (commonExtension is ApplicationExtension && this is ApplicationProductFlavor) {
                        flavor.applicationIdSuffix?.let { applicationIdSuffix = it }
                        flavor.versionNameSuffix?.let { versionNameSuffix = it }
                    }

                    // Library modules won't apply these; they just have the flavor itself
                }
            }
        }
    }
}
