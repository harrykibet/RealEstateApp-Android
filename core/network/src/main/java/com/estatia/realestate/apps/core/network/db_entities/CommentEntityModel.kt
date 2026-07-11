package com.estatia.realestate.apps.core.network.db_entities

import com.google.firebase.firestore.ServerTimestamp

data class CommentEntityModel(
    val id: String? = null,
    val authorId: String,
    val message: String,
    val propertyId: String,
    val authorName: String,
    @ServerTimestamp val timeStamp: Long
)
