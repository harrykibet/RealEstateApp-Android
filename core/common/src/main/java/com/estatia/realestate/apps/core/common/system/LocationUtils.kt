package com.estatia.realestate.apps.core.common.system

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.LocationManager
import android.location.Location
import android.location.Geocoder
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.estatia.realestate.apps.core.common.interfaces.ILocationUtils
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.model.user.UserLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject

/**
 * Android implementation of [ILocationUtils] for retrieving user location data.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage system-level location retrieval and reverse geocoding.
 * - Concurrency: Thread-safe via [suspendCancellableCoroutine] and background geocoding listeners.
 * - Resilience: Surfaces an [unknownLocation] fallback for disabled services or denied permissions.
 * - Security: Strictly respects [Manifest.permission.ACCESS_FINE_LOCATION] and [Manifest.permission.ACCESS_COARSE_LOCATION].
 * - Performance: Avoids blocking the Main thread during heavy reverse geocoding operations.
 */
class LocationUtils @Inject constructor(
    private val context: Context,
    private val logger: ILogger
) : ILocationUtils {

    override suspend fun getLocationInfo(): UserLocation {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!isLocationEnabled(locationManager)) {
            logger.e(message = "Location services are disabled")
            return unknownLocation()
        }

        if (
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            logger.e(message = "Location permissions are not granted")
            return unknownLocation()
        }

        val location =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?: return unknownLocation()

        return reverseGeocode(location)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun reverseGeocode(location: Location): UserLocation =
        suspendCancellableCoroutine { cont ->
            val geocoder = Geocoder(context, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1,
                    object : Geocoder.GeocodeListener {

                        override fun onGeocode(addresses: MutableList<Address>) {
                            val address = addresses.firstOrNull()

                            cont.resume(
                                UserLocation(
                                    country = address?.countryName ?: "Unknown",
                                    city = address?.locality ?: "Unknown",
                                    latitude = location.latitude,
                                    longitude = location.longitude
                                )
                            ) { _, _, _ -> }
                        }

                        override fun onError(errorMessage: String?) {
                            logger.e(message = "Geocoding failed: $errorMessage")
                            cont.resume(
                                UserLocation(
                                    country = "Unknown",
                                    city = "Unknown",
                                    latitude = location.latitude,
                                    longitude = location.longitude
                                )
                            ) { _, _, _ -> }
                        }
                    }
                )
            }
        }

    private fun unknownLocation() = UserLocation(
        country = "Unknown",
        city = "Unknown",
        latitude = 0.0,
        longitude = 0.0
    )
}
