import org.gradle.api.artifacts.ProjectDependency

buildscript {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.com.android.application)  apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.plugin.compose) apply false
    alias(libs.plugins.com.google.devtools.ksp) apply false
    alias(libs.plugins.com.google.gms.google.services) apply false
    alias(libs.plugins.com.google.firebase.crashlytics) apply false
    alias(libs.plugins.com.google.firebase.perf) apply false
    alias(libs.plugins.androidx.navigation.safeargs.kotlin) apply false
    alias(libs.plugins.com.google.dagger.hilt.android) apply false
    alias(libs.plugins.androidx.room)  apply false
    alias(libs.plugins.org.jetbrains.dokka) apply false
    alias(libs.plugins.com.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
    alias(libs.plugins.org.sonarqube) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.android.dynamic.feature) apply false
    alias(libs.plugins.module.graph)
    alias(libs.plugins.kotlin.serialization)  apply false
}

extensions.configure<com.jraska.module.graph.assertion.GraphRulesExtension>("moduleGraphAssert") {
    maxHeight = 10
    configurations = setOf("api", "implementation")
    
    // 🛡️ Architectural Guardrails:
    // 1. Core modules must never depend on Features (No upward dependencies)
    // 2. Features must never depend on each other (No feature coupling)
    //    Exception: :feature:shared-ui is currently a shared provider (Tech Debt)
    restricted = arrayOf(
        ":core.* -X> :feature.*",
        ":feature.* -X> :feature:(?!shared-ui).*"
    )
}

/**
 * Custom task to generate a Graphviz dot file for module dependencies
 * with NIA-style colors. This replaces the default behavior of the
 * module-graph plugin for visualization purposes.
 */
/**
 * Custom task to generate Graphviz dot files for module dependencies.
 * If 'modules.graph.of.module' is provided, generates a graph for that module and its dependencies.
 * Otherwise, generates a global graph and per-module graphs in each module's directory.
 */
tasks.register("generateModuleGraphs") {
    group = "reporting"
    description = "Generates Graphviz dot files for module dependency graphs."

    doLast {
        val ofModule = project.findProperty("modules.graph.of.module") as? String
        val dotBinary = "C:/Program Files/Graphviz/bin/dot.exe"
        val hasDot = project.file(dotBinary).exists()

        fun generateGraphForProject(target: Project, outputFile: File) {
            val modulesToInclude = mutableSetOf<Project>()
            fun collectDependencies(p: Project) {
                if (modulesToInclude.add(p)) {
                    p.configurations.forEach { config ->
                        config.dependencies.forEach { dep ->
                            if (dep is ProjectDependency) {
                                try {
                                    val depProj = dep::class.java.getMethod("getDependencyProject").invoke(dep) as Project
                                    collectDependencies(depProj)
                                } catch (_: Exception) { }
                            }
                        }
                    }
                }
            }
            collectDependencies(target)

            outputFile.parentFile.mkdirs()
            outputFile.printWriter().use { writer ->
                writer.println("digraph {")
                writer.println("  graph [label=\"${target.path} Dependencies\", labelloc=t, fontsize=20, ranksep=1.2];")
                writer.println("  node [style=filled, fillcolor=\"#bbdefb\", fontname=\"sans-serif\", shape=box, style=\"rounded,filled\"];")

                // Colors
                val appColor = "#CAFFBF"     // Light Green
                val featureColor = "#FFD6A5" // Light Orange
                val coreColor = "#9BF6FF"    // Light Blue
                val testColor = "#A0C4FF"    // Periwinkle
                val otherColor = "#BDB2FF"   // Purple

                modulesToInclude.forEach { p ->
                    val color = when {
                        p.path == ":app" -> appColor
                        p.path.startsWith(":feature") -> featureColor
                        p.path.startsWith(":core") -> coreColor
                        p.path.contains("benchmark") || p.path.contains("test") || p.path == ":lint" -> testColor
                        else -> otherColor
                    }
                    writer.println("  \"${p.path}\" [fillcolor=\"$color\"];")
                }

                modulesToInclude.forEach { p ->
                    p.configurations.forEach { config ->
                        config.dependencies.forEach { dep ->
                            if (dep is ProjectDependency) {
                                try {
                                    val depProj = dep::class.java.getMethod("getDependencyProject").invoke(dep) as Project
                                    if (modulesToInclude.contains(depProj)) {
                                        writer.println("  \"${p.path}\" -> \"${depProj.path}\"")
                                    }
                                } catch (_: Exception) { }
                            }
                        }
                    }
                }
                writer.println("}")
            }
        }

        if (ofModule != null) {
            val targetProj = project.rootProject.allprojects.find { it.path == ofModule }
                ?: throw IllegalArgumentException("Module $ofModule not found")
            val output = project.file("${project.layout.buildDirectory.get()}/reports/graph/${targetProj.name}_graph.gv")
            generateGraphForProject(targetProj, output)
            println("Graph generated for $ofModule at ${output.absolutePath}")
        } else {
            // 1. Generate Global Graph
            val globalOutput = project.file("${project.layout.buildDirectory.get()}/reports/graph/global_module_graph.gv")
            val allModules = project.rootProject.allprojects
            
            globalOutput.parentFile.mkdirs()
            globalOutput.printWriter().use { writer ->
                writer.println("digraph {")
                writer.println("  graph [label=\"Estatia Global Module Graph\", labelloc=t, fontsize=30, ranksep=1.4];")
                writer.println("  node [style=filled, fillcolor=\"#bbdefb\", fontname=\"sans-serif\", shape=box, style=\"rounded,filled\"];")
                
                allModules.forEach { p ->
                    val color = when {
                        p.path == ":app" -> "#CAFFBF"
                        p.path.startsWith(":feature") -> "#FFD6A5"
                        p.path.startsWith(":core") -> "#9BF6FF"
                        p.path.contains("benchmark") || p.path.contains("test") || p.path == ":lint" -> "#A0C4FF"
                        else -> "#BDB2FF"
                    }
                    writer.println("  \"${p.path}\" [fillcolor=\"$color\"];")
                }

                allModules.forEach { p ->
                    p.configurations.forEach { config ->
                        config.dependencies.forEach { dep ->
                            if (dep is ProjectDependency) {
                                try {
                                    val depProj = dep::class.java.getMethod("getDependencyProject").invoke(dep) as Project
                                    writer.println("  \"${p.path}\" -> \"${depProj.path}\"")
                                } catch (_: Exception) { }
                            }
                        }
                    }
                }
                writer.println("}")
            }
            println("Global graph generated.")

            // 2. Generate Per-Module Graphs
            allModules.forEach { p ->
                if (p == project.rootProject) return@forEach
                val moduleOutput = project.file("${p.projectDir}/module_graph.gv")
                generateGraphForProject(p, moduleOutput)
            }
            println("Per-module graphs generated in each module's root directory.")
        }
    }
}

