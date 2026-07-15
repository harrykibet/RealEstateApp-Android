package com.estatia.realestate.apps.core.network.core

object RetryConfigs {


    val PROPERTY_FEED =
        RetryConfig(
            name = "PROPERTY_FEED",
            maxAttempts = 3,
            initialDelayMs = 500,
            maxDelayMs = 5000
        )

    val ANALYTICS =
        RetryConfig(
            name = "ANALYTICS",
            maxAttempts = 1,
            initialDelayMs = 500,
            maxDelayMs = 5000
        )

    val COMMENTS =
        RetryConfig(
            name = "COMMENTS",
            maxAttempts = 3,
            initialDelayMs = 500,
            maxDelayMs = 5000
        )


    val AUTH =
        RetryConfig(
            name = "AUTH",
            maxAttempts = 1,
            initialDelayMs = 0,
            maxDelayMs = 0
        )


    val IMAGE_UPLOAD =
        RetryConfig(
            name = "IMAGE_UPLOAD",
            maxAttempts = 5,
            initialDelayMs = 1000,
            maxDelayMs = 30000
        )

    val VIDEO_UPLOAD =
        RetryConfig(
            name = "VIDEO_UPLOAD",
            maxAttempts = 4,
            initialDelayMs = 1000,
            maxDelayMs = 30000
        )


    val PAYMENT =
        RetryConfig(
            name = "PAYMENT",
            maxAttempts = 1,
            initialDelayMs = 0,
            maxDelayMs = 0
        )


    val CHAT =
        RetryConfig(
            name = "CHAT",
            maxAttempts = Int.MAX_VALUE,
            initialDelayMs = 1000,
            maxDelayMs = 60000
        )
}