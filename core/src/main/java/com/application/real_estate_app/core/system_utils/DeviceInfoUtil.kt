package com.application.real_estate_app.core.system_utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.application.real_estate_app.core.data_utils.data_models.DeviceInfo

object DeviceInfoUtil {

    fun getDeviceInfo(context: Context): DeviceInfo {
        val displayMetrics = context.resources.displayMetrics
        val screenResolution = "${displayMetrics.widthPixels}x${displayMetrics.heightPixels}"
        val appVersion = context.packageManager.getPackageInfo(context.packageName, 0)?.versionName

        return DeviceInfo(
            os = "Android ${Build.VERSION.RELEASE}",
            browser = "N/A",
            deviceType = if (isTablet(context)) "Tablet" else "Phone",
            screenResolution = screenResolution,
            appVersion = appVersion!!
        )
    }

    private fun isTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }
}
