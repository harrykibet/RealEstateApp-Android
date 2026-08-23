package com.estatia.realestate.apps.core.data.repositories

import androidx.core.net.toUri
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.common.exceptions.getOrNull
import com.estatia.realestate.apps.core.common.exceptions.map
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.model.engagement.EngagementAction
import com.estatia.realestate.apps.core.data.mappers.remote.RemotePropertyMapper
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import com.estatia.realestate.apps.core.model.property.PropertyDraftDomainModel
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.common.IExceptionTranslator
import com.estatia.realestate.apps.core.domain.common.IContentSafetyService
import com.estatia.realestate.apps.core.model.engagement.SafetyResult
import com.estatia.realestate.apps.core.common.exceptions.PropertyException
import com.estatia.realestate.apps.core.data.util.translatePropertyFailures
import com.estatia.realestate.apps.core.data.mappers.room.RoomPropertyDraftMapper
import com.estatia.realestate.apps.core.data.mappers.room.RoomPropertyMapper
import com.estatia.realestate.apps.core.data.mappers.room.RoomPropertyMapper.toCacheEntities
import com.estatia.realestate.apps.core.network.db_entities.PropertyContactEntity
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.model.property.PropertyPage
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

private const val MAX_CACHE_AGE_5_MIN = 5 * 60 * 1000L

internal class PropertyRepository @Inject constructor(
    private val localDataSource: IPropertyLocalDataSource,
    private val remoteDataSource: IPropertyRemoteDatasource,
    private val userRepository: IUserRepository,
    private val metricsTracker: IMetricsTracker,
    private val engagementRepository: IEngagementRepository,
    private val contentSafetyService: IContentSafetyService,
    private val exceptionTranslator: IExceptionTranslator
) : IPropertyRepository {

    override suspend fun saveDraft(
        draft: PropertyDraftDomainModel
    ): AppResult<Long> {
        val entity = RoomPropertyDraftMapper.toEntity(draft)
        return localDataSource.saveDraft(entity)
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun getAllDrafts(): AppResult<List<PropertyDraftDomainModel>> {
        return localDataSource.getAllDrafts()
            .map { entities ->
                entities.map(RoomPropertyDraftMapper::toDomain)
            }
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun getDraftById(
        draftId: Long
    ): AppResult<PropertyDraftDomainModel?> {
        return localDataSource.getDraftById(draftId)
            .map { entity ->
                RoomPropertyDraftMapper.toDomain(entity)
            }
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun deleteDraft(draftId: Long): AppResult<Unit> {
        return localDataSource.deleteDraft(draftId)
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun clearAllDrafts(): AppResult<Unit> {
        return localDataSource.clearAllDrafts()
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun uploadProperty(
        property: PropertyDomainModel,
        imageUris: List<MediaReference>,
        videoUris: List<MediaReference>
    ): AppResult<String> {

        // 🛡️ Proactive On-Device Moderation: Text
        property.description?.let { desc ->
            val textSafety = contentSafetyService.validateText(desc)
            if (textSafety is SafetyResult.Flagged) {
                return AppResult.Error(PropertyException.SafetyViolation("Description: ${textSafety.reason}"))
            }
        }

        // 🛡️ Proactive On-Device Moderation: Images
        imageUris.forEach { uri ->
            val imageSafety = contentSafetyService.validateImage(uri)
            if (imageSafety is SafetyResult.Flagged) {
                return AppResult.Error(PropertyException.SafetyViolation("Image: ${imageSafety.reason}"))
            }
        }

        // 🛡️ Proactive On-Device Moderation: Videos
        videoUris.forEach { uri ->
            val videoSafety = contentSafetyService.validateVideo(uri)
            if (videoSafety is SafetyResult.Flagged) {
                return AppResult.Error(PropertyException.SafetyViolation("Video: ${videoSafety.reason}"))
            }
        }

        val startTime = System.currentTimeMillis()
        val result = remoteDataSource
            .uploadProperty(
                RemotePropertyMapper.toEntity(property),
                PropertyContactEntity(
                    phone = property.contact.phone,
                    email = property.contact.email
                ),
                imageUris.map { it.value.toUri() },
                videoUris.map { it.value.toUri() }
            )
        
        val duration = System.currentTimeMillis() - startTime
        metricsTracker.trackDuration("property.upload.duration", duration.milliseconds)
        
        if (result is AppResult.Success) {
            metricsTracker.incrementCounter("property.upload.success")
        } else {
            metricsTracker.incrementCounter("property.upload.failure")
        }

        return result.translatePropertyFailures(
                exceptionTranslator
            )
    }

    override suspend fun updateProperty(
        propertyId: String,
        updates: Map<String, Any>
    ): AppResult<Unit> {

        return remoteDataSource
            .updateProperty(
                propertyId,
                updates
            )
            .translatePropertyFailures(
                exceptionTranslator
            )
    }

    override suspend fun deleteProperty(
        propertyId: String
    ): AppResult<Unit> {

        return remoteDataSource
            .deleteProperty(propertyId)
            .translatePropertyFailures(
                exceptionTranslator
            )
    }

    override suspend fun getPropertyById(
        propertyId: String
    ): AppResult<PropertyDomainModel> {

        return remoteDataSource
            .getPropertyById(propertyId)
            .map {
                RemotePropertyMapper.toDomain(it)
            }
            .translatePropertyFailures(
                exceptionTranslator
            )
    }

    override suspend fun getPropertiesByIds(
        propertyIds: List<String>
    ): AppResult<List<PropertyDomainModel>> {
        return localDataSource.getCachedPropertiesByIds(propertyIds)
            .map { entities ->
                entities.map(RoomPropertyMapper::toDomain)
            }
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun likeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit> {

        userRepository.setPropertyIdLiked(propertyId, true)

        return remoteDataSource
            .likeProperty(
                userId,
                propertyId
            )
            .translatePropertyFailures(
                exceptionTranslator
            )
    }

    override suspend fun unlikeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit> {

        userRepository.setPropertyIdLiked(propertyId, false)

        return remoteDataSource.unlikeProperty(
            userId,
            propertyId
        )
            .translatePropertyFailures(
                exceptionTranslator
            )
    }

    override suspend fun fetchLikedProperties(
        userId: String
    ): AppResult<List<PropertyDomainModel>> {

        return remoteDataSource
            .fetchLikedProperties(userId)
            .map { properties ->
                val domainModels = properties.map(RemotePropertyMapper::toDomain)
                localDataSource.cacheProperties(domainModels.toCacheEntities())
                domainModels
            }
            .translatePropertyFailures(
                exceptionTranslator
            ).let { result ->
                if (result is AppResult.Error) {
                    // Robust offline fallback: fetch liked IDs from local preferences
                    // and return corresponding properties from cache
                    val userData = userRepository.userData.firstOrNull()
                    val likedIds = userData?.likedProperties?.toList() ?: emptyList()
                    
                    if (likedIds.isNotEmpty()) {
                        localDataSource.getCachedPropertiesByIds(likedIds).map { entities ->
                            entities.map(RoomPropertyMapper::toDomain)
                        }
                    } else result
                } else result
            }
    }

    override suspend fun recordView(propertyId: String): AppResult<Unit> {
        // 🏎️ Report engagement signal for personalization
        engagementRepository.reportInteraction(propertyId, EngagementAction.VIEW)
        return remoteDataSource.recordView(propertyId)
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun recordShare(propertyId: String): AppResult<Unit> {
        // 🏎️ Report engagement signal for personalization
        engagementRepository.reportInteraction(propertyId, EngagementAction.SHARE)
        return remoteDataSource.recordShare(propertyId)
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun fetchPropertiesPaginated(
        userId: String?,
        cursor: PropertyCursor?,
        pageSize: Int
    ): AppResult<PropertyPage> {

        // 1. Try to serve from cache if first page and not stale
        // Personalized feeds are generally not cached locally to ensure fresh ML ranking
        if (cursor == null && userId == null) {
            val isStale = localDataSource.isCacheStale(MAX_CACHE_AGE_5_MIN).getOrNull() ?: true
            if (!isStale) {
                val cached = localDataSource.getCachedProperties().getOrNull()
                if (!cached.isNullOrEmpty()) {
                    return AppResult.Success(
                        PropertyPage(
                            properties = cached.map(RoomPropertyMapper::toDomain),
                            cursor = null // Cache only stores first page for now
                        )
                    )
                }
            }
        }

        // 2. Fetch from remote
        val startTime = System.currentTimeMillis()
        val remoteResult = remoteDataSource.fetchPropertiesPaginated(userId, cursor, pageSize)
        val duration = System.currentTimeMillis() - startTime
        metricsTracker.trackDuration("property.fetch_paginated.duration", duration.milliseconds)

        return remoteResult.map { remotePage ->
            val domainProperties = remotePage.properties.map(RemotePropertyMapper::toDomain)
            
            // 3. Update cache if it's the first page
            if (cursor == null) {
                localDataSource.cacheProperties(domainProperties.toCacheEntities())
            }

            PropertyPage(
                properties = domainProperties,
                cursor = remotePage.cursor
            )
        }.translatePropertyFailures(exceptionTranslator).let { result ->
            // 4. Fallback to cache on error if first page
            if (result is AppResult.Error && cursor == null) {
                val cached = localDataSource.getCachedProperties().getOrNull()
                if (!cached.isNullOrEmpty()) {
                    AppResult.Success(
                        PropertyPage(
                            properties = cached.map(RoomPropertyMapper::toDomain),
                            cursor = null
                        )
                    )
                } else result
            } else result
        }
    }
}
