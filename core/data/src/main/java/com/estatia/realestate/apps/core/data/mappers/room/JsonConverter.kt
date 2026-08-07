package com.estatia.realestate.apps.core.data.mappers.room

import com.google.gson.Gson

internal object JsonConverter {
    private val gson = Gson()

    // Convert a list of strings to JSON
    fun toJson(list: List<String>): String = gson.toJson(list)

    // Convert a JSON string to a list of strings
    fun fromJson(json: String): List<String> =
        try {
            gson.fromJson(json, Array<String>::class.java)?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
}
