package com.estatia.realestate.apps.core.domain.config

import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig

interface IPlayerTuningConfig : IConfigLifecycle {
    val playerTuning: PlayerTuningConfig
}
