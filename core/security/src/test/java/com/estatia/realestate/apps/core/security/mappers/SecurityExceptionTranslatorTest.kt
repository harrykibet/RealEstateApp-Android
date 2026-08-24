package com.estatia.realestate.apps.core.security.mappers

import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.testing.chaos.auth.AuthBehavior
import org.junit.Assert.assertEquals
import org.junit.Test
import java.security.InvalidKeyException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.SignatureException
import java.security.UnrecoverableKeyException

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
    fun `translate unknown Exception returns default fallback`() {
        // 🧪 Adversarial Behavior: Unexpected Exception during Crypto
        val behavior = AuthBehavior.SessionRestorationFailure
        println("Testing mapping for: $behavior")
        
        val exception = RuntimeException("Unknown crypto error")
        val result = translator.translate(exception, SecurityException.KeyGenerationFailed)
        assertEquals(SecurityException.KeyGenerationFailed, result)
    }
}
