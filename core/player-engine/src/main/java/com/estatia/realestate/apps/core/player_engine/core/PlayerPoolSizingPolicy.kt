package com.estatia.realestate.apps.core.player_engine.core

interface PlayerPoolSizingPolicy {
    fun calculateMaxPoolSize(): Int
}