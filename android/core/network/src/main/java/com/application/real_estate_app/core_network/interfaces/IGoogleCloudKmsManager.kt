package com.application.real_estate_app.core_network.interfaces

interface IGoogleCloudKmsManager {
    suspend fun encryptDataSymmetric(plaintext: String): String
    suspend fun decryptDataSymmetric(ciphertextBase64: String): String
    suspend fun encryptDataAsymmetric(plaintext: String): String
    suspend fun decryptDataAsymmetric(ciphertextBase64: String): String
    suspend fun listKeys(): List<String>
    suspend fun signData(data: String): String
    suspend fun verifySignature(data: String, signatureBase64: String): Boolean
}