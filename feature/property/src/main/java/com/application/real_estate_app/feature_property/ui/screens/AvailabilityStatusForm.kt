package com.application.real_estate_app.feature_property.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.real_estate_app.core_design_system.component.RoundedElevatedTextField
import com.application.real_estate_app.core_design_system.theme.ReaTheme

@Composable
fun AvailabilityStatusForm() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Available")
            Spacer(Modifier.width(8.dp))
            Checkbox(
                checked = false,
                onCheckedChange = {}
            )
        }
        Spacer(Modifier.height(8.dp))

        RoundedElevatedTextField(
            value = "",
            onValueChange = {},
            label = "Deposit Amount",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(0.9f),
            singleLine = true
        )
        Spacer(Modifier.height(24.dp))

        RoundedElevatedTextField(
            value = "",
            onValueChange = {},
            label = "Lease Terms",
            modifier = Modifier.fillMaxWidth(0.9f),
            singleLine = false
        )
    }
}


@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 400
)

@Composable
fun AvailabilityStatusFormDarkPreview() {
    ReaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AvailabilityStatusForm()
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 400
)

@Composable
fun AvailabilityStatusFormLightPreview() {
    ReaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AvailabilityStatusForm()
        }
    }
}

