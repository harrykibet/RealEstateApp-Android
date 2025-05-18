package com.application.real_estate_app.feature_property.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.real_estate_app.core_design_system.component.SectionCard
import com.application.real_estate_app.core_design_system.component.StepNavigation
import com.application.real_estate_app.core_design_system.theme.ReaTheme
import com.application.real_estate_app.core_design_system.component.MockAvailabilityStatusForm
import com.application.real_estate_app.core_design_system.component.MockBasicDetailsForm
import com.application.real_estate_app.core_design_system.component.MockContactOwnershipForm
import com.application.real_estate_app.core_design_system.component.MockExtraDetailsForm
import com.application.real_estate_app.core_design_system.component.MockLocationInfoForm
import com.application.real_estate_app.core_design_system.component.MockMediaUploadsForm
import com.application.real_estate_app.core_design_system.component.ReaBackground

@Composable
fun PropertyFormScreen(isPreview: Boolean = false) {
    val sections = listOf(
        "Basic Details",
        "Location Info",
        "Media Uploads",
        "Contact & Ownership",
        "Extra Details",
        "Availability & Status"
    )

    var currentStep by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Add Property", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        SectionCard(title = sections[currentStep]) {
            when (currentStep) {
                0 -> if (isPreview) MockBasicDetailsForm() else BasicDetailsForm()
                1 -> if (isPreview) MockLocationInfoForm() else LocationInfoForm()
                2 -> if (isPreview) MockMediaUploadsForm() else MediaUploadsForm()
                3 -> if (isPreview) MockContactOwnershipForm() else ContactOwnershipForm()
                4 -> if (isPreview) MockExtraDetailsForm() else ExtraDetailsForm()
                5 -> if (isPreview) MockAvailabilityStatusForm() else AvailabilityStatusForm()
            }
        }

        Spacer(Modifier.height(24.dp))

        StepNavigation(
            currentStep = currentStep,
            totalSteps = sections.size,
            onPrevious = { if (currentStep > 0) currentStep-- },
            onNext = { if (currentStep < sections.lastIndex) currentStep++ }
        )
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
    ReaTheme {
        ReaBackground {
            PropertyFormScreen( isPreview = true)
        }
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
    ReaTheme {
        ReaBackground {
            PropertyFormScreen(isPreview = true)
        }
    }
}
