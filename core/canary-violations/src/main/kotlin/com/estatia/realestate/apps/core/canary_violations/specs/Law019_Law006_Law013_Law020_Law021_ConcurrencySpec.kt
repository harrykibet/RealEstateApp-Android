package com.estatia.realestate.apps.core.canary_violations.specs

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Concurrency Spec
 */

public class PositiveConcurrencySpec : CoroutineScope {
    // [CANARY:POSITIVE:HardcodedDispatcher:BLOCK:Hardcoded dispatcher]
    override val coroutineContext: CoroutineContext = Dispatchers.Main
    
    public suspend fun doTrickyWork() {
        // [CANARY:POSITIVE:SecretConcurrency:BLOCK:independent work]
        launch { }
        
        // [CANARY:POSITIVE:UnusedAsync:BLOCK:Async results]
        async { 1 }
        
        // [CANARY:POSITIVE:MisplacedCoroutineExceptionHandler:WARN:Exception handlers must be placed] [CANARY:POSITIVE:HardcodedDispatcher:BLOCK:Hardcoded dispatcher]
        withContext(Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) { }
    }
    
    public suspend fun zombieLoop() {
        // [CANARY:POSITIVE:MissingCoroutineCancellation:BLOCK:cancellable]
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
