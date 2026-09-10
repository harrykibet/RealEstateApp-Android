package com.estatia.realestate.apps.core.player_engine.core

import com.estatia.realestate.apps.core.architecture.annotations.Helper

import java.io.IOException

/**
 * Thrown when a non-urgent player request is rejected because the pool is at capacity.
 */
@Helper
class PoolCapacityExceededException(message: String) : IOException(message)
