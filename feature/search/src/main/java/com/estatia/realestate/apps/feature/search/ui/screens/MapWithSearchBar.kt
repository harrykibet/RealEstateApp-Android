package com.estatia.realestate.apps.feature.search.ui.screens

import android.Manifest
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
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.google.android.libraries.places.api.Places
import com.google.android.gms.location.LocationServices
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun MapWithSearchBar(
    viewModel: SearchViewModel = hiltViewModel(
        viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    ),
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val mapView = rememberMapViewWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    var map by remember { mutableStateOf<GoogleMap?>(null) }

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
            placeholder = { EstatiaText("Search location") },
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
                                viewModel.loadNearbyProperties(
                                    latLng.latitude,
                                    latLng.longitude,
                                    10.0,
                                )
                            },
                        ) {
                            Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
            ),
        )

        // MapView
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
        ) {
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
                            viewModel.loadNearbyProperties(latLng.latitude, latLng.longitude, 10.0)
                        }
                    }
                } else {
                    // You may use Accompanist Permissions or ActivityResultLauncher here
                    Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

private suspend fun searchForLocation(
    query: String,
    placesClient: PlacesClient,
    onLocationFound: (LatLng) -> Unit,
    onError: (String) -> Unit,
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



