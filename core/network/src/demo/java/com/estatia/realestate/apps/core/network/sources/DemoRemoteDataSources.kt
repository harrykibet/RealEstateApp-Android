package com.estatia.realestate.apps.core.network.sources

import android.app.Activity
import android.net.Uri
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.interfaces.PhoneVerificationState
import com.estatia.realestate.apps.core.network.db_entities.*
import com.estatia.realestate.apps.core.network.interfaces.*
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.model.security.SecretId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoAuthRemoteDataSource @Inject constructor() : IAuthRemoteDataSource {
    override fun isUserAuthenticated(): Flow<Boolean> = flowOf(true)
    override fun getCurrentUserId(): String? = DemoData.demoUser.userId
    override fun getCurrentUserEmail(): String? = DemoData.demoUser.email
    override fun getCurrentUser(): NetworkUserEntity? = NetworkUserEntity(
        DemoData.demoUser.userId!!,
        DemoData.demoUser.name,
        DemoData.demoUser.email,
        null,
        null,
        true
    )

    override suspend fun signUpWithEmail(email: String, password: String): AppResult<NetworkUserEntity> = 
        AppResult.Success(getCurrentUser()!!)

    override suspend fun signInWithEmail(email: String, password: String): AppResult<NetworkUserEntity> = 
        AppResult.Success(getCurrentUser()!!)

    override suspend fun signInInteractive(activity: Activity): AppResult<NetworkUserEntity> = 
        AppResult.Success(getCurrentUser()!!)

    override suspend fun signInWithGoogle(idToken: String): AppResult<NetworkUserEntity> = 
        AppResult.Success(getCurrentUser()!!)

    override suspend fun createOrUpdateUserProfile(userId: String, user: UserEntityModel): AppResult<Unit> = AppResult.Success(Unit)

    override fun startPhoneNumberVerification(phoneNumber: String, activity: Activity): Flow<PhoneVerificationState> = 
        flowOf(PhoneVerificationState.Verified)

    override suspend fun verifyPhoneCode(verificationId: String, code: String): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun resendVerificationCode(phoneNumber: String, activity: Activity): AppResult<String> = AppResult.Success("new_id")
    override suspend fun sendEmailVerification(): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun isEmailVerified(): AppResult<Boolean> = AppResult.Success(true)
    override suspend fun sendPasswordResetEmail(email: String): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun signOut(): AppResult<Unit> = AppResult.Success(Unit)
}

@Singleton
class DemoPropertyRemoteDataSource @Inject constructor() : IPropertyRemoteDatasource {
    override suspend fun uploadProperty(property: PropertyEntityModel, imageUris: List<Uri>, videoUris: List<Uri>): AppResult<String> = AppResult.Success("prop_1")
    override suspend fun updateProperty(propertyId: String, updates: Map<String, Any>): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun deleteProperty(propertyId: String): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun getPropertyById(propertyId: String): AppResult<PropertyEntityModel> = 
        AppResult.Success(DemoData.sampleProperties.find { it.id == propertyId } ?: DemoData.sampleProperties.first())
    override suspend fun likeProperty(userId: String, propertyId: String): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun unlikeProperty(userId: String, propertyId: String): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun recordView(propertyId: String): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun recordShare(propertyId: String): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun fetchLikedProperties(userId: String): AppResult<List<PropertyEntityModel>> = AppResult.Success(emptyList())
    override suspend fun fetchPropertiesPaginated(cursor: PropertyCursor?, pageSize: Int): AppResult<PropertyRemotePage> {
        val allProperties = DemoData.sampleProperties
        val startIndex = (cursor?.documentId?.toIntOrNull() ?: 0)
        val endIndex = (startIndex + pageSize).coerceAtMost(allProperties.size)
        
        val properties = if (startIndex < allProperties.size) {
            allProperties.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        
        val nextCursor = if (endIndex < allProperties.size) {
            PropertyCursor(createdAt = System.currentTimeMillis(), documentId = endIndex.toString())
        } else {
            null
        }
        
        return AppResult.Success(PropertyRemotePage(properties, nextCursor))
    }
}

@Singleton
class DemoSearchRemoteDataSource @Inject constructor() : ISearchRemoteDataSource {
    override suspend fun searchProperties(query: String, limit: Int): AppResult<List<PropertyEntityModel>> = 
        AppResult.Success(DemoData.sampleProperties.filter { it.title.contains(query, ignoreCase = true) })
    
    override suspend fun getNearbyProperties(latitude: Double, longitude: Double, radiusKm: Double): AppResult<List<PropertyEntityModel>> = AppResult.Success(emptyList())
}

@Singleton
class DemoCommentsRemoteDataSource @Inject constructor() : ICommentsRemoteDataSource {
    override fun observeComments(propertyId: String): Flow<AppResult<List<CommentEntityModel>>> = flowOf(AppResult.Success(emptyList()))
    override suspend fun submitComment(comment: CommentEntityModel): AppResult<Unit> = AppResult.Success(Unit)
}

@Singleton
class DemoAnalyticsRemoteDataSource @Inject constructor() : IAnalyticsRemoteDataSource {
    override suspend fun logEvent(event: AnalyticsEvent): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun getEventsForUser(userId: String): AppResult<List<AnalyticsEvent>> = AppResult.Success(emptyList())
    override suspend fun getEventById(eventId: String): AppResult<AnalyticsEvent?> = AppResult.Success(null)
    override suspend fun logEvent(message: String, eventType: String, customMetadata: Map<String, String>?): AppResult<Unit> = AppResult.Success(Unit)
    override fun generateEventId(): String = UUID.randomUUID().toString()
}

@Singleton
class DemoUserRemoteDataSource @Inject constructor() : IUserRemoteDataSource {
    override suspend fun getUserById(userId: String): AppResult<UserEntityModel> = AppResult.Success(DemoData.demoUser)
}

@Singleton
class DemoConfigRemoteDataSource @Inject constructor() : IConfigRemoteDataSource {
    override suspend fun fetchRemoteConfig(): AppResult<String?> = AppResult.Success("demo_config")
}

@Singleton
class DemoSecretRemoteDataSource @Inject constructor() : ISecretRemoteDataSource {
    override suspend fun fetchSecret(secretId: SecretId): AppResult<String> = AppResult.Success("demo_secret")
}
