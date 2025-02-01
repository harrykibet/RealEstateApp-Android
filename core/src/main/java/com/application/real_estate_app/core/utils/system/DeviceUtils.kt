package com.application.real_estate_app.core.utils.system

import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.media.MediaCodecList
import android.os.Build
import android.view.Display
import androidx.core.app.ActivityManagerCompat
import com.application.real_estate_app.core.domain.models.DeviceInfo
import javax.inject.Inject

class DeviceUtils @Inject constructor(
    private val context: Context,
    private val displayManager: DisplayManager
) {

    fun getDeviceInfo(): DeviceInfo {
        val displayMetrics = context.resources.displayMetrics
        val screenResolution = "${displayMetrics.widthPixels}x${displayMetrics.heightPixels}"
        val appVersion = context.packageManager.getPackageInfo(context.packageName, 0)?.versionName

        return DeviceInfo(
            os = "Android ${Build.VERSION.RELEASE}",
            browser = "N/A",
            deviceType = if (isTablet()) "Tablet" else "Phone",
            screenResolution = screenResolution,
            appVersion = appVersion!!
        )
    }

    private fun isTablet(): Boolean {
        return (context.resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    // region Media Capabilities
    fun supportsAV1(): Boolean {
        return MediaCodecList(MediaCodecList.ALL_CODECS).findEncoderForMimeType("video/av01") != null
    }

    fun supportsHDR(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
            display.hdrCapabilities?.supportedHdrTypes?.isNotEmpty() ?: false
        } else {
            false
        }
    }

    fun getMaxSupportedBitrate(): Long {
        return when {
            isHighEndDevice() -> 20_000_000 // 20 Mbps
            isMidRangeDevice() -> 8_000_000
            else -> 2_000_000
        }
    }

    fun getOptimalVideoResolution(): Pair<Int, Int> {
        return when {
            supports4K() -> 3840 to 2160
            supports1440p() -> 2560 to 1440
            else -> 1920 to 1080
        }
    }
    // endregion

    // region General Device Info
    fun isHighEndDevice(): Boolean {
        return Runtime.getRuntime().availableProcessors() >= 8 &&
                context.getSystemService<ActivityManager>()!!.memoryClass >= 512
    }

    fun isLowRamDevice(): Boolean {
        return ActivityManagerCompat.isLowRamDevice(
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        )
    }

    fun getRefreshRate(): Float {
        return displayManager.getDisplay(Display.DEFAULT_DISPLAY).refreshRate
    }
    // endregion

    // region Private helpers
    private fun supports4K(): Boolean {
        return context.resources.configuration.smallestScreenWidthDp >= 600 &&
                isHighEndDevice()
    }

    private fun supports1440p(): Boolean {
        return context.resources.configuration.smallestScreenWidthDp >= 400
    }
    // endregion

    // Memory constraints
    fun getAvailableMemoryMB(): Long {
        val memInfo = ActivityManager.MemoryInfo()
        (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .getMemoryInfo(memInfo)
        return memInfo.availMem / (1024 * 1024)
    }
}
