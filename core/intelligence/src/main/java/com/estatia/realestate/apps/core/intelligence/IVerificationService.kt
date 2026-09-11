package com.estatia.realestate.apps.core.intelligence

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.model.user.FaceMatchResult
import com.estatia.realestate.apps.core.model.user.IdDocumentResult
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

/**
 * Service for handling identity and asset verification using on-device ML.
 */
@Contract
interface IVerificationService {
    /**
     * Scans a government-issued ID and extracts key information.
     */
    suspend fun scanIdDocument(imageUri: MediaReference): AppResult<IdDocumentResult>

    /**
     * Compares a selfie against an ID photo to verify a match.
     */
    suspend fun verifyFaceMatch(idPhotoUri: MediaReference, selfieUri: MediaReference): AppResult<FaceMatchResult>

    /**
     * Checks a short video for human liveness (blink, head turn, etc.).
     */
    suspend fun verifyLiveness(videoUri: MediaReference): AppResult<Boolean>

    /**
     * Verifies physical presence at a location by checking signed media metadata.
     */
    suspend fun verifyPhysicalPresence(mediaUri: MediaReference, expectedLat: Double, expectedLng: Double): AppResult<Boolean>
}
