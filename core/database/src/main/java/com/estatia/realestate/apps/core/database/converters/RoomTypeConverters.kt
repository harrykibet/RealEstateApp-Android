package com.estatia.realestate.apps.core.database.converters

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RoomTypeConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        if (value == null) return null
        return try {
            json.decodeFromString<List<String>>(value)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        if (list == null) return null
        return json.encodeToString(list)
    }

    @TypeConverter
    fun toList(jsonStr: String): List<String> {
        return try {
            json.decodeFromString<List<String>>(jsonStr)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

