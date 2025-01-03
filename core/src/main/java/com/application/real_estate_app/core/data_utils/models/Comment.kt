package com.application.real_estate_app.core.data_utils.models

import java.util.Date

data class Comment(
    val id: String?,
    val userId: String?,
    val commentText: String?,
    val timeStamp: Date?
)
