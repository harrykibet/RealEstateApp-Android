package com.estatia.realestate.apps.core.intelligence

import android.net.Uri
import com.estatia.realestate.apps.core.model.user.FaceMatchResult
import com.estatia.realestate.apps.core.model.user.IdDocumentResult

/**
 * Service for handling identity and asset verification using on-device ML.
 */
interface IVerificationService {
    /**
     * Scans a government-issued ID and extracts key information.
     */
    suspend fun scanIdDocument(imageUri: Uri): IdDocumentResult

    /**
     * Compares a selfie against an ID photo to verify a match.
     */
    suspend fun verifyFaceMatch(idPhotoUri: Uri, selfieUri: Uri): FaceMatchResult

    /**
     * Checks a short video for human liveness (blink, head turn, etc.).
     */
    suspend fun verifyLiveness(videoUri: Uri): Boolean

    /**
     * Verifies physical presence at a location by checking signed media metadata.
     */
    suspend fun verifyPhysicalPresence(mediaUri: Uri, expectedLat: Double, expectedLng: Double): Boolean
}
