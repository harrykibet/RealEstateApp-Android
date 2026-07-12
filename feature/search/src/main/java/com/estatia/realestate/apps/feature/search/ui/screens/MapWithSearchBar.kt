package com.estatia.realestate.apps.feature.search.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.estatia.realestate.apps.feature.search.ui.viewmodels.SearchViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import com.google.android.libraries.places.api.Places
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.LaunchedEffect
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.estatia.realestate.apps.feature.search.BuildConfig

@Composable
fun MapWithSearchBar(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val mapView = rememberMapViewWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    var map by remember { mutableStateOf<GoogleMap?>(null) }
    var zoomOutCount by remember { mutableIntStateOf(0) }
    val maxZoomOut = 1

    // Initialize Places
    LaunchedEffect(Unit) {
        viewModel.initializePlaces(context)
    }

    val placesClient = remember { Places.createClient(context) }

    var searchQuery by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search location") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    coroutineScope.launch {
                        searchForLocation(
                            query = searchQuery,
                            placesClient = placesClient,
                            onLocationFound = { latLng ->
                                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                                if (!viewModel.loadNearbyProperties(map!!, latLng.latitude, latLng.longitude)) {
                                    handleNoNearbyProperties(context, map!!, viewModel, zoomOutCount, maxZoomOut) {
                                        zoomOutCount++
                                    }
                                }
                            },
                            onError = {
                                Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            )
        )

        // MapView
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = {
                mapView.getMapAsync { googleMap ->
                    map = googleMap

                    // Enable location if permitted
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        googleMap.isMyLocationEnabled = true
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            location?.let {
                                val latLng = LatLng(it.latitude, it.longitude)
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                                if (!viewModel.loadNearbyProperties(googleMap, latLng.latitude, latLng.longitude)) {
                                    handleNoNearbyProperties(context, googleMap, viewModel, zoomOutCount, maxZoomOut) {
                                        zoomOutCount++
                                    }
                                }
                            }
                        }
                    } else {
                        // You may use Accompanist Permissions or ActivityResultLauncher here
                        Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

private suspend fun searchForLocation(
    query: String,
    placesClient: PlacesClient,
    onLocationFound: (LatLng) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val predictionResponse = placesClient
            .findAutocompletePredictions(
                FindAutocompletePredictionsRequest.builder()
                    .setQuery(query)
                    .build()
            ).await()

        val prediction = predictionResponse.autocompletePredictions.firstOrNull()
        if (prediction != null) {
            val placeId = prediction.placeId
            val fetchPlaceRequest = FetchPlaceRequest.newInstance(
                placeId,
                listOf(Place.Field.LOCATION)
            )
            val placeResponse = placesClient.fetchPlace(fetchPlaceRequest).await()
            val latLng = placeResponse.place.location

            if (latLng != null) {
                onLocationFound(latLng)
            } else {
                onError("No coordinates found")
            }
        } else {
            onError("No results found")
        }
    } catch (e: Exception) {
        onError(e.message ?: "Unknown error")
    }
}



private fun handleNoNearbyProperties(
    context: Context,
    map: GoogleMap,
    viewModel: SearchViewModel,
    zoomOutCount: Int,
    maxZoomOut: Int,
    onZoomOut: () -> Unit
) {
    if (zoomOutCount < maxZoomOut) {
        onZoomOut()
        Toast.makeText(context, "No nearby properties", Toast.LENGTH_SHORT).show()
        map.animateCamera(CameraUpdateFactory.zoomOut())
        val defaultLoc = LatLng(-1.286389, 36.817223)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 10f))
        viewModel.loadNearbyProperties(map, defaultLoc.latitude, defaultLoc.longitude)
    } else {
        Toast.makeText(context, "Still no results after zooming out", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, mapView) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) = mapView.onResume()
            override fun onStart(owner: LifecycleOwner) = mapView.onStart()
            override fun onStop(owner: LifecycleOwner) = mapView.onStop()
            override fun onPause(owner: LifecycleOwner) = mapView.onPause()
            override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
    return mapView
}



