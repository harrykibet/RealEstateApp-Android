package com.estatia.realestate.apps.graph

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ModuleGraphPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (target != target.rootProject) return

        val generateModuleGraphs = target.tasks.register("generateModuleGraphs", GenerateModuleGraphsTask::class.java) {
            group = "reporting"
            description = "Generates module dependency graphs from the resolved Gradle configuration graph."

            dotBinary.set(
                target.providers.gradleProperty("estatia.graphviz.dot")
                    .orElse(target.providers.environmentVariable("GRAPHVIZ_DOT"))
                    .orElse(target.providers.provider {
                        listOf(
                            "C:/Program Files/Graphviz/bin/dot.exe",
                            "/opt/homebrew/bin/dot", "/usr/local/bin/dot", "/usr/bin/dot"
                        ).find { File(it).exists() } ?: "dot"
                    })
            )
            outputDir.set(target.layout.projectDirectory.dir("docs/images/graph"))
        }

        val calculateImpact = target.tasks.register("calculateImpact") {
            group = "verification"
            description = "Calculates the impact of changes on the codebase."
        }

        // Defer input collection until all projects are evaluated to capture full graph
        target.gradle.projectsEvaluated {
            val allSubprojects = target.subprojects
            val modules = allSubprojects.map { it.path }.toSet()
            val edges = ModuleGraphExtractor.extractEdges(allSubprojects)
            
            generateModuleGraphs.configure {
                this.modules.set(modules)
                this.edges.set(edges.map { "${it.from}|${it.to}|${it.type}" })
            }
            
            calculateImpact.configure {
                doLast {
                    val diffOutput = try {
                        ProcessBuilder("git", "diff", "--name-only", "origin/main").start().inputStream.bufferedReader().readText().lines().filter { it.isNotBlank() }
                    } catch (_: Exception) { emptyList<String>() }

                    if (diffOutput.isEmpty()) {
                        println("IMPACT_TASKS=test lint")
                        return@doLast
                    }

                    val fullImpact = ModuleGraphExtractor.findImpactedModules(diffOutput, modules, edges, target.projectDir)
                    val tasks = fullImpact.flatMap { listOf("$it:lintDemoDebug", "$it:testDemoDebugUnitTest") }
                    println("IMPACT_TASKS=" + tasks.joinToString(" "))
                }
            }
        }
    }
}
