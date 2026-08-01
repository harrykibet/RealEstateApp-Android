package com.estatia.realestate.apps

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project

fun configurePackagingOptions(project: Project) {
    val androidComponents =
        project.extensions.findByType(AndroidComponentsExtension::class.java) ?: return

    androidComponents.finalizeDsl { dsl ->
        val excludes = setOf(
            "META-INF/DEPENDENCIES",
            "META-INF/INDEX.LIST",
            "META-INF/versions/9/OSGI-INF/MANIFEST.MF",
            "META-INF/versions/11/OSGI-INF/MANIFEST.MF",
            "META-INF/COPYRIGHT.txt",
            "/META-INF/{AL2.0,LGPL2.1}"
        )
        when (dsl) {
            is ApplicationExtension -> {
                dsl.packaging.resources.excludes += excludes
                dsl.packaging.jniLibs.useLegacyPackaging = false
            }

            is LibraryExtension -> {
                dsl.packaging.resources.excludes += excludes
                dsl.packaging.jniLibs.useLegacyPackaging = false
            }
        }
    }
}
