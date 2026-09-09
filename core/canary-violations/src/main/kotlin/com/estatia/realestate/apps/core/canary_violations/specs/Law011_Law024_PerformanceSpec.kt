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
        // [CANARY:POSITIVE:BlockingMainThreadWork]
        Thread.sleep(1000)
        
        // [CANARY:POSITIVE:UnboundedBuffer]
        val flow = MutableSharedFlow<Int>(replay = 101)
        
        // [CANARY:POSITIVE:UnboundedBuffer]
        val flow2 = flow { emit(1) }.buffer()
    }
}

@Singleton
public class Law024_Performance_Positive_Spec(
    // [CANARY:POSITIVE:ContextLeak]
    public val context: Context
)
