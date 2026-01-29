package com.estatia.realestate.apps.core.model.feature

data class Comment(
    val id: String?,
    val propertyId: String,
    val authorId: String,
    val authorName: String,
    val message: String,
    val timestamp: Long
)
