package com.estatia.realestate.apps.core.canary_violations.specs

import androidx.compose.runtime.Composable

public object CanaryConfig {
    @JvmField
    public var mutableValue: Int = 0
}

public class Law025_Compose_Positive_Spec {
    @Composable
    public fun PositiveCanary() {
        // [CANARY:POSITIVE:ComposeMutableSingletonRead]
        val x = CanaryConfig.mutableValue
    }
}
