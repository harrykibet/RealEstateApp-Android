package com.application.real_estate_app.feature_property.ui.uploads.fragments

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.application.real_estate_app.feature_property.R
import com.application.real_estate_app.feature_property.databinding.FragmentMapSelectionBinding
import com.application.real_estate_app.feature_property.ui.uploads.viewModels.AddPropertyField
import com.application.real_estate_app.feature_property.ui.uploads.viewModels.AddPropertyViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapSelectionFragment : Fragment(R.layout.fragment_map_selection), OnMapReadyCallback {

    private var _binding: FragmentMapSelectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private val viewModel: AddPropertyViewModel by activityViewModels()
    private var selectedLocation: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO("Add Places API and a search bar")

        // Initialize Fused Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Initialize map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Handle getting current location
        binding.getCurrentLocationButton.setOnClickListener {
            getCurrentLocation()
        }

        // Handle confirming selected location
        binding.confirmLocationButton.setOnClickListener {
            confirmLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val defaultLocation = LatLng(-1.286389, 36.817223) // Nairobi
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))

        // Add draggable marker
        map.addMarker(MarkerOptions().position(defaultLocation).title("Select Location").draggable(true))

        // Handle marker drag
        map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDrag(marker: Marker) {}
            override fun onMarkerDragEnd(marker: Marker) {
                selectedLocation = marker.position
            }
        })
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    selectedLocation = latLng
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                    map.clear()
                    map.addMarker(MarkerOptions().position(latLng).title("Current Location").draggable(true))
                } else {
                    Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun confirmLocation() {
        if (selectedLocation != null) {
            viewModel.updateField(AddPropertyField.PropertyLocation, selectedLocation)
            // Navigate back to AddPropertyFragment
            findNavController().navigateUp()
        } else {
            Toast.makeText(requireContext(), "Please select a location", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
