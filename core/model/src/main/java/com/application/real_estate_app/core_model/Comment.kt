package com.application.real_estate_app.core_model

import java.util.Date

data class Comment(
    val id: String?,
    val userId: String?,
    val commentText: String?,
    val timeStamp: Date?
)
