package com.application.real_estate_app.core_design_system.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StepNavigation(
    currentStep: Int,
    totalSteps: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        if (currentStep > 0) {
            TextButton(onClick = onPrevious) {
                Text("Back")
            }
        } else {
            Spacer(Modifier.width(64.dp)) // Keeps spacing when "Back" is hidden
        }

        if (currentStep < totalSteps - 1) {
            TextButton(onClick = onNext) {
                Text("Next")
            }
        } else {
            TextButton(onClick = { /* Submit form or navigate */ }) {
                Text("Submit")
            }
        }
    }
}
