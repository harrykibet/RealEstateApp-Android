package com.estatia.realestate.apps

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project
import kotlin.collections.plusAssign

fun configurePackagingOptions(project: Project) {
    val androidComponents =
        project.extensions.findByType(AndroidComponentsExtension::class.java)
            ?: return

    androidComponents.finalizeDsl { dsl ->
        (dsl as CommonExtension<*, *, *, *, *, *>).packaging {
            resources {
                excludes += setOf(
                    "META-INF/DEPENDENCIES",
                    "META-INF/INDEX.LIST",
                    "META-INF/versions/9/OSGI-INF/MANIFEST.MF",
                    "META-INF/versions/11/OSGI-INF/MANIFEST.MF",
                    "META-INF/COPYRIGHT.txt",
                    "/META-INF/{AL2.0,LGPL2.1}"
                )
            }
        }
    }
}