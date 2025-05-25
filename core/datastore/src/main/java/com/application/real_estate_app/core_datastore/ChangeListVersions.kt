package com.application.real_estate_app.core_datastore

/**
 * Class summarizing the local version of each model for sync.
 * Helps determine what data needs to be refreshed from backend.
 */
data class ChangeListVersions(
    val propertyVersion: Int = -1,
    val userVersion: Int = -1,
)
