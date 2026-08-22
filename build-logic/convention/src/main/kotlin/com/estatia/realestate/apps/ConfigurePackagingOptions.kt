package com.estatia.realestate.apps

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.TestExtension
import com.android.build.api.dsl.DynamicFeatureExtension
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
            "/META-INF/{AL2.0,LGPL2.1}",
            "META-INF/LICENSE-notice.md",
            "google/protobuf/*.proto"
        )
        val pickFirsts = setOf(
            "META-INF/LICENSE.md",
            "META-INF/LICENSE.txt",
            "META-INF/NOTICE.md",
            "META-INF/NOTICE.txt",
            "META-INF/ASL2.0"
        )
        when (dsl) {
            is ApplicationExtension -> {
                dsl.packaging.resources.excludes.addAll(excludes)
                dsl.packaging.resources.pickFirsts.addAll(pickFirsts)
                dsl.packaging.jniLibs.useLegacyPackaging = false
            }

            is LibraryExtension -> {
                dsl.packaging.resources.excludes.addAll(excludes)
                dsl.packaging.resources.pickFirsts.addAll(pickFirsts)
                dsl.packaging.jniLibs.useLegacyPackaging = false
            }

            is TestExtension -> {
                dsl.packaging.resources.excludes.addAll(excludes)
                dsl.packaging.resources.pickFirsts.addAll(pickFirsts)
                dsl.packaging.jniLibs.useLegacyPackaging = false
            }

            is DynamicFeatureExtension -> {
                dsl.packaging.resources.excludes.addAll(excludes)
                dsl.packaging.resources.pickFirsts.addAll(pickFirsts)
                dsl.packaging.jniLibs.useLegacyPackaging = false
            }
        }
    }
}
