package com.application.real_estate_app.feature_property.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.real_estate_app.core_design_system.component.SectionCard
import com.application.real_estate_app.core_design_system.theme.RealEstateAppTheme
import com.application.real_estate_app.core_testing.composables.MockAvailabilityStatusForm
import com.application.real_estate_app.core_testing.composables.MockBasicDetailsForm
import com.application.real_estate_app.core_testing.composables.MockContactOwnershipForm
import com.application.real_estate_app.core_testing.composables.MockExtraDetailsForm
import com.application.real_estate_app.core_testing.composables.MockLocationInfoForm
import com.application.real_estate_app.core_testing.composables.MockMediaUploadsForm

@Composable
fun PropertyFormScreen(isPreview: Boolean = false) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Add Property", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        SectionCard(title = "Basic Details") {
            if (isPreview) MockBasicDetailsForm() else BasicDetailsForm()
        }

        SectionCard(title = "Location Info") {
            if (isPreview) MockLocationInfoForm() else LocationInfoForm()
        }

        SectionCard(title = "Media Uploads") {
            if (isPreview) MockMediaUploadsForm() else MediaUploadsForm()
        }

        SectionCard(title = "Contact & Ownership") {
            if (isPreview) MockContactOwnershipForm() else ContactOwnershipForm()
        }

        SectionCard(title = "Extra Details") {
            if (isPreview) MockExtraDetailsForm() else ExtraDetailsForm()
        }

        SectionCard(title = "Availability & Status") {
            if (isPreview) MockAvailabilityStatusForm() else AvailabilityStatusForm()
        }
    }
}

@Preview(
    name = "Property Form - Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 400
)
@Composable
fun PropertyFormPreviewLight() {
    RealEstateAppTheme(useDarkTheme = false) {
        PropertyFormScreen(isPreview = true)
    }
}

@Preview(
    name = "Property Form - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 400
)
@Composable
fun PropertyFormPreviewDark() {
    RealEstateAppTheme(useDarkTheme = true) {
        PropertyFormScreen(isPreview = true)
    }
}
