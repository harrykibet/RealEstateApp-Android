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

enum class DependencyType { API, IMPLEMENTATION, TEST, ANDROID_TEST, TEST_FIXTURES, KSP, LINT, OTHER }

data class ModuleEdge(val from: String, val to: String, val type: DependencyType)

object ModuleGraphExtractor {

    /**
     * Reads the *resolved* declared dependencies straight from each subproject's
     * configurations.
     */
    fun extractEdges(subprojects: Collection<Project>): Set<ModuleEdge> {
        val edges = mutableSetOf<ModuleEdge>()
        subprojects.forEach { sub ->
            sub.configurations.forEach { config ->
                val type = classify(config.name) ?: return@forEach
                config.dependencies
                    .filterIsInstance<ProjectDependency>()
                    .forEach { dep ->
                        val targetPath = dep.path
                        if (targetPath != sub.path) {
                            edges.add(ModuleEdge(sub.path, targetPath, type))
                        }
                    }
            }
        }
        return edges
    }

    private fun classify(configName: String): DependencyType? = when {
        configName.contains("TestFixtures", ignoreCase = true) -> DependencyType.TEST_FIXTURES
        configName.contains("AndroidTest") -> DependencyType.ANDROID_TEST
        configName.contains("Test") -> DependencyType.TEST
        configName.contains("ksp", ignoreCase = true) -> DependencyType.KSP
        configName.contains("lint", ignoreCase = true) -> DependencyType.LINT
        configName.equals("api", true) || configName.endsWith("Api") -> DependencyType.API
        configName.equals("implementation", true) || configName.endsWith("Implementation") -> DependencyType.IMPLEMENTATION
        else -> null
    }

    fun findImpactedModules(diffFiles: List<String>, modules: Set<String>, edges: Set<ModuleEdge>, rootDir: File): Set<String> {
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
                    if (modules.contains(modulePath)) {
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
                w.println("""  node [style="rounded,filled", fontname="sans-serif", shape=box];""")
                include.sorted().forEach { m ->
                    val color = when {
                        m == ":app" -> "#CAFFBF"
                        m.startsWith(":feature") -> "#FFD6A5"
                        m.startsWith(":core") -> "#9BF6FF"
                        else -> "#BDB2FF"
                    }
                    w.println("""  "$m" [fillcolor="$color"];""")
                }
                parsedEdges
                    .filter { it.from in include && it.to in modules.get() }
                    .sortedWith(compareBy({ it.from }, { it.to }, { it.type }))
                    .forEach { w.println("""  "${it.from}" -> "${it.to}" [${style(it.type)}]""") }
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

    private fun style(type: DependencyType) = when (type) {
        DependencyType.API -> """color="#1A237E", penwidth=2.5, label="api""""
        DependencyType.IMPLEMENTATION -> """color="#1A237E", style=solid"""
        DependencyType.TEST, DependencyType.ANDROID_TEST -> """color="#757575", style=dashed"""
        DependencyType.TEST_FIXTURES -> """color="#FF9800", style=dashed, label="fixtures""""
        DependencyType.KSP -> """color="#9C27B0", style=dotted, label="ksp""""
        DependencyType.LINT -> """color="#607D8B", style=dotted, label="lint""""
        DependencyType.OTHER -> """color="#607D8B", style=dotted"""
    }
}
