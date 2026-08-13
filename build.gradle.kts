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
tasks.register("generateNiaModuleGraph") {
    group = "reporting"
    description = "Generates a Graphviz dot file for the module dependency graph with NIA-style colors."

    doLast {
        val ofModule = project.findProperty("modules.graph.of.module") as? String
        val outputFile = project.findProperty("modules.graph.output.gv") as? String
            ?: "${project.layout.buildDirectory.get()}/reports/module_graph.gv"

        val dotFile = project.file(outputFile)
        dotFile.parentFile.mkdirs()

        // Use allprojects to ensure we see everything
        val allProjects = project.allprojects
        val modulesToInclude = mutableSetOf<Project>()

        if (ofModule != null) {
            val rootProj = allProjects.find { it.path == ofModule } ?: throw IllegalArgumentException("Module $ofModule not found")
            fun collectModules(p: Project) {
                if (modulesToInclude.add(p)) {
                    p.configurations.forEach { config ->
                        try {
                            config.dependencies.forEach { dep ->
                                if (dep is ProjectDependency) {
                                    val target = dep::class.java.getMethod("getDependencyProject").invoke(dep) as Project
                                    collectModules(target)
                                }
                            }
                        } catch (_: Exception) { }
                    }
                }
            }
            collectModules(rootProj)
        } else {
            modulesToInclude.addAll(allProjects)
        }

        dotFile.printWriter().use { writer ->
            writer.println("digraph {")
            writer.println("  graph [label=\"Estatia Module Graph\", labelloc=t, fontsize=30, ranksep=1.4];")
            writer.println("  node [style=filled, fillcolor=\"#bbdefb\", fontname=\"sans-serif\", shape=box, style=\"rounded,filled\"];")

            // NIA-inspired color palette
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

            // Generate edges
            modulesToInclude.forEach { p ->
                p.configurations.forEach { config ->
                    try {
                        config.dependencies.forEach { dep ->
                            if (dep is ProjectDependency) {
                                val target = dep::class.java.getMethod("getDependencyProject").invoke(dep) as Project
                                if (modulesToInclude.contains(target)) {
                                    writer.println("  \"${p.path}\" -> \"${target.path}\"")
                                }
                            }
                        }
                    } catch (_: Exception) { }
                }
            }
            writer.println("}")
        }
        println("NIA-style Graphviz generated at: ${dotFile.absolutePath}")
    }
}

