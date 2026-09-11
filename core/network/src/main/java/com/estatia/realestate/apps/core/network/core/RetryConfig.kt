package com.estatia.realestate.apps.core.network.core
import com.estatia.realestate.apps.core.architecture.annotations.Data.EntityModel

@EntityModel
data class RetryConfig(

    val name:String,

    val maxAttempts:Int,

    val initialDelayMs:Long,

    val maxDelayMs:Long,

    val multiplier:Double = 2.0,

    val maxTotalDurationMs: Long? = null

)
