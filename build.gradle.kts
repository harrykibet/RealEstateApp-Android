import java.io.File
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
    alias(libs.plugins.androidx.baselineprofile) apply false
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
    // 3. Infrastructure Isolation (LAW-003):
    //    Pure layers (Domain, Model) must never depend on Infrastructure.
    restricted = arrayOf(
        ":core.* -X> :feature.*",
        ":feature.* -X> :feature:(?!shared-ui).*",
        ":core:domain -X> :core:(network|database|datastore|intelligence|notifications|security)",
        ":core:model -X> :core:(?!common).*"
    )
}

/**
 * Custom task to generate a Graphviz dot file for module dependencies
 * with standard Estatia colors. This replaces the default behavior of the
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
            val edges = mutableSetOf<Pair<String, String>>()
            
            fun collectDeps(p: Project) {
                if (modulesToInclude.add(p)) {
                    // 1. Implicit feature dependencies
                    /**
                     * 💡 ARCHITECTURAL NOTE ON IMPLICIT DEPENDENCIES:
                     * Many core dependencies (UI, Domain, Navigation, etc.) are injected automatically 
                     * via the 'estatia.android.feature' convention plugin. 
                     * 
                     * Because these are applied in 'build-logic' and not the local build file, 
                     * Gradle's standard configuration analysis won't see them here. 
                     * 
                     * ⚠️ MAINTENANCE WARNING: 
                     * If you add a new project dependency to 'AndroidFeatureConventionPlugin.kt', 
                     * you MUST manually update the list below to ensure it appears in the module graphs.
                     */
                    val buildFile = File(p.projectDir, "build.gradle.kts")
                    if (buildFile.exists()) {
                        val content = buildFile.readText()
                        // 1.1 Feature Implicit Dependencies
                        if (content.contains("estatia.android.feature")) {
                            listOf(":core:ui", ":core:common", ":core:domain", ":core:navigation", ":core:model", ":core:design-system", ":core:testing").forEach { depPath ->
                                val depProj = p.rootProject.allprojects.find { it.path == depPath }
                                if (depProj != null) {
                                    edges.add(p.path to depProj.path)
                                    collectDeps(depProj)
                                }
                            }
                        }
                        // 1.2 Core & App Implicit Dependencies
                        if (content.contains("estatia.android.core") || content.contains("estatia.android.application")) {
                            listOf(":core:testing").forEach { depPath ->
                                val depProj = p.rootProject.allprojects.find { it.path == depPath }
                                if (depProj != null) {
                                    edges.add(p.path to depProj.path)
                                    collectDeps(depProj)
                                }
                            }
                        }
                    }

                    // 2. Explicit dependencies through configurations
                    p.configurations.forEach { config ->
                        try {
                            config.dependencies.forEach { dep ->
                                if (dep is ProjectDependency) {
                                    val depProj = dep::class.java.getMethod("getDependencyProject").invoke(dep) as Project
                                    edges.add(p.path to depProj.path)
                                    collectDeps(depProj)
                                }
                            }
                        } catch (_: Exception) { }
                    }
                }
            }
            collectDeps(target)

            outputFile.parentFile.mkdirs()
            outputFile.printWriter().use { writer ->
                writer.println("digraph {")
                writer.println("  graph [label=\"${target.path} Dependencies\", labelloc=t, fontsize=20, ranksep=1.2];")
                writer.println("  node [style=filled, fillcolor=\"#bbdefb\", fontname=\"sans-serif\", shape=box, style=\"rounded,filled\"];")
                
                modulesToInclude.forEach { p ->
                    val color = when {
                        p.path == ":app" -> "#CAFFBF"
                        p.path.startsWith(":feature") -> "#FFD6A5"
                        p.path.startsWith(":core") -> "#9BF6FF"
                        else -> "#BDB2FF"
                    }
                    writer.println("  \"${p.path}\" [fillcolor=\"$color\"];")
                }

                edges.forEach { (from, to) ->
                    writer.println("  \"$from\" -> \"$to\"")
                }
                writer.println("}")
            }
            
            if (hasDot) {
                try {
                    val pngPath = outputFile.absolutePath.replace(".gv", ".png")
                    ProcessBuilder(dotBinary, "-Tpng", outputFile.absolutePath, "-o", pngPath).start().waitFor()
                } catch (_: Exception) { }
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
            val globalOutput = project.file("docs/images/graph/global_module_graph.gv")
            val allModules = project.rootProject.allprojects
            val globalEdges = mutableSetOf<Pair<String, String>>()
            
            allModules.forEach { p ->
                // Implicit
                /**
                 * 💡 NOTE: Synchronize this list with the one in 'generateGraphForProject'
                 * if 'AndroidFeatureConventionPlugin.kt' is updated.
                 */
                val buildFile = File(p.projectDir, "build.gradle.kts")
                if (buildFile.exists()) {
                    val content = buildFile.readText()
                    if (content.contains("estatia.android.feature")) {
                        listOf(":core:ui", ":core:common", ":core:domain", ":core:navigation", ":core:model", ":core:design-system", ":core:testing").forEach {
                            globalEdges.add(p.path to it)
                        }
                    }
                    if (content.contains("estatia.android.core") || content.contains("estatia.android.application")) {
                        listOf(":core:testing").forEach {
                            globalEdges.add(p.path to it)
                        }
                    }
                }
                // Explicit
                p.configurations.forEach { config ->
                    try {
                        config.dependencies.forEach { dep ->
                            if (dep is ProjectDependency) {
                                val depProj = dep::class.java.getMethod("getDependencyProject").invoke(dep) as Project
                                globalEdges.add(p.path to depProj.path)
                            }
                        }
                    } catch (_: Exception) { }
                }
            }

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
                        else -> "#BDB2FF"
                    }
                    writer.println("  \"${p.path}\" [fillcolor=\"$color\"];")
                }

                globalEdges.forEach { (from, to) ->
                    writer.println("  \"$from\" -> \"$to\"")
                }
                writer.println("}")
            }
            if (hasDot) {
                try {
                    val pngPath = globalOutput.absolutePath.replace(".gv", ".png")
                    ProcessBuilder(dotBinary, "-Tpng", globalOutput.absolutePath, "-o", pngPath).start().waitFor()
                } catch (_: Exception) { }
            }
            println("Global graph generated.")

            // 2. Generate Per-Module Graphs
            allModules.forEach { p ->
                if (p == project.rootProject) return@forEach
                val moduleOutput = File(p.projectDir, "module_graph.gv")
                generateGraphForProject(p, moduleOutput)
            }
            println("Per-module graphs generated in each module's root directory.")
        }
    }
}

