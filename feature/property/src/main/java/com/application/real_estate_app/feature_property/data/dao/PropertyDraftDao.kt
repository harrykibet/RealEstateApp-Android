package com.application.real_estate_app.feature_property.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.application.real_estate_app.feature_property.data.entities.PropertyDraftEntity

@Dao
interface PropertyDraftDao {

    // Insert a new draft into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: PropertyDraftEntity): Long

    // Get all drafts from the database
    @Query("SELECT * FROM property_drafts")
    suspend fun getAllDrafts(): List<PropertyDraftEntity>

    // Get a specific draft by its ID
    @Query("SELECT * FROM property_drafts WHERE draft_id = :id")
    suspend fun getDraftById(id: Int): PropertyDraftEntity?

    // Update an existing draft
    @Update
    suspend fun updateDraft(draft: PropertyDraftEntity)

    // Delete a specific draft by its ID
    @Query("DELETE FROM property_drafts WHERE draft_id = :id")
    suspend fun deleteDraftById(id: Int)

    // Clear all drafts from the database
    @Query("DELETE FROM property_drafts")
    suspend fun clearAllDrafts()
}
