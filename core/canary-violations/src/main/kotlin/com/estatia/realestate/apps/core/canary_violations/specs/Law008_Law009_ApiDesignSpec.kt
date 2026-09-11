package com.estatia.realestate.apps.core.canary_violations.specs

import android.util.Log
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Repository
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * LAW-008: Abstraction Boundaries
 * LAW-009: Explicit Failure Handling
 */

@Contract
public interface ICanaryRepo

// [CANARY:POSITIVE:MissingVisibilityModifier]
@Repository
class PositiveRepository : ICanaryRepo {
    // [CANARY:POSITIVE:ImplementationTypeInPublicApi] [CANARY:POSITIVE:MissingVisibilityModifier]
    public fun leak(retrofit: Retrofit?): String = ""
}

@Repository
public class PositiveFailureRepository {
    // [CANARY:POSITIVE:MissingResultWrapper] [CANARY:POSITIVE:MissingVisibilityModifier]
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

/**
 * ✅ NEGATIVE CANARIES
 */

@Repository
public class NegativeRepository @Inject constructor() : ICanaryRepo {
    // [CANARY:NEGATIVE:MissingVisibilityModifier]
    // [CANARY:NEGATIVE:ImplementationTypeInPublicApi]
    // [CANARY:NEGATIVE:MissingResultWrapper]
    public fun safeData(): AppResult<String> = AppResult.Success("ok")
    
    // [CANARY:NEGATIVE:FailureSmuggling]
    public fun safeCatch() {
        try { println() } catch (e: Exception) { Log.e("ERR", "failed", e) }
    }
}
