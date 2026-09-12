package com.estatia.realestate.apps.core.canary_violations.specs

import android.content.Context
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import javax.inject.Singleton

public class Law011_Performance_Positive_Spec {
    @Composable
    public fun PositiveCanary() {
        // [CANARY:POSITIVE:BlockingMainThreadWork:BLOCK:background dispatcher]
        Thread.sleep(1000)
        
        // [CANARY:POSITIVE:UnboundedBuffer:BLOCK:Large replay buffer]
        val flow = MutableSharedFlow<Int>(replay = 101)
        
        // [CANARY:POSITIVE:UnboundedBuffer:BLOCK:Unbounded buffer]
        val flow2 = flow { emit(1) }.buffer()
    }
}

@Singleton
public class Law024_Performance_Positive_Spec(
    // [CANARY:POSITIVE:ContextLeak:BLOCK:Context stored]
    public val context: Context
)
