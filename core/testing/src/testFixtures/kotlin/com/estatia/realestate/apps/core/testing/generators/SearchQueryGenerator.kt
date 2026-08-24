package com.estatia.realestate.apps.core.testing.generators

import kotlin.random.Random

/**
 * Generator for realistic search queries to support property-based testing of the search engine.
 */
object SearchQueryGenerator {
    private val locations = listOf("Nairobi", "Mombasa", "Kisumu", "Westlands", "Kilimani")
    private val types = listOf("Apartment", "House", "Studio", "Office")

    fun generate(): String {
        val location = locations.random()
        val type = types.random()
        return if (Random.nextBoolean()) "$location $type" else location
    }
}
