package com.estatia.realestate.apps.core.domain.config

import com.estatia.realestate.apps.core.model.config.ChaosConfig

interface IChaosConfig : IConfigLifecycle {
    val chaosConfig: ChaosConfig
}
