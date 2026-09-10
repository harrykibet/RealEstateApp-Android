import com.jraska.module.graph.assertion.GraphRulesExtension
import org.gradle.api.artifacts.ProjectDependency
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

    restricted = arrayOf(
        ":core:(?!canary-violations).* -X> :feature.*",
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
 * Authoritative Architectural Analysis Engine.
 * Uses Gradle's native Project and Configuration APIs instead of fragile Regex.
 */
object EstatiaArch {
    fun extractEdges(subprojects: Collection<Project>): Set<ModuleEdge> {
        val edges = mutableSetOf<ModuleEdge>()
        val implicitDeps = setOf(":core:ui", ":core:common", ":core:domain", ":core:navigation", ":core:model", ":core:design-system")
        val featurePluginId = "com.estatia.realestate.apps.android.feature"

        subprojects.forEach { p ->
            val hasFeaturePlugin = p.plugins.hasPlugin(featurePluginId)

            p.configurations.forEach { config ->
                val type = mapConfigurationToType(config.name) ?: return@forEach

                config.dependencies.withType(ProjectDependency::class.java).forEach { dep ->
                    val from = p.path
                    val to = dep.path
                    if (from == to) return@forEach

                    var actualType = type
                    if (hasFeaturePlugin && implicitDeps.contains(to) && type == DependencyType.IMPLEMENTATION) {
                        actualType = DependencyType.IMPLICIT
                    }

                    edges.add(ModuleEdge(from, to, actualType))
                }
            }
        }

        return edges.groupBy { it.from to it.to }.map { (_, list) ->
            list.maxByOrNull { getPriority(it.type) }!!
        }.toSet()
    }

    private fun getPriority(type: DependencyType): Int = when (type) {
        DependencyType.API -> 10
        DependencyType.IMPLEMENTATION -> 9
        DependencyType.TEST_FIXTURES -> 8
        DependencyType.IMPLICIT -> 7
        DependencyType.KSP -> 6
        DependencyType.LINT -> 5
        DependencyType.TEST -> 4
    }

    private fun mapConfigurationToType(name: String): DependencyType? {
        val lower = name.lowercase()
        if (lower == "testfixturesapi") return DependencyType.TEST_FIXTURES
        if (lower.contains("ksp")) return DependencyType.KSP
        if (lower.contains("lint")) return DependencyType.LINT

        if (lower.endsWith("implementation") || lower == "implementation") {
            return if (lower.contains("test")) DependencyType.TEST else DependencyType.IMPLEMENTATION
        }
        if (lower.endsWith("api") || lower == "api") {
            return if (lower.contains("test")) DependencyType.TEST else DependencyType.API
        }

        return null
    }

    fun getAllProjects(root: Project): Set<Project> {
        val result = mutableSetOf<Project>()
        fun traverse(p: Project) {
            result.add(p)
            p.childProjects.values.forEach { traverse(it) }
        }
        root.childProjects.values.forEach { traverse(it) }
        return result
    }

    fun findImpactedModules(rootDir: File, changedFiles: List<String>, modulePaths: Set<String>, edges: Set<ModuleEdge>): Set<String> {
        val directlyAffected = mutableSetOf<String>()
        changedFiles.forEach { path ->
            val file = File(rootDir, path)
            if (!file.exists()) return@forEach

            var dir = file.parentFile
            while (dir != null && dir != rootDir) {
                val buildFile = File(dir, "build.gradle.kts")
                if (buildFile.exists()) {
                    val modulePath = ":" + dir.relativeTo(rootDir).path.replace(File.separator, ":")
                    if (modulePaths.contains(modulePath)) {
                        directlyAffected.add(modulePath)
                        break
                    }
                }
                dir = dir.parentFile
            }
        }

        val fullImpact = mutableSetOf<String>()
        fun addWithConsumers(module: String) {
            if (fullImpact.add(module)) {
                edges.filter { it.to == module }.forEach { addWithConsumers(it.from) }
            }
        }
        directlyAffected.forEach { addWithConsumers(it) }
        return fullImpact
    }
}

tasks.register("generateModuleGraphs") {
    description = "Generates module dependency graphs."
    group = "reporting"
    doLast {
        val rootDir = project.projectDir

        val dotBinary = project.findProperty("estatia.graphviz.dot")?.toString()
            ?: System.getenv("GRAPHVIZ_DOT")
            ?: listOf(
                "C:/Program Files/Graphviz/bin/dot.exe",
                "C:/Program Files (x86)/Graphviz/bin/dot.exe",
                "/opt/homebrew/bin/dot",
                "/usr/local/bin/dot",
                "/usr/bin/dot"
            ).find { File(it).exists() }
            ?: "dot"

        val hasDot = try {
            ProcessBuilder(dotBinary, "-V").start().waitFor() == 0
        } catch (_: Exception) {
            false
        }

        if (!hasDot) {
            logger.warn(
                """
                [GRAPHVIZ_NOT_FOUND] The 'dot' binary was not found. PNG graph generation will be skipped.
                To enable PNG generation:
                  1. Install Graphviz (https://graphviz.org/download/)
                  2. Ensure 'dot' is in your system PATH, OR
                  3. Set 'estatia.graphviz.dot' property in gradle.properties, OR
                  4. Set 'GRAPHVIZ_DOT' environment variable.
                """.trimIndent()
            )
        }

        val allSubprojects = project.rootProject.allprojects.filter { it != project.rootProject }
        val modules = allSubprojects.map { it.path }.toSet()
        val allEdges = EstatiaArch.extractEdges(allSubprojects)

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

        writeGraph("Estatia Global", File(rootDir, "docs/images/graph/global_module_graph.gv"), modules)
        modules.forEach { modulePath ->
            val relativePath = modulePath.removePrefix(":").replace(":", "/")
            val outputFile = File(File(rootDir, relativePath), "module_graph.gv")

            val reachable = mutableSetOf<String>()
            fun collectReachable(current: String) {
                if (reachable.add(current)) {
                    allEdges.filter { it.from == current }.forEach { collectReachable(it.to) }
                }
            }
            collectReachable(modulePath)
            writeGraph(modulePath, outputFile, reachable)
        }
    }
}

tasks.register("checkDependencyDrift") {
    description = "Checks for hardcoded dependencies."
    group = "verification"
    doLast {
        val rootDir = project.projectDir
        val magicStringRegex = Regex("""(implementation|api|testImplementation|debugImplementation|androidTestImplementation)\s*\(?\s*["']([^:"]+:[^:"]+:[^:"]+)["']""")
        val violations = mutableListOf<String>()
        rootDir.walkTopDown().forEach { file ->
            if (file.name == "build.gradle.kts" && !file.path.contains(".gradle") && !file.path.contains("build-logic")) {
                val content = file.readText()
                magicStringRegex.findAll(content).forEach { violations.add("${file.relativeTo(rootDir).path}: ${it.value}") }
            }
        }
        if (violations.isNotEmpty()) {
            throw GradleException("LAW-037: Dependency Drift detected. Hardcoded dependencies found:\n" + violations.joinToString("\n"))
        }
    }
}

tasks.register("calculateImpact") {
    description = "Calculates the impact of changes on the codebase."
    group = "verification"
    doLast {
        val rootDir = project.projectDir
        val allSubprojects = project.rootProject.allprojects.filter { it != project.rootProject }
        val modules = allSubprojects.map { it.path }.toSet()
        val allEdges = EstatiaArch.extractEdges(allSubprojects)

        val diffOutput = try {
            ProcessBuilder("git", "diff", "--name-only", "origin/main").start().inputStream.bufferedReader().readText().lines().filter { it.isNotBlank() }
        } catch (_: Exception) { emptyList<String>() }

        if (diffOutput.isEmpty()) {
            println("IMPACT_TASKS=test lint") // Fallback to all
            return@doLast
        }

        val fullImpact = EstatiaArch.findImpactedModules(rootDir, diffOutput, modules, allEdges)
        val tasks = fullImpact.flatMap { listOf("$it:lintDemoDebug", "$it:testDemoDebugUnitTest") }
        println("IMPACT_TASKS=" + tasks.joinToString(" "))
    }
}

tasks.register("auditBinaryPurity") {
    description = "Audits the binary for sensitive keywords."
    group = "verification"
    doLast {
        val mappingFile = File(project.rootDir, "app/build/outputs/mapping/prodRelease/mapping.txt")
        if (!mappingFile.exists()) {
            println("Skip: R8 mapping not found at ${mappingFile.absolutePath}")
            return@doLast
        }
        val forbidden = listOf("Secret", "ApiKey", "InternalImpl")
        val content = mappingFile.readText()
        val violations = forbidden.filter { content.contains(it) }
        if (violations.isNotEmpty()) throw GradleException("LAW-038: Binary Purity Violation. Sensitive keywords found in R8 mapping: $violations")
    }
}

tasks.register("verifyArchitecture") {
    group = "verification"
    description = "Executes all architectural and policy enforcement gates."

    // 1. Standard Implementation Guard (Lint)
    dependsOn(":lint:assemble") // Ensure custom lint rules are built
    dependsOn(":lint:test")     // Run detector unit tests and DocParityTest

    // 2. Global Structural Enforcement (Konsist & Regression Tests)
    dependsOn(":core:testing-architecture:test")
    dependsOn(":core:canary-violations:lintDemoDebug")
    dependsOn("assertModuleGraph")
    dependsOn("generateModuleGraphs")
    dependsOn(":core:data:compileDemoDebugKotlin")
    dependsOn(":core:network:compileDemoDebugKotlin")
    dependsOn("checkDependencyDrift")
}
