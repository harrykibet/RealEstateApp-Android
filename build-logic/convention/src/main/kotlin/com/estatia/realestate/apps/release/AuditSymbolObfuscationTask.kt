package com.estatia.realestate.apps.release

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.*

/**
 * LAW-038: Release Symbol Obfuscation Integrity.
 * 
 * Scans the R8 mapping file for unobfuscated sensitive symbol names.
 */
abstract class AuditSymbolObfuscationTask : DefaultTask() {

    @get:InputFile
    abstract val mappingFile: RegularFileProperty

    @get:Input
    abstract val forbiddenPatterns: ListProperty<String>

    @get:Input
    abstract val allowlist: ListProperty<String>

    @get:OutputFile
    abstract val reportFile: RegularFileProperty

    @TaskAction
    fun audit() {
        val file = mappingFile.get().asFile
        val patterns = forbiddenPatterns.get().map { Regex(it) }
        val allowed = allowlist.get().toSet()

        val violations = file.readLines().withIndex().flatMap { (lineNo, line) ->
            // mapping.txt lines look like: "com.example.InternalImpl -> com.example.a:"
            val symbol = line.substringBefore(" ->").substringAfterLast(".")
            patterns.mapNotNull { pattern ->
                val match = pattern.find(symbol)
                if (match != null && symbol !in allowed) {
                    "L${lineNo + 1}: $symbol (matched ${pattern.pattern})"
                } else null
            }
        }

        reportFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText(violations.joinToString("\n"))
        }

        if (violations.isNotEmpty()) {
            throw GradleException(
                "LAW-038 Violation: Unobfuscated sensitive symbol(s) detected in release mapping:\n" +
                    violations.take(10).joinToString("\n") +
                    (if (violations.size > 10) "\n... and ${violations.size - 10} more." else "") +
                    "\n\nFull report: ${reportFile.get().asFile.absolutePath}" +
                    "\nRECOMMENDED: Update Proguard/R8 rules to ensure internal implementation details are obfuscated."
            )
        }
    }
}
