package com.estatia.realestate.apps.core.canary_violations.specs

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Concurrency Spec
 */

public class PositiveConcurrencySpec : CoroutineScope {
    // [CANARY:POSITIVE:HardcodedDispatcher]
    override val coroutineContext: CoroutineContext = Dispatchers.Main
    
    public suspend fun doTrickyWork() {
        // [CANARY:POSITIVE:SecretConcurrency]
        launch { }
        
        // [CANARY:POSITIVE:UnusedAsync]
        async { 1 }
        
        // [CANARY:POSITIVE:MisplacedCoroutineExceptionHandler] [CANARY:POSITIVE:HardcodedDispatcher]
        withContext(Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) { }
    }
    
    public suspend fun zombieLoop() {
        // [CANARY:POSITIVE:MissingCoroutineCancellation]
        while(true) {
            println("zombie")
        }
    }
}

public class NegativeConcurrencySpec {
    public suspend fun safeWork() = coroutineScope {
        yield()
        val result = async { 1 }.await()
    }
}
