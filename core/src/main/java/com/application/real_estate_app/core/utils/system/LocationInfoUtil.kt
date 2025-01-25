package com.application.real_estate_app.core.utils.system

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.location.Location
import android.location.Geocoder
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.application.real_estate_app.core.domain.models.UserLocation
import java.util.Locale

object LocationInfoUtil {

    fun getLocationInfo(context: Context): UserLocation {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!isLocationEnabled(locationManager)) {
            //Logger.error("Location services are disabled")
            return UserLocation(
                country = "Unknown",
                city = "Unknown",
                latitude = 0.0,
                longitude = 0.0
            )
        }

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Logger.error("Location permissions are not granted")
            return UserLocation(
                country = "Unknown",
                city = "Unknown",
                latitude = 0.0,
                longitude = 0.0
            )
        }

        // Safe to access location
        val location: Location? = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        var country = "Unknown"
        var city = "Unknown"
        var latitude = 0.0
        var longitude = 0.0

        location?.let {
            latitude = it.latitude
            longitude = it.longitude

            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val address = geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
                country = address?.countryName ?: country
                city = address?.locality ?: city
            } catch (e: Exception) {
                //Logger.error("Geocoding failed: ${e.message}")
            }
        }

        return UserLocation(
            country = country,
            city = city,
            latitude = latitude,
            longitude = longitude
        )
    }
}
