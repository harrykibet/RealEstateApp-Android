package com.estatia.realestate.apps.core.player_engine.utils

interface IPlayerPoolSizingPolicy {
    fun calculateMaxPoolSize(): Int
}