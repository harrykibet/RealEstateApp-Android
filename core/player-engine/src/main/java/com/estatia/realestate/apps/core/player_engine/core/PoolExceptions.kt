package com.estatia.realestate.apps.core.player_engine.core

import java.io.IOException

/**
 * Thrown when a non-urgent player request is rejected because the pool is at capacity.
 */
class PoolCapacityExceededException(message: String) : IOException(message)
