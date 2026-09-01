package com.estatia.realestate.apps

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import java.util.Locale

private val jacocoExclusions = listOf(
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",
    "**/*Fragment*.*",
    "**/*Activity*.*",
    "**/*Adapter*.*",
    "**/*ViewHolder*.*",
    "**/dagger/hilt/internal/*",
    "**/Hilt_*",
    "**/*_HiltModules*",
    "**/*_Factory*",
    "**/*_MembersInjector*",
    "**/*JsonAdapter.*",
    "**/*\$Serializer.*",
    "**/*\$Companion.*",
    "**/*\$DefaultImpls.*",
    "**/*\$SAM.*",
    "**/*\$1.*",
    "**/*\$Creator.*",
    "**/*_Impl.class", // Room generated
    "**/*_ViewBinding.class" // Butterknife (if any)
)

internal fun Project.configureJacoco(
    androidComponents: AndroidComponentsExtension<*, *, *>,
) {
    pluginManager.apply("jacoco")

    val jacocoVersion = libs.findVersion("jacoco").get().toString()
    extensions.configure<JacocoPluginExtension> {
        toolVersion = jacocoVersion
    }

    androidComponents.onVariants { variant ->
        val variantName = variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val unitTestTaskName = "test${variantName}UnitTest"

        val reportTask = tasks.register<JacocoReport>("jacoco${variantName}Report") {
            dependsOn(unitTestTaskName)

            reports {
                xml.required.set(true)
                html.required.set(true)
            }

            val buildDir = layout.buildDirectory.get().asFile
            val classDirectoriesTree = fileTree("${buildDir}/intermediates/javac/${variant.name}/classes") {
                exclude(jacocoExclusions)
            } + fileTree("${buildDir}/tmp/kotlin-classes/${variant.name}") {
                exclude(jacocoExclusions)
            }

            sourceDirectories.setFrom(files("${projectDir}/src/main/java", "${projectDir}/src/main/kotlin"))
            classDirectories.setFrom(classDirectoriesTree)
            executionData.setFrom(file("${buildDir}/jacoco/${unitTestTaskName}.exec"))
        }

        tasks.register<JacocoCoverageVerification>("jacoco${variantName}Verification") {
            dependsOn(reportTask)

            val buildDir = layout.buildDirectory.get().asFile
            val classDirectoriesTree = fileTree("${buildDir}/intermediates/javac/${variant.name}/classes") {
                exclude(jacocoExclusions)
            } + fileTree("${buildDir}/tmp/kotlin-classes/${variant.name}") {
                exclude(jacocoExclusions)
            }

            sourceDirectories.setFrom(files("${projectDir}/src/main/java", "${projectDir}/src/main/kotlin"))
            classDirectories.setFrom(classDirectoriesTree)
            executionData.setFrom(file("${buildDir}/jacoco/${unitTestTaskName}.exec"))

            violationRules {
                rule {
                    limit {
                        minimum = 0.0.toBigDecimal() // Default, to be overridden per module
                    }
                }
            }
        }
    }
}

/**
 * Configure JaCoCo thresholds for the given module.
 */
fun Project.jacocoThresholds(
    line: Double? = null,
    branch: Double? = null
) {
    tasks.withType<JacocoCoverageVerification>().configureEach {
        violationRules {
            rule {
                line?.let {
                    limit {
                        counter = "LINE"
                        minimum = it.toBigDecimal()
                    }
                }
                branch?.let {
                    limit {
                        counter = "BRANCH"
                        minimum = it.toBigDecimal()
                    }
                }
            }
        }
    }
}
