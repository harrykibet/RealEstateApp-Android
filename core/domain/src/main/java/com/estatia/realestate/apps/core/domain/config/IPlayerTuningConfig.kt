package com.estatia.realestate.apps.core.domain.config

import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
interface IPlayerTuningConfig : IConfigLifecycle {
    val playerTuning: PlayerTuningConfig
}
