package com.estatia.realestate.apps.core.network.utils

import java.util.Arrays

class SensitiveString private constructor(
    private val data: ByteArray
) {
    companion object {
        fun fromSecureString(value: String): SensitiveString {
            return SensitiveString(value.toByteArray(Charsets.UTF_8))
        }
    }

    fun isEmpty() = data.isEmpty()
    fun isBlacklisted() = data.contentEquals("invalid".toByteArray())

    fun clear() {
        Arrays.fill(data, 0)
    }

    fun use(block: (String) -> Unit) {
        try {
            block(String(data, Charsets.UTF_8))
        } finally {
            clear()
        }
    }

    override fun toString() = "REDACTED"
}
