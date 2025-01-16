package com.application.real_estate_app.feature_comments.domain.interfaces

import android.net.ConnectivityManager
import com.application.real_estate_app.core.data_utils.data_models.Comment
import kotlinx.coroutines.flow.Flow

interface ICommentsApi {

    fun listenForComments(
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Flow<List<Comment?>>

    suspend fun submitComment(
        propertyId: String,
        comment: Comment,
        onFailure: (Exception) -> Unit
    ): Boolean?
}