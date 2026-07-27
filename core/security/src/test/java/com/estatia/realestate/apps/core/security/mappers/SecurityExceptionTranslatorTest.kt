package com.estatia.realestate.apps.core.security.mappers

import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import org.junit.Assert.assertEquals
import org.junit.Test
import java.security.InvalidKeyException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.SignatureException
import java.security.UnrecoverableKeyException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

class SecurityExceptionTranslatorTest {

    private val translator = SecurityExceptionTranslator()

    @Test
    fun `translate KeyStoreException returns KeyRetrievalFailed`() {
        val result = translator.translate(KeyStoreException(), SecurityException.KeyGenerationFailed)
        assertEquals(SecurityException.KeyRetrievalFailed, result)
    }

    @Test
    fun `translate UnrecoverableKeyException returns KeyRetrievalFailed`() {
        val result = translator.translate(UnrecoverableKeyException(), SecurityException.KeyGenerationFailed)
        assertEquals(SecurityException.KeyRetrievalFailed, result)
    }

    @Test
    fun `translate NoSuchAlgorithmException returns KeyGenerationFailed`() {
        val result = translator.translate(NoSuchAlgorithmException(), SecurityException.KeyGenerationRequired)
        assertEquals(SecurityException.KeyGenerationFailed, result)
    }

    @Test
    fun `translate NoSuchPaddingException returns KeyGenerationFailed`() {
        val result = translator.translate(NoSuchPaddingException(), SecurityException.KeyGenerationRequired)
        assertEquals(SecurityException.KeyGenerationFailed, result)
    }

    @Test
    fun `translate InvalidKeyException returns InvalidKey`() {
        val result = translator.translate(InvalidKeyException(), SecurityException.KeyGenerationFailed)
        assertEquals(SecurityException.InvalidKey, result)
    }

    @Test
    fun `translate SignatureException returns SignatureGenerationFailed`() {
        val exception = SignatureException("error")
        val result = translator.translate(exception, SecurityException.KeyGenerationFailed)
        assert(result is SecurityException.SignatureGenerationFailed)
        assertEquals(exception, (result as SecurityException.SignatureGenerationFailed).throwable)
    }

    @Test
    fun `translate IllegalBlockSizeException returns EncryptionFailed`() {
        val exception = IllegalBlockSizeException("error")
        val result = translator.translate(exception, SecurityException.KeyGenerationFailed)
        assert(result is SecurityException.EncryptionFailed)
        assertEquals(exception, (result as SecurityException.EncryptionFailed).throwable)
    }

    @Test
    fun `translate BadPaddingException returns DecryptionFailed`() {
        val exception = BadPaddingException("error")
        val result = translator.translate(exception, SecurityException.KeyGenerationFailed)
        assert(result is SecurityException.DecryptionFailed)
        assertEquals(exception, (result as SecurityException.DecryptionFailed).throwable)
    }

    @Test
    fun `translate SecurityException returns itself`() {
        val exception = SecurityException.InvalidCredentials
        val result = translator.translate(exception, SecurityException.KeyGenerationFailed)
        assertEquals(exception, result)
    }

    @Test
    fun `translate unknown Exception returns default`() {
        val exception = RuntimeException("error")
        val result = translator.translate(exception, SecurityException.KeyGenerationFailed)
        assertEquals(SecurityException.KeyGenerationFailed, result)
    }
}
