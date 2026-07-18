package com.estatia.realestate.apps.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.estatia.realestate.apps.core.database.entities.PropertyDraftEntity

@Dao
interface PropertyDraftDao {

    // Insert a new draft into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: PropertyDraftEntity): Long

    // Get all drafts from the database
    @Query("SELECT * FROM properties_drafts")
    suspend fun getAllDrafts(): List<PropertyDraftEntity>

    // Get a specific draft by its ID
    @Query("SELECT * FROM properties_drafts WHERE id = :id")
    suspend fun getDraftById(id: Long): PropertyDraftEntity?

    // Update an existing draft
    @Update
    suspend fun updateDraft(draft: PropertyDraftEntity)

    // Delete a specific draft by its ID
    @Query("DELETE FROM properties_drafts WHERE id = :id")
    suspend fun deleteDraftById(id: Long)

    // Clear all drafts from the database
    @Query("DELETE FROM properties_drafts")
    suspend fun clearAllDrafts()
}
