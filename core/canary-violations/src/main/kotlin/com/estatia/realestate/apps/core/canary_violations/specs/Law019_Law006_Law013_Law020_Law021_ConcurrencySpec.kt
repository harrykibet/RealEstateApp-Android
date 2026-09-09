package com.estatia.realestate.apps.core.canary_violations.specs

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * LAW-019: Secret Concurrency
 * LAW-006: Hardcoded Dispatchers
 * LAW-013: Cancellation
 * LAW-020: Async Usage
 * LAW-021: Exception Handlers
 */

public class Law019_006_013_020_021_Positive_Spec : CoroutineScope {
    // [CANARY:POSITIVE:HardcodedDispatcher]
    override val coroutineContext: CoroutineContext = Dispatchers.Main
    
    public suspend fun doTrickyWork() {
        // [CANARY:POSITIVE:SecretConcurrency]
        launch { }
        
        val deferred = async { 
            // [CANARY:POSITIVE:UnusedAsync]
            1 
        }
        
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

public class Concurrency_Negative_Spec {
    public suspend fun safeWork() = coroutineScope {
        launch { yield() }
        val result = async { 1 }.await()
    }
}
