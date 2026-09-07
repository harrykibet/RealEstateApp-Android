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

enum class DependencyType {
    API, IMPLEMENTATION, TEST, TEST_FIXTURES, KSP, LINT, IMPLICIT
}

data class ModuleEdge(val from: String, val to: String, val type: DependencyType)

/**
 * Custom task to generate high-fidelity Graphviz dot files for module dependencies.
 * Uses a hybrid approach: project discovery via settings and dependency extraction via build file analysis.
 */
tasks.register("generateModuleGraphs") {
    group = "reporting"
    description = "Generates Graphviz dot files with classified and implicit dependency visualization."

    doLast {
        val rootDir = project.projectDir
        val dotBinary = "C:/Program Files/Graphviz/bin/dot.exe"
        val hasDot = File(dotBinary).exists()

        val allEdges = mutableSetOf<ModuleEdge>()
        val modules = mutableSetOf<String>()

        // 1. Authoritative Module Discovery (from settings.gradle.kts)
        val settingsFile = File(rootDir, "settings.gradle.kts")
        if (settingsFile.exists()) {
            Regex("""include\s*\(\s*["']([^"']+)["']\s*\)""").findAll(settingsFile.readText()).forEach { match ->
                modules.add(match.groupValues[1])
            }
        }

        // 2. High-Fidelity Dependency Analysis
        modules.forEach { modulePath ->
            val relativePath = modulePath.removePrefix(":").replace(":", "/")
            val moduleDir = File(rootDir, relativePath)
            val buildFile = File(moduleDir, "build.gradle.kts")
            
            if (buildFile.exists()) {
                val content = buildFile.readText()
                
                fun extract(regex: Regex, type: DependencyType) {
                    regex.findAll(content).forEach { match ->
                        var dep = match.groupValues[1]
                        // Handle projects accessor: core.common -> :core:common
                        if (!dep.startsWith(":")) {
                            dep = ":" + dep.replace(".", ":")
                                .replace(Regex("([a-z])([A-Z])"), "$1-$2") // camelCase to kebab-case
                                .lowercase()
                        }
                        
                        // Refine match against discovered modules (e.g. handle testFixtures project(":core:testing"))
                        val actualDep = modules.find { 
                            it.replace("-", "").replace(":", "") == dep.replace("-", "").replace(":", "") 
                        } ?: dep
                        
                        if (modulePath != actualDep) {
                            allEdges.add(ModuleEdge(modulePath, actualDep, type))
                        }
                    }
                }

                // Explicit patterns classified by configuration
                extract(Regex("""(?:api|compile)\s*\(?\s*(?:projects\.|project\s*\(\s*["'])([^"'\)]+)"""), DependencyType.API)
                extract(Regex("""implementation\s*\(?\s*(?:projects\.|project\s*\(\s*["'])([^"'\)]+)"""), DependencyType.IMPLEMENTATION)
                extract(Regex("""testImplementation\s*\(?\s*(?:projects\.|project\s*\(\s*["'])([^"'\)]+)"""), DependencyType.TEST)
                extract(Regex("""androidTestImplementation\s*\(?\s*(?:projects\.|project\s*\(\s*["'])([^"'\)]+)"""), DependencyType.TEST)
                extract(Regex("""testFixturesApi\s*\(?\s*(?:projects\.|project\s*\(\s*["'])([^"'\)]+)"""), DependencyType.TEST_FIXTURES)
                extract(Regex("""ksp\s*\(?\s*(?:projects\.|project\s*\(\s*["'])([^"'\)]+)"""), DependencyType.KSP)
                extract(Regex("""lintChecks\s*\(?\s*(?:projects\.|project\s*\(\s*["'])([^"'\)]+)"""), DependencyType.LINT)

                // 2.2 Implicit Dependency Metadata (Convention Plugins)
                if (content.contains("estatia.android.feature")) {
                    listOf(":core:ui", ":core:common", ":core:domain", ":core:navigation", ":core:model", ":core:design-system").forEach {
                        if (modulePath != it) allEdges.add(ModuleEdge(modulePath, it, DependencyType.IMPLICIT))
                    }
                }
                if (content.contains("estatia.android.testing")) {
                    if (modulePath != ":core:testing") allEdges.add(ModuleEdge(modulePath, ":core:testing", DependencyType.TEST))
                    if (modulePath != ":core:testing-network") allEdges.add(ModuleEdge(modulePath, ":core:testing-network", DependencyType.TEST))
                }
                if (content.contains("estatia.android.core") || content.contains("estatia.android.application")) {
                    if (modulePath != ":core:testing") allEdges.add(ModuleEdge(modulePath, ":core:testing", DependencyType.IMPLICIT))
                }
            }
        }

        fun getEdgeStyle(type: DependencyType): String = when (type) {
            DependencyType.API -> "color=\"#1A237E\", penwidth=2.5, label=\"api\""
            DependencyType.IMPLEMENTATION -> "color=\"#1A237E\", style=solid"
            DependencyType.TEST -> "color=\"#757575\", style=dashed"
            DependencyType.TEST_FIXTURES -> "color=\"#FF9800\", style=dashed, label=\"fixtures\""
            DependencyType.IMPLICIT -> "color=\"#4CAF50\", style=dotted, label=\"implicit\""
            DependencyType.KSP -> "color=\"#9C27B0\", style=dotted, label=\"ksp\""
            DependencyType.LINT -> "color=\"#607D8B\", style=dotted, label=\"lint\""
        }

        fun writeGraph(targetPath: String, outputFile: File, includeModules: Set<String>) {
            outputFile.parentFile.mkdirs()
            outputFile.printWriter().use { writer ->
                writer.println("digraph {")
                writer.println("  graph [label=\"$targetPath Dependencies\", labelloc=t, fontsize=24, ranksep=1.4];")
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

                for (edge in allEdges) {
                    if (includeModules.contains(edge.from) && modules.contains(edge.to)) {
                        writer.println("  \"${edge.from}\" -> \"${edge.to}\" [${getEdgeStyle(edge.type)}]")
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
        val globalOutput = File(rootDir, "docs/images/graph/global_module_graph.gv")
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
                    allEdges.filter { it.from == current }.forEach { collectReachable(it.to) }
                }
            }
            collectReachable(modulePath)
            
            writeGraph(modulePath, outputFile, reachable)
        }
        println("Per-module graphs generated.")
    }
}
