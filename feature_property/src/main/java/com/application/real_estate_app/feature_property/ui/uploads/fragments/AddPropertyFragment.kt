package com.application.real_estate_app.feature_property.ui.uploads.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.application.real_estate_app.feature_property.databinding.FragmentAddPropertyBinding
import com.application.real_estate_app.feature_property.ui.uploads.viewModels.AddPropertyViewModel
import com.application.real_estate_app.feature_property.R
import com.application.real_estate_app.feature_property.data.utils.PropertyData
import com.application.real_estate_app.feature_property.ui.uploads.viewModels.AddPropertyField
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddPropertyFragment : Fragment() {

    private lateinit var binding: FragmentAddPropertyBinding
    private val viewModel: AddPropertyViewModel by activityViewModels()

    @Inject lateinit var propertyData: PropertyData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Use ViewBinding to inflate the layout
        binding = FragmentAddPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupSpinners()

        // Observe ViewModel StateFlow values and manually update the UI
        viewLifecycleOwner.lifecycleScope.launch {
            // Ensure that we are only collecting data when the Fragment is in the STARTED state
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.title.collect { title ->
                        if (binding.titleField.text.toString() != title) {
                            binding.titleField.setText(title)
                        }
                    }
                }
                launch {
                    viewModel.description.collect { description ->
                        if (binding.descriptionField.text.toString() != description) {
                            binding.descriptionField.setText(description)
                        }
                    }
                }
                launch {
                    viewModel.price.collect { price ->
                        if (binding.priceField.text.toString() != price.toString()) {
                            binding.priceField.setText(price.toString())
                        }
                    }
                }
                launch {
                    viewModel.depositAmount.collect { depositAmount ->
                        if (binding.depositAmountField.text.toString() != depositAmount.toString()) {
                            binding.depositAmountField.setText(depositAmount.toString())
                        }
                    }
                }
                launch {
                    viewModel.bedrooms.collect { bedrooms ->
                        if (binding.bedroomsField.text.toString() != bedrooms.toString()) {
                            binding.bedroomsField.setText(bedrooms.toString())
                        }
                    }
                }
                launch {
                    viewModel.bathrooms.collect { bathrooms ->
                        if (binding.bathroomsField.text.toString() != bathrooms.toString()) {
                            binding.bathroomsField.setText(bathrooms.toString())
                        }
                    }
                }
                launch {
                    viewModel.areaSize.collect { areaSize ->
                        if (binding.areaSizeField.text.toString() != areaSize.toString()) {
                            binding.areaSizeField.setText(areaSize.toString())
                        }
                    }
                }
                launch {
                    viewModel.availableFrom.collect { availableFrom ->
                        if (binding.availableFromField.text.toString() != availableFrom) {
                            binding.availableFromField.setText(availableFrom)
                        }
                    }
                }
                launch {
                    viewModel.leaseTerms.collect { leaseTerms ->
                        if (binding.leaseTermsField.text.toString() != leaseTerms) {
                            binding.leaseTermsField.setText(leaseTerms)
                        }
                    }
                }
                launch {
                    viewModel.additionalFeatures.collect { additionalFeatures ->
                        if(binding.additionalFeaturesField.text.toString() != additionalFeatures) {
                            binding.additionalFeaturesField.setText(additionalFeatures)
                        }
                    }
                }
                launch {
                    viewModel.contactEmail.collect { contactEmail ->
                        if (binding.contactEmailField.text.toString() != contactEmail) {
                            binding.contactEmailField.setText(contactEmail)
                        }
                    }
                }
                launch {
                    viewModel.contactPhone.collect { contactPhone ->
                        if (binding.contactPhoneField.text.toString() != contactPhone) {
                            binding.contactPhoneField.setText(contactPhone)
                        }
                    }
                }
                launch {
                    viewModel.wifiChecked.collect { wifiChecked ->
                        if (binding.checkboxWifi.isChecked != wifiChecked) {
                            binding.checkboxWifi.isChecked = wifiChecked
                        }
                    }
                }
                launch {
                    viewModel.poolChecked.collect { poolChecked ->
                        if (binding.checkboxPool.isChecked != poolChecked) {
                            binding.checkboxPool.isChecked = poolChecked
                        }
                    }
                }
                launch {
                    viewModel.gymChecked.collect { gymChecked ->
                        if (binding.checkboxGym.isChecked != gymChecked) {
                            binding.checkboxGym.isChecked = gymChecked
                        }
                    }
                }
                launch {
                    viewModel.parkingChecked.collect { parkingChecked ->
                        if (binding.checkboxParking.isChecked != parkingChecked) {
                            binding.checkboxParking.isChecked = parkingChecked
                        }
                    }
                }
                launch {
                    viewModel.airConditioningChecked.collect { airConditioningChecked ->
                        if (binding.checkboxAirConditioning.isChecked != airConditioningChecked) {
                            binding.checkboxAirConditioning.isChecked = airConditioningChecked
                        }
                    }
                }
                launch {
                    viewModel.securityChecked.collect { securityChecked ->
                        if (binding.checkboxSecurity.isChecked != securityChecked) {
                            binding.checkboxSecurity.isChecked = securityChecked
                        }
                    }
                }
                launch {
                    viewModel.propertyType.collect { propertyType ->
                        if (binding.propertyTypeSpinner.selectedItem != propertyType) {
                            binding.propertyTypeSpinner.setSelection(
                                viewModel.propertyTypes.value.indexOf(propertyType)
                            )
                        }
                    }
                }
                launch {
                    viewModel.county.collect { county ->
                        if (binding.countySpinner.selectedItem != county) {
                            binding.countySpinner.setSelection(
                                viewModel.countyNames.value.indexOf(
                                    county
                                )
                            )
                        }
                    }
                }
                launch {
                    viewModel.isUpLoading.collect { isUpLoading ->
                        if (isUpLoading == true) {
                            showProgressBar()
                        }
                        else{
                            hideProgressBar()
                        }
                    }
                }
                launch {
                    viewModel.uploadingError.collect{ error ->
                        if (error != null) {
                                Toast.makeText(requireContext(), "Error uploading: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        // Set focus change listeners to update fields in the ViewModel
        binding.titleField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // Update ViewModel only when EditText loses focus
                val newTitle = binding.titleField.text.toString()
                if (newTitle != viewModel.title.value) {
                    viewModel.updateField(AddPropertyField.Title, newTitle)
                }
            }
        }

        binding.descriptionField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newDescription = binding.descriptionField.text.toString()
                if (newDescription != viewModel.description.value) {
                    viewModel.updateField(AddPropertyField.Description, newDescription)
                }
            }
        }

        binding.priceField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newPrice = binding.priceField.text.toString().toDoubleOrNull()
                if (newPrice != viewModel.price.value) {
                    viewModel.updateField(AddPropertyField.Price, newPrice)
                }
            }
        }

        binding.depositAmountField.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus) {
                val newDepositAmount = binding.depositAmountField.text.toString().toDoubleOrNull()
                if (newDepositAmount != viewModel.depositAmount.value) {
                    viewModel.updateField(AddPropertyField.DepositAmount, newDepositAmount)
                }
            }
        }

        binding.bedroomsField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newBedrooms = binding.bedroomsField.text.toString().toIntOrNull()
                if (newBedrooms != viewModel.bedrooms.value) {
                    viewModel.updateField(AddPropertyField.Bedrooms, newBedrooms)
                }
            }
        }

        binding.bathroomsField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newBathrooms = binding.bathroomsField.text.toString().toIntOrNull()
                if (newBathrooms != viewModel.bathrooms.value) {
                    viewModel.updateField(AddPropertyField.Bathrooms, newBathrooms)
                }
            }
        }

        binding.areaSizeField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newAreaSize = binding.areaSizeField.text.toString().toDoubleOrNull()
                if (newAreaSize != viewModel.areaSize.value) {
                    viewModel.updateField(AddPropertyField.AreaSize, newAreaSize)
                }
            }
        }

        binding.availableFromField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newAvailableFrom = binding.availableFromField.text.toString()
                if (newAvailableFrom != viewModel.availableFrom.value) {
                    viewModel.updateField(AddPropertyField.AvailableFrom, newAvailableFrom)
                }
            }
        }

        binding.leaseTermsField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newLeaseTerms = binding.leaseTermsField.text.toString()
                if (newLeaseTerms != viewModel.leaseTerms.value) {
                    viewModel.updateField(AddPropertyField.LeaseTerms, newLeaseTerms)
                }
            }
        }

        binding.additionalFeaturesField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newAdditionalFeatures = binding.additionalFeaturesField.text.toString()
                if (newAdditionalFeatures != viewModel.additionalFeatures.value) {
                    viewModel.updateField(AddPropertyField.AdditionalFeatures, newAdditionalFeatures)
                }
            }
        }

        binding.contactEmailField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newContactEmail = binding.contactEmailField.text.toString()
                if (newContactEmail != viewModel.contactEmail.value) {
                    viewModel.updateField(AddPropertyField.ContactEmail, newContactEmail)
                }
            }
        }

        binding.contactPhoneField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newContactPhone = binding.contactPhoneField.text.toString()
                if (newContactPhone != viewModel.contactPhone.value) {
                    viewModel.updateField(AddPropertyField.ContactPhone, newContactPhone)
                }
            }
        }

        // Set amenities checked state  listeners to update amenities
        binding.checkboxWifi.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                viewModel.updateField(AddPropertyField.WifiChecked, isChecked)
            }
        }

        binding.checkboxPool.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                viewModel.updateField(AddPropertyField.PoolChecked, isChecked)
            }
        }

        binding.checkboxGym.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                viewModel.updateField(AddPropertyField.GymChecked, isChecked)
            }

        }

        binding.checkboxParking.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                viewModel.updateField(AddPropertyField.ParkingChecked, isChecked)
            }
        }

        binding.checkboxAirConditioning.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                viewModel.updateField(AddPropertyField.AirConditioningChecked, isChecked)
            }
        }

        binding.checkboxSecurity.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                viewModel.updateField(AddPropertyField.SecurityChecked, isChecked)
            }
        }

        // Set item selected listeners for spinners
        binding.propertyTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                lifecycleScope.launch {
                    viewModel.updateField(AddPropertyField.PropertyType, selectedItem)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected, if necessary
            }
        }

        binding.countySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                lifecycleScope.launch {
                    viewModel.updateField(AddPropertyField.County, selectedItem)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected, if necessary
            }
        }
    }


    private fun setupSpinners() {
        // Initialize the property type spinner with data from PropertyData
        val propertyTypeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, // Default spinner item layout
            propertyData.propertyTypes // Use injected list
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Dropdown view layout
        }

        binding.propertyTypeSpinner.adapter = propertyTypeAdapter

        // Initialize the county spinner with data from PropertyData
        val countyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, // Default spinner item layout
            propertyData.counties // Use injected list
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Dropdown view layout
        }

        binding.countySpinner.adapter = countyAdapter
    }

    private fun setupListeners() {
        binding.addMediaButton.setOnClickListener {
            findNavController().navigate(R.id.action_addPropertyFragment_to_mediaSelectionFragment)
        }
        binding.selectLocationButton.setOnClickListener {
            findNavController().navigate(R.id.action_addPropertyFragment_to_mapSelectionFragment)
        }

        binding.savePropertyButton.setOnClickListener {
            if (isFormValid()) {
                showProgressBar()
                // Proceed with saving the property if all fields are valid
                viewModel.saveProperty()
            } else {
                // If the form is invalid, show appropriate error messages
                showValidationErrors()
            }
        }

        binding.clearFormButton.setOnClickListener{
            viewModel.clearData() // Clear the ViewModel's data
            //Reset the UI input fields
            binding.titleField.text?.clear()
            binding.descriptionField.text?.clear()
            binding.priceField.text?.clear()
            binding.depositAmountField.text?.clear()
            binding.bedroomsField.text?.clear()
            binding.bathroomsField.text?.clear()
            binding.areaSizeField.text?.clear()
            binding.availableFromField.text?.clear()
            binding.leaseTermsField.text?.clear()
            binding.additionalFeaturesField.text?.clear()
            binding.contactEmailField.text?.clear()
            binding.contactPhoneField.text?.clear()
            binding.checkboxWifi.isChecked = false
            binding.checkboxPool.isChecked = false
            binding.checkboxGym.isChecked = false
            binding.checkboxParking.isChecked = false
            binding.checkboxAirConditioning.isChecked = false
            binding.checkboxSecurity.isChecked = false
            binding.propertyTypeSpinner.setSelection(0)
            binding.countySpinner.setSelection(0)
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.savePropertyButton.isEnabled = false
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        binding.savePropertyButton.isEnabled = true
    }

    private fun isFormValid(): Boolean {
        return viewModel.title.value!!.isNotEmpty() &&
                viewModel.description.value!!.isNotEmpty() && //Add more fields to validate if necessary
                isPriceValid(viewModel.price.value!!)
    }

    private fun isPriceValid(price: Double): Boolean {
        // Check if the price is greater than 0 or a valid positive number
        return price > 0
    }

    private fun showValidationErrors() {
        var isValid = true

        if (viewModel.title.value!!.isEmpty()) {
            binding.titleField.error = "Title is required"
            isValid = false
        } else {
            binding.titleField.error = null
        }

        if (viewModel.description.value!!.isEmpty()) {
            binding.descriptionField.error = "Description is required"
            isValid = false
        } else {
            binding.descriptionField.error = null
        }

        if (!isPriceValid(viewModel.price.value!!)) {
            binding.priceField.error = "Price must be greater than 0"
            isValid = false
        } else {
            binding.priceField.error = null
        }

        if (!isValid) {
            Snackbar.make(requireView(), "Please fill out the required fields", Snackbar.LENGTH_SHORT).show()
        }
    }
}
