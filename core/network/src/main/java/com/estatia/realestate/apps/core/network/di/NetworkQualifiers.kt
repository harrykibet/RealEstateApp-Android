package com.estatia.realestate.apps.core.network.di

import javax.inject.Qualifier

/**
 * Qualifier for an OkHttpClient tuned for fast-failing authentication requests.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthClient

/**
 * Qualifier for an OkHttpClient tuned for long-running media upload requests.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UploadClient

/**
 * Qualifier for an OkHttpClient optimized for media playback (high throughput, multiplexing).
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlaybackClient

/**
 * Qualifier for a set of OkHttp interceptors to be added to all network clients.
 * Used to inject ChaosInterceptor in test environments.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkInterceptors
