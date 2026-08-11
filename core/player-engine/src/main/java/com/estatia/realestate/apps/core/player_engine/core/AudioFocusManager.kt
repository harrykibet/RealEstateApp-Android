package com.estatia.realestate.apps.core.player_engine.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import com.estatia.realestate.apps.core.player_engine.di.PlayerDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles Android audio focus requests and transitions.
 */
@UnstableApi
@Singleton
class AudioFocusManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @param:EngineScope private val engineScope: CoroutineScope,
    @param:PlayerDispatcher private val playerDispatcher: CoroutineDispatcher
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var activeFocusRequest: AudioFocusRequest? = null
    
    private var onFocusLost: (() -> Unit)? = null
    private var onFocusGained: (() -> Unit)? = null

    private val noisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                engineScope.launch(playerDispatcher) {
                    onFocusLost?.invoke()
                }
            }
        }
    }

    private val audioFocusListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        engineScope.launch(playerDispatcher) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS,
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> onFocusLost?.invoke()
                AudioManager.AUDIOFOCUS_GAIN -> onFocusGained?.invoke()
            }
        }
    }

    init {
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(noisyReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(noisyReceiver, filter)
        }
    }

    fun setCallbacks(onLost: () -> Unit, onGained: () -> Unit) {
        this.onFocusLost = onLost
        this.onFocusGained = onGained
    }

    fun request(): Boolean {
        // Idempotency check: don't churn requests if we already have an active one
        if (activeFocusRequest != null) return true

        val audioManager = audioManager ?: return false

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
            .build()

        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(attributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(audioFocusListener)
            .build()

        activeFocusRequest = request
        return audioManager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun abandon() {
        activeFocusRequest?.let { request ->
            audioManager?.abandonAudioFocusRequest(request)
        }
        activeFocusRequest = null
    }

    fun cleanup() {
        try {
            context.unregisterReceiver(noisyReceiver)
        } catch (_: Exception) { }
    }
}
