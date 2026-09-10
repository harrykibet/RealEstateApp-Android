package com.estatia.realestate.apps.core.datastore
import com.estatia.realestate.apps.core.architecture.annotations.Helper

/**
 * Class summarizing the local version of each model for sync.
 * Helps determine what data needs to be refreshed from backend.
 */
@Helper
data class ChangeListVersions(
    val propertyVersion: Int = -1,
    val userVersion: Int = -1,
)
