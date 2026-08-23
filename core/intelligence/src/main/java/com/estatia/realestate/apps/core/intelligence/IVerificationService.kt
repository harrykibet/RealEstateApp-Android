package com.estatia.realestate.apps.core.intelligence

import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.model.user.FaceMatchResult
import com.estatia.realestate.apps.core.model.user.IdDocumentResult

/**
 * Service for handling identity and asset verification using on-device ML.
 */
interface IVerificationService {
    /**
     * Scans a government-issued ID and extracts key information.
     */
    suspend fun scanIdDocument(imageUri: MediaReference): IdDocumentResult

    /**
     * Compares a selfie against an ID photo to verify a match.
     */
    suspend fun verifyFaceMatch(idPhotoUri: MediaReference, selfieUri: MediaReference): FaceMatchResult

    /**
     * Checks a short video for human liveness (blink, head turn, etc.).
     */
    suspend fun verifyLiveness(videoUri: MediaReference): Boolean

    /**
     * Verifies physical presence at a location by checking signed media metadata.
     */
    suspend fun verifyPhysicalPresence(mediaUri: MediaReference, expectedLat: Double, expectedLng: Double): Boolean
}
