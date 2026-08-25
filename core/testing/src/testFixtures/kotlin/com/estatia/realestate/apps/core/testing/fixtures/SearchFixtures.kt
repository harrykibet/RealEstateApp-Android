package com.estatia.realestate.apps.core.testing.fixtures

import kotlin.random.Random

/**
 * Unified source of truth for search domain fixtures.
 */
object SearchFixtures {

    private val locations = listOf("Nairobi", "Mombasa", "Kisumu", "Westlands", "Kilimani")
    private val types = listOf("Apartment", "House", "Studio", "Office")

    /**
     * Returns a deterministic search query.
     */
    fun defaultQuery() = "Nairobi Apartment"

    /**
     * Factory method for building randomized search queries.
     */
    fun buildQuery(): String {
        val location = locations.random()
        val type = types.random()
        return if (Random.nextBoolean()) "$location $type" else location
    }
}
