package com.estatia.realestate.apps.feature.search.ui.fragments

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
import androidx.fragment.app.viewModels
import com.estatia.realestate.apps.feature.search.R
import com.estatia.realestate.apps.feature.search.ui.viewmodels.SearchViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private var zoomOutCount = 0
    private val maxZoomOutAttempts = 1 // Limit to 1 zoom-out


    private val searchViewModel: SearchViewModel by viewModels()

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
                                if(!searchViewModel.loadNearbyProperties(map, latLng.latitude, latLng.longitude)){
                                    handleNoNearbyProperties()
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.fetch_location_error, exception.localizedMessage),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(requireContext(), R.string.location_not_found, Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    getString(R.string.search_location_error, exception.localizedMessage),
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
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
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

                    // Retrieve nearby properties
                    if(!searchViewModel.loadNearbyProperties(map, it.latitude, it.longitude)){
                        handleNoNearbyProperties()
                    }
                }
            }
    }



    private fun handleNoNearbyProperties() {
        if (zoomOutCount < maxZoomOutAttempts) {
            zoomOutCount++
            Toast.makeText(
                requireContext(),
                R.string.no_nearby_properties,
                Toast.LENGTH_SHORT
            ).show()

            // Zoom out the map to show a wider area
        map.animateCamera(CameraUpdateFactory.zoomOut())

        // Load properties in a default popular location
        val defaultLocation = LatLng(-1.286389, 36.817223) // Nairobi, Kenya
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
        searchViewModel.loadNearbyProperties(map, defaultLocation.latitude, defaultLocation.longitude)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_properties_after_zoom_out, maxZoomOutAttempts),Toast.LENGTH_SHORT).show()
        }
    }
}
