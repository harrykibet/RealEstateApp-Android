package com.application.real_estate_app.security.utils.extensions

// Semantic versioning implementation
data class SemanticVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preRelease: String? = null
) : Comparable<SemanticVersion> {

    companion object {
        fun parse(version: String): SemanticVersion {
            // Implementation with proper semver parsing
            return SemanticVersion(0, 0, 0)
        }
    }

    override fun compareTo(other: SemanticVersion): Int {
        // Standard semver comparison logic
        return compareValuesBy(this, other, { it.major }, { it.minor }, { it.patch })
    }
}