package com.application.real_estate_app.core_design_system.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MockBasicDetailsForm() {
    Column {
        RoundedElevatedTextField(
            value = "Nice Apartment",
            onValueChange = {},
            label = "Title",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        RoundedElevatedTextField(
            value = "Spacious and well-lit apartment in Nairobi.",
            onValueChange = {},
            label = "Description",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        RoundedElevatedTextField(
            value = "45000",
            onValueChange = {},
            label = "Price",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MockLocationInfoForm() {
    Column {
        RoundedElevatedTextField(
            value = "Nairobi",
            onValueChange = {},
            label = "County",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        RoundedElevatedTextField(
            value = "Westlands",
            onValueChange = {},
            label = "Address",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MockMediaUploadsForm() {
    Column {
        Text("Add photos and video links", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        RoundedElevatedTextField(
            value = "https://image1.jpg",
            onValueChange = {},
            label = "Image URL",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        RoundedElevatedTextField(
            value = "https://video1.mp4",
            onValueChange = {},
            label = "Video URL",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MockContactOwnershipForm() {
    Column {
        RoundedElevatedTextField(
            value = "Harry Kibet",
            onValueChange = {},
            label = "Owner Name",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        RoundedElevatedTextField(
            value = "+254712345678",
            onValueChange = {},
            label = "Phone",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        RoundedElevatedTextField(
            value = "owner@email.com",
            onValueChange = {},
            label = "Email",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MockExtraDetailsForm() {
    Column {
        RoundedElevatedTextField(
            value = "Swimming pool, Wi-Fi, Parking",
            onValueChange = {},
            label = "Amenities",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        RoundedElevatedTextField(
            value = "2 bedrooms, 2 bathrooms, 120 sqm",
            onValueChange = {},
            label = "Features",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MockAvailabilityStatusForm() {
    Column {
        RoundedElevatedTextField(
            value = "2025-06-01",
            onValueChange = {},
            label = "Available From",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        RoundedElevatedTextField(
            value = "12-month lease",
            onValueChange = {},
            label = "Lease Terms",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
