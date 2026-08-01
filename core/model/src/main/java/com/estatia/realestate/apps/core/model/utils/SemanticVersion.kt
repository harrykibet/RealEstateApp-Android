package com.estatia.realestate.apps.core.model.utils

import java.lang.IllegalArgumentException

/**
 * Represents a semantic version following the SemVer 2.0.0 specification
 * @param major Major version (increment for incompatible API changes)
 * @param minor Minor version (increment for additive functionality)
 * @param patch Patch version (increment for bug fixes)
 * @param preRelease Optional pre-release version (hyphen-separated identifiers)
 */
data class SemanticVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preRelease: String? = null
) : Comparable<SemanticVersion> {

    init {
        validateVersionComponents(major, minor, patch)
        preRelease?.let { validatePreRelease(it) }
    }

    companion object {
        private val SEMVER_REGEX = Regex(
            """^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?${'$'}"""
        )

        fun parse(version: String): SemanticVersion {
            val match = SEMVER_REGEX.matchEntire(version)
                ?: throw IllegalArgumentException("Invalid semantic version format: $version")

            val (majorStr, minorStr, patchStr) = match.destructured
            val preRelease = match.groups[4]?.value

            return SemanticVersion(
                major = majorStr.toInt(),
                minor = minorStr.toInt(),
                patch = patchStr.toInt(),
                preRelease = preRelease
            )
        }

        private fun validateVersionComponents(major: Int, minor: Int, patch: Int) {
            if (major < 0 || minor < 0 || patch < 0) {
                throw IllegalArgumentException("Version components cannot be negative")
            }
        }

        private fun validatePreRelease(preRelease: String) {
            val identifiers = preRelease.split('.')
            if (identifiers.isEmpty()) {
                throw IllegalArgumentException("Pre-release cannot be empty")
            }

            identifiers.forEach { identifier ->
                if (identifier.isEmpty()) {
                    throw IllegalArgumentException("Empty pre-release identifier")
                }

                if (identifier.matches(Regex("^0\\d+"))) {
                    throw IllegalArgumentException("Numeric pre-release identifier cannot have leading zero: $identifier")
                }

                if (!identifier.matches(Regex("^[0-9A-Za-z-]+"))) {
                    throw IllegalArgumentException("Invalid pre-release identifier: $identifier")
                }
            }
        }
    }

    override fun compareTo(other: SemanticVersion): Int {
        // Compare core version components
        val coreComparison = compareValuesBy(this, other,
            { it.major }, { it.minor }, { it.patch }
        )
        if (coreComparison != 0) return coreComparison

        // Handle pre-release precedence
        return when {
            preRelease == null && other.preRelease == null -> 0
            preRelease == null -> 1  // Release version has higher precedence
            other.preRelease == null -> -1
            else -> comparePreRelease(other.preRelease)
        }
    }

    private fun comparePreRelease(otherPreRelease: String): Int {
        val thisIdentifiers = preRelease!!.split('.')
        val otherIdentifiers = otherPreRelease.split('.')

        for (i in 0 until maxOf(thisIdentifiers.size, otherIdentifiers.size)) {
            val thisId = thisIdentifiers.getOrNull(i)
            val otherId = otherIdentifiers.getOrNull(i)

            when {
                thisId == null -> return -1
                otherId == null -> return 1
                else -> {
                    val comparison = compareIdentifiers(thisId, otherId)
                    if (comparison != 0) return comparison
                }
            }
        }
        return 0
    }

    private fun compareIdentifiers(a: String, b: String): Int {
        val aNumeric = a.matches(Regex("\\d+"))
        val bNumeric = b.matches(Regex("\\d+"))

        return when {
            aNumeric && bNumeric -> a.toInt().compareTo(b.toInt())
            aNumeric -> -1  // Numeric identifiers have lower precedence
            bNumeric -> 1
            else -> a.compareTo(b)
        }
    }

    override fun toString(): String {
        return buildString {
            append("$major.$minor.$patch")
            preRelease?.let { append("-$it") }
        }
    }
}
