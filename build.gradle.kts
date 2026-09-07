import com.jraska.module.graph.assertion.GraphRulesExtension
import java.io.File

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

extensions.configure<GraphRulesExtension>("moduleGraphAssert") {
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
 * Custom task to generate Graphviz dot files for module dependencies.
 * Uses a manual project discovery logic that works even if subprojects aren't evaluated.
 */
tasks.register("generateModuleGraphs") {
    group = "reporting"
    description = "Generates Graphviz dot files for module dependency graphs."

    doLast {
        val rootDir = project.projectDir
        val dotBinary = "C:/Program Files/Graphviz/bin/dot.exe"
        val hasDot = File(dotBinary).exists()

        // 1. Discover all modules by reading settings.gradle.kts
        val settingsFile = File(rootDir, "settings.gradle.kts")
        val modules = mutableSetOf<String>()
        if (settingsFile.exists()) {
            val content = settingsFile.readText()
            Regex("""include\s*\(\s*["']([^"']+)["']\s*\)""").findAll(content).forEach { match ->
                modules.add(match.groupValues[1])
            }
        }

        // 2. Parse dependencies from build files
        val edges = mutableSetOf<Pair<String, String>>()
        
        modules.forEach { modulePath ->
            val relativePath = modulePath.removePrefix(":").replace(":", "/")
            val moduleDir = File(rootDir, relativePath)
            val buildFile = File(moduleDir, "build.gradle.kts")
            
            if (buildFile.exists()) {
                val content = buildFile.readText()
                
                // 2.1 Explicit dependencies: projects.core.common or project(":core:common")
                Regex("""project\s*\(\s*["']([^"']+)["']\s*\)""").findAll(content).forEach { match ->
                    edges.add(modulePath to match.groupValues[1])
                }
                
                Regex("""projects\.((?:[a-zA-Z0-9]+\.)*[a-zA-Z0-9]+)""").findAll(content).forEach { match ->
                    val dottedPath = match.groupValues[1]
                    val path = ":" + dottedPath.replace(".", ":")
                        .replace(Regex("([a-z])([A-Z])"), "$1-$2") // handle camelCase to kebab-case
                        .lowercase()
                    
                    // Match against discovered modules
                    val actualPath = modules.find { it.replace("-", "").replace(":", "") == path.replace("-", "").replace(":", "") }
                        ?: path
                    edges.add(modulePath to actualPath)
                }

                // 2.2 Implicit dependencies from convention plugins
                if (content.contains("libs.plugins.estatia.android.feature")) {
                    listOf(":core:ui", ":core:common", ":core:domain", ":core:navigation", ":core:model", ":core:design-system", ":core:testing").forEach {
                        edges.add(modulePath to it)
                    }
                }
                if (content.contains("libs.plugins.estatia.android.core") || content.contains("libs.plugins.estatia.android.application")) {
                    edges.add(modulePath to ":core:testing")
                }
            }
        }

        fun writeGraph(targetPath: String, outputFile: File, includeModules: Set<String>) {
            outputFile.parentFile.mkdirs()
            outputFile.printWriter().use { writer ->
                writer.println("digraph {")
                writer.println("  graph [label=\"$targetPath Dependencies\", labelloc=t, fontsize=24, ranksep=1.2];")
                writer.println("  node [style=filled, fillcolor=\"#bbdefb\", fontname=\"sans-serif\", shape=box, style=\"rounded,filled\"];")
                
                for (module in includeModules) {
                    val color = when {
                        module == ":app" -> "#CAFFBF"
                        module.startsWith(":feature") -> "#FFD6A5"
                        module.startsWith(":core") -> "#9BF6FF"
                        else -> "#BDB2FF"
                    }
                    writer.println("  \"$module\" [fillcolor=\"$color\"];")
                }

                for (edge in edges) {
                    if (includeModules.contains(edge.first) && modules.contains(edge.second)) {
                        writer.println("  \"${edge.first}\" -> \"${edge.second}\"")
                    }
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

        // 3. Generate Global Graph
        val globalOutput = project.file("docs/images/graph/global_module_graph.gv")
        writeGraph("Estatia Global", globalOutput, modules)
        println("Global graph generated at ${globalOutput.absolutePath}")

        // 4. Generate Per-Module Graphs
        modules.forEach { modulePath ->
            val relativePath = modulePath.removePrefix(":").replace(":", "/")
            val moduleDir = File(rootDir, relativePath)
            val outputFile = File(moduleDir, "module_graph.gv")
            
            val reachable = mutableSetOf<String>()
            fun collectReachable(current: String) {
                if (reachable.add(current)) {
                    edges.filter { it.first == current }.forEach { collectReachable(it.second) }
                }
            }
            collectReachable(modulePath)
            
            writeGraph(modulePath, outputFile, reachable)
        }
        println("Per-module graphs generated.")
    }
}
