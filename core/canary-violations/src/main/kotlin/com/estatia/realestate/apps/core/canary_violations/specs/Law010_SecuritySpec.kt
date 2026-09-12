package com.estatia.realestate.apps.core.canary_violations.specs

import android.util.Log

/**
 * LAW-010: Security & PII Protection
 */

public class Law010_Positive_Spec {
    // [CANARY:POSITIVE:HardcodedSecrets:WARN:hardcoded secret]
    public val apiKey: String = "AKIAIOSFODNN7EXAMPLE"
    
    public fun log(password: String) {
        // [CANARY:POSITIVE:HardcodedSecrets:WARN:hardcoded secret]
        val secret: String = "super_secret_token"
        
        // [CANARY:POSITIVE:SensitiveLogging:WARN:sensitive data]
        Log.d("AUTH", "User password is: $password")
    }
}

public class Law010_Negative_Spec {
    public fun safeLog() {
        Log.d("APP", "App started")
    }
}
