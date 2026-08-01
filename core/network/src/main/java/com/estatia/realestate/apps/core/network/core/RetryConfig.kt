package com.estatia.realestate.apps.core.network.core

data class RetryConfig(

    val name:String,

    val maxAttempts:Int,

    val initialDelayMs:Long,

    val maxDelayMs:Long,

    val multiplier:Double = 2.0

)
