import android.content.Context
import android.os.Build
import android.os.ThermalManager
import androidx.annotation.RequiresApi
import androidx.media3.common.util.UnstableApi
import com.application.real_estate_app.core.utils.media_players.exoplayer.MediaPlayer

@RequiresApi(Build.VERSION_CODES.R)
@UnstableApi
class ThermalStateMonitor(
    private val context: Context,
    private val player: MediaPlayer
) {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            registerThermalCallback()
        } else {
            setupFallbackMonitoring()
        }
    }

    // Registers the thermal callback for API 30+
    @RequiresApi(Build.VERSION_CODES.R)
    private fun registerThermalCallback() {
        val thermalManager = context.getSystemService(Context.THERMAL_SERVICE) as? ThermalManager
        thermalManager?.addThermalStatusListener(context.mainExecutor) { status ->
            handleThermalStatus(status)
        }
    }

    // Handles the thermal status and applies mitigation strategies
    private fun handleThermalStatus(status: Int) {
        when (status) {
            ThermalManager.THERMAL_STATUS_SEVERE -> player.applyThermalMitigation(level = 2)
            ThermalManager.THERMAL_STATUS_MODERATE -> player.applyThermalMitigation(level = 1)
            else -> player.resetThermalMitigation()
        }
    }

    // Sets up fallback monitoring for devices running below API 30
    private fun setupFallbackMonitoring() {
        BatteryTemperatureMonitor(context) { temperature ->
            if (temperature > 45) {
                player.applyThermalMitigation(level = 2) // Severe threshold
            } else if (temperature > 40) {
                player.applyThermalMitigation(level = 1) // Moderate threshold
            } else {
                player.resetThermalMitigation()
            }
        }.start()
    }
}

// Fallback for older devices: Monitors battery temperature
private class BatteryTemperatureMonitor(
    private val context: Context,
    private val onTemperatureUpdate: (Float) -> Unit
) {
    fun start() {
        // TODO: Implement logic to monitor battery temperature using BatteryManager
        // For demonstration purposes, we'll simulate temperature updates
        val simulatedTemperature = 42.5f // Replace with real monitoring
        onTemperatureUpdate(simulatedTemperature)
    }
}

// Extensions for the MediaPlayer class
fun MediaPlayer.applyThermalMitigation(level: Int) {
    when (level) {
        1 -> { // Moderate mitigation
            trackSelector.setParameters(
                trackSelector.parameters.buildUpon()
                    .setMaxVideoSize(1280, 720) // Limit video resolution
                    .build()
            )
        }
        2 -> { // Severe mitigation
            exoPlayer.setVideoEffects(listOf(ReduceFrameRateEffect())) // Reduce frame rate
            trackSelector.setParameters(
                trackSelector.parameters.buildUpon()
                    .setMaxVideoSize(854, 480) // Limit resolution further
                    .build()
            )
        }
    }
}

fun MediaPlayer.resetThermalMitigation() {
    trackSelector.setParameters(
        trackSelector.parameters.buildUpon()
            .clearVideoSizeConstraints() // Reset video constraints
            .build()
    )
    exoPlayer.clearVideoEffects() // Remove all applied video effects
}
