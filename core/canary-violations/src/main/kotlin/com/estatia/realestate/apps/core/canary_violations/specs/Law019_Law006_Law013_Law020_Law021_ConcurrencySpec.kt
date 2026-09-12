package com.estatia.realestate.apps.core.canary_violations.specs

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Concurrency Spec
 */

public class PositiveConcurrencySpec : CoroutineScope {
    // [CANARY:POSITIVE:HardcodedDispatcher:BLOCK:hardcoded Dispatcher]
    override val coroutineContext: CoroutineContext = Dispatchers.Main
    
    public suspend fun doTrickyWork() {
        // [CANARY:POSITIVE:SecretConcurrency:BLOCK:launch]
        launch { }
        
        // [CANARY:POSITIVE:UnusedAsync:BLOCK:await]
        async { 1 }
        
        // [CANARY:POSITIVE:MisplacedCoroutineExceptionHandler:WARN:ExceptionHandler] [CANARY:POSITIVE:HardcodedDispatcher:BLOCK:hardcoded Dispatcher]
        withContext(Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) { }
    }
    
    public suspend fun zombieLoop() {
        // [CANARY:POSITIVE:MissingCoroutineCancellation:BLOCK:zombieLoop]
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
