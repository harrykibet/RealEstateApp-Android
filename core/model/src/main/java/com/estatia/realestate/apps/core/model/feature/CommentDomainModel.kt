package com.estatia.realestate.apps.core.model.feature

data class CommentDomainModel(
    val id: String?,
    val propertyId: String,
    val authorId: String,
    val authorName: String,
    val message: String,
    val timeStamp: Long
)
