package com.estatia.realestate.apps.core.canary_violations.specs

import android.util.Log
import com.estatia.realestate.apps.core.common.annotations.Repository
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * LAW-008: Abstraction Boundaries
 * LAW-009: Explicit Failure Handling
 */

public interface ICanaryRepo

/**
 * [CANARY:POSITIVE:MissingVisibilityModifier]
 */
class Law008_Positive_Repository : ICanaryRepo {
    // [CANARY:POSITIVE:ImplementationTypeInPublicApi]
    public fun leak(retrofit: Retrofit?): String = ""
}

/**
 * ✅ NEGATIVE CANARY
 */
public interface IValidRepo

@Repository
public class Law008_Negative_Repository @Inject constructor() : IValidRepo {
    public fun safe(id: String): String = id
}

@Repository
public class Law009_Positive_Repository {
    // [CANARY:POSITIVE:MissingResultWrapper]
    // [CANARY:POSITIVE:MissingVisibilityModifier]
    fun getRawData(): List<String> = emptyList()
    
    public fun smuggled(): List<String> {
        // [CANARY:POSITIVE:FailureSmuggling]
        try { return listOf("a") } catch (e: Exception) { return emptyList() }
    }
    
    public fun dangerous(data: String?): List<String> {
        // [CANARY:POSITIVE:DangerousFallback]
        return data?.let { listOf(it) } ?: emptyList()
    }
}

@Repository
public class Law009_Negative_Repository {
    // [CANARY:NEGATIVE:MissingResultWrapper]
    public fun safeData(): AppResult<String> = AppResult.Success("ok")
    
    // [CANARY:NEGATIVE:FailureSmuggling]
    public fun safeCatch() {
        try { println() } catch (e: Exception) { Log.e("ERR", "failed", e) }
    }
}
