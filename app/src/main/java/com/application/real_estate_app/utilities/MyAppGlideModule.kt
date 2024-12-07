package com.application.real_estate_app.utilities


import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.crashlytics.FirebaseCrashlytics

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    private val customStrategy = GlideExecutor.UncaughtThrowableStrategy { throwable ->
        Log.e("GlideError", "Uncaught throwable in Glide executor", throwable)
        // Log the error to Firebase Crashlytics
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val memoryCacheSizeBytes = 1024 * 1024 * 20 //100MB
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes.toLong()))

        val bitmapPoolSizeBytes = 1024 * 1024 * 30 //100MB
        builder.setBitmapPool(LruBitmapPool(bitmapPoolSizeBytes.toLong()))
    }
}