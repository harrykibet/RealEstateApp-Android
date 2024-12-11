package com.application.real_estate_app.feature_explore.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.application.real_estate_app.feature_explore.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private var zoomOutCount = 0
    private val maxZoomOutAttempts = 1 // Limit to 1 zoom-out

    @Inject
    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize Places API
        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())

        //initialize FireStore and Location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        //set up the map
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this@MapsFragment)

        // Set up the search bar
        setupSearchBar(view)
    }

    private fun setupSearchBar(view: View) {
        val searchBar = view.findViewById<AutoCompleteTextView>(R.id.search_bar)

        // set up listener for user input
        searchBar.setOnEditorActionListener { _, _, _ ->
            val query = searchBar.text.toString()
            if (query.isNotEmpty()) {
                searchLocation(query)
            }
            false
        }
    }

    private fun searchLocation(query: String) {
        zoomOutCount = 0
        //TODO("Create an adapter to display autocomplete predictions")

        // Build the autocomplete predictions request
        val autocompleteRequest = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        // Perform the autocomplete predictions request
        placesClient.findAutocompletePredictions(autocompleteRequest)
            .addOnSuccessListener { response ->
                if (response.autocompletePredictions.isNotEmpty()) {
                    val prediction = response.autocompletePredictions.first()
                    val placeId = prediction.placeId

                    // Fetch the place details with the updated fields
                    val placeRequest = FetchPlaceRequest.newInstance(
                        placeId,
                        listOf(Place.Field.LOCATION) // Replace LAT_LNG with LOCATION
                    )

                    placesClient.fetchPlace(placeRequest)
                        .addOnSuccessListener { placeResponse ->
                            @Suppress("DEPRECATION")
                            val latLng = placeResponse.place.latLng //latlng is deprecated
                            if (latLng != null) {
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))

                                // Optionally load properties near this location
                                loadNearbyProperties(latLng.latitude, latLng.longitude)
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                requireContext(),
                                "Failed to fetch location details: ${exception.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error in searching location: ${exception.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: android.location.Location? ->
                location?.let {
                    val userLocation = LatLng(it.latitude, it.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14f))

                    // Retrieve nearby properties from firestore
                    loadNearbyProperties(it.latitude, it.longitude)
                }
            }
    }

    private fun loadNearbyProperties(userLat: Double, userLng: Double) {
        val nearbyDistanceThreshold = 10.0 // Distance in kilometers
        var propertiesFound = false
        //TODO("Create a property repository method for the below logic")

        db.collection("properties")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    result?.let {
                        for (document in it.documents) {
                            val propertyLat = document.getDouble("latitude") ?: 0.0
                            val propertyLng = document.getDouble("longitude") ?: 0.0
                            val propertyName = document.getString("title") ?: "Property"

                            val distanceToProperty =
                                calculateDistance(userLat, userLng, propertyLat, propertyLng)

                            if (distanceToProperty <= nearbyDistanceThreshold) {
                                propertiesFound = true
                                val propertyLocation = LatLng(propertyLat, propertyLng)
                                map.addMarker(
                                    MarkerOptions().position(propertyLocation).title(propertyName)
                                )
                            }
                        }
                        if (!propertiesFound) {
                            handleNoNearbyProperties()
                        }
                    }
                }
            }
    }

    private fun handleNoNearbyProperties() {
        if (zoomOutCount < maxZoomOutAttempts) {
            zoomOutCount++
            Toast.makeText(
                requireContext(),
                "No nearby properties found. Zooming out to show more properties",
                Toast.LENGTH_SHORT
            ).show()

            // Zoom out the map to show a wider area
        map.animateCamera(CameraUpdateFactory.zoomOut())

        // Load properties in a default popular location
        val defaultLocation = LatLng(-1.286389, 36.817223) // Nairobi, Kenya
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
        loadNearbyProperties(defaultLocation.latitude, defaultLocation.longitude)
        } else {
            Toast.makeText(
                requireContext(),
                "No nearby properties found after $maxZoomOutAttempts zoom-outs",Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371.0 // Earth's radius in kilometers
        val latDiff = Math.toRadians(lat2 - lat1)
        val lngDiff = Math.toRadians(lng2 - lng1)

        val a = sin(latDiff / 2) * sin(latDiff / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(lngDiff / 2) * sin(lngDiff / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}
