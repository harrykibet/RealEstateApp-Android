package com.estatia.realestate.apps.graph

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*
import java.io.File

enum class DependencyType { API, IMPLEMENTATION, TEST, ANDROID_TEST, TEST_FIXTURES, KSP, LINT, IMPLICIT, OTHER }

data class ModuleEdge(val from: String, val to: String, val type: DependencyType)

object ModuleGraphExtractor {

    /**
     * Files that, if changed, invalidate all optimizations and require a full test run.
     */
    private val GLOBAL_IMPACT_PATTERNS = listOf(
        "gradle/libs.versions.toml",
        "build-logic/",
        "settings.gradle.kts",
        "gradle.properties",
        "build.gradle.kts", // Root
        "core/architecture/", // The SSoT for all enforcement
        "lint/" // Changes to the enforcement engine itself
    )

    fun discoverModules(root: Project): Set<String> =
        root.subprojects.map { it.path }.toSet()

    fun extractEdges(subprojects: Collection<Project>): Set<ModuleEdge> {
        val edges = mutableSetOf<ModuleEdge>()
        val implicitDeps = setOf(":core:ui", ":core:common", ":core:domain", ":core:navigation", ":core:model", ":core:design-system")
        val featurePluginId = "com.estatia.realestate.apps.android.feature"

        subprojects.forEach { p ->
            val hasFeaturePlugin = p.plugins.hasPlugin(featurePluginId)

            p.configurations.forEach { config ->
                val type = classify(config.name) ?: return@forEach

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
        DependencyType.ANDROID_TEST -> 4
        DependencyType.TEST -> 3
        DependencyType.OTHER -> 0
    }

    private fun classify(configName: String): DependencyType? {
        val lower = configName.lowercase()
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

    /**
     * Pessimistic Impact Calculation.
     * Returns the set of all modules that must be tested based on the diff.
     */
    fun findImpactedModules(diffFiles: List<String>, allModules: Set<String>, edges: Set<ModuleEdge>, rootDir: File): Set<String> {
        // 1. Check for Tier 0: Global Atomic Triggers
        val hasGlobalChange = diffFiles.any { path -> 
            GLOBAL_IMPACT_PATTERNS.any { pattern -> path.startsWith(pattern) } 
        }

        if (hasGlobalChange) {
            println("INFO: Global trigger detected. Optimization bypassed. Running full test suite.")
            return allModules
        }

        // 2. Identify directly affected modules
        val directlyAffected = mutableSetOf<String>()
        diffFiles.forEach { path ->
            val file = File(rootDir, path)
            if (!file.exists()) return@forEach

            var dir = file.parentFile
            while (dir != null && dir != rootDir) {
                val buildFile = File(dir, "build.gradle.kts")
                if (buildFile.exists()) {
                    val relativePath = dir.relativeTo(rootDir).path.replace(File.separator, ":")
                    val modulePath = if (relativePath.isEmpty()) ":" else ":$relativePath"
                    if (allModules.contains(modulePath)) {
                        directlyAffected.add(modulePath)
                        break
                    }
                }
                dir = dir.parentFile
            }
        }

        // 3. Propagate impact to all downstream consumers (Transitive Closure)
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

abstract class GenerateModuleGraphsTask : DefaultTask() {

    @get:Input
    abstract val modules: SetProperty<String>

    @get:Input
    abstract val edges: ListProperty<String> // "from|to|type"

    @get:Input
    @get:Optional
    abstract val dotBinary: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val parsedEdges = edges.get().map {
            val parts = it.split("|")
            ModuleEdge(parts[0], parts[1], DependencyType.valueOf(parts[2]))
        }.toSet()

        val bin = dotBinary.orNull
        val hasDot = bin?.let { b ->
            runCatching { ProcessBuilder(b, "-V").start().waitFor() == 0 }.getOrDefault(false)
        } ?: false

        if (bin != null && !hasDot) {
            logger.warn("[GRAPHVIZ_NOT_FOUND] '$bin' not usable — PNG generation skipped.")
        }

        fun writeGraph(label: String, target: File, include: Set<String>) {
            target.parentFile.mkdirs()
            target.printWriter().use { w ->
                w.println("digraph {")
                w.println("""  graph [label="$label Dependencies", labelloc=t, fontsize=24, ranksep=1.4];""")
                w.println("""  node [style=filled, fillcolor="#bbdefb", fontname="sans-serif", shape=box, style="rounded,filled"];""")
                include.sorted().forEach { module ->
                    val color = when {
                        module == ":app" -> "#CAFFBF"
                        module.startsWith(":feature") -> "#FFD6A5"
                        module.startsWith(":core") -> "#9BF6FF"
                        else -> "#BDB2FF"
                    }
                    w.println("""  "$module" [fillcolor="$color"];""")
                }
                parsedEdges
                    .filter { it.from in include && it.to in modules.get() }
                    .sortedWith(compareBy({ it.from }, { it.to }, { it.type }))
                    .forEach { edge ->
                        w.println("""  "${edge.from}" -> "${edge.to}" [${getEdgeStyle(edge.type)}]""")
                    }
                w.println("}")
            }
            if (hasDot && bin != null) {
                val png = File(target.absolutePath.removeSuffix(".gv") + ".png")
                val proc = ProcessBuilder(bin, "-Tpng", target.absolutePath, "-o", png.absolutePath)
                    .redirectErrorStream(true).start()
                val exit = proc.waitFor()
                if (exit != 0) {
                    logger.warn("dot failed (exit $exit) for $target:\n${proc.inputStream.bufferedReader().readText()}")
                }
            }
        }

        val allModules = modules.get()
        writeGraph("Estatia Global", outputDir.file("global_module_graph.gv").get().asFile, allModules)

        allModules.forEach { modulePath ->
            val reachable = mutableSetOf<String>()
            fun visit(m: String) {
                if (reachable.add(m)) parsedEdges.filter { it.from == m }.forEach { visit(it.to) }
            }
            visit(modulePath)
            val relPath = modulePath.removePrefix(":").replace(":", "/")
            writeGraph(modulePath, File(project.rootDir, "$relPath/module_graph.gv"), reachable)
        }
    }

    private fun getEdgeStyle(type: DependencyType): String = when (type) {
        DependencyType.API -> "color=\"#1A237E\", penwidth=2.5, label=\"api\""
        DependencyType.IMPLEMENTATION -> "color=\"#1A237E\", style=solid"
        DependencyType.TEST, DependencyType.ANDROID_TEST -> "color=\"#757575\", style=dashed"
        DependencyType.TEST_FIXTURES -> "color=\"#FF9800\", style=dashed, label=\"fixtures\""
        DependencyType.KSP -> "color=\"#9C27B0\", style=dotted, label=\"ksp\""
        DependencyType.LINT -> "color=\"#607D8B\", style=dotted, label=\"lint\""
        DependencyType.IMPLICIT -> "color=\"#4CAF50\", style=dotted, label=\"implicit\""
        DependencyType.OTHER -> "color=\"#000000\", style=dotted"
    }
}
