package com.estatia.realestate.apps.core.model.feature

import java.util.Date

data class Comment(
    val id: String?,
    val userId: String?,
    val commentText: String?,
    val timeStamp: Date?
)
