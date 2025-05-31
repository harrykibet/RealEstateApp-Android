package com.estatia.realestate.apps.feature.property.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaTextField
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme

@Composable
fun BasicDetailsForm() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter a short, clear title for the property (e.g., '2 Bedroom Apartment in Kilimani')",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(0.9f)
        )
        EstatiaTextField(
            value = "",
            onValueChange = {},
            label = "Title",
            modifier = Modifier.fillMaxWidth(0.9f)
        )
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Provide a detailed description (include amenities, location benefits, etc.)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(0.9f)
        )
        EstatiaTextField(
            value = "",
            onValueChange = {},
            label = "Description",
            modifier = Modifier.fillMaxWidth(0.9f),
            singleLine = false
        )
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Enter the price in KES (e.g., 20000). Avoid adding currency symbols.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(0.9f)
        )
        EstatiaTextField(
            value = "",
            onValueChange = {},
            label = "Price",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(0.9f)
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
fun BasicDetailsFormDarkPreview() {
    EstatiaTheme {
        EstatiaBackground {
            BasicDetailsForm()
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
fun BasicDetailsFormLightPreview() {
    EstatiaTheme {
        EstatiaBackground {
            BasicDetailsForm()
        }
    }
}
