package com.application.real_estate_app.feature_property.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.real_estate_app.core_design_system.component.RoundedElevatedTextField
import com.application.real_estate_app.core_design_system.theme.ReaTheme

@Composable
fun LocationInfoForm() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RoundedElevatedTextField(
            value = "",
            onValueChange = {},
            label = "Latitude",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(0.9f),
            singleLine = true
        )
        Spacer(Modifier.height(24.dp))

        RoundedElevatedTextField(
            value = "",
            onValueChange = {},
            label = "Longitude",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(0.9f),
            singleLine = true
        )
        Spacer(Modifier.height(24.dp))

        RoundedElevatedTextField(
            value = "",
            onValueChange = {},
            label = "County",
            modifier = Modifier.fillMaxWidth(0.9f),
            singleLine = true
        )
        Spacer(Modifier.height(24.dp))

        RoundedElevatedTextField(
            value = "",
            onValueChange = {},
            label = "Address",
            modifier = Modifier.fillMaxWidth(0.9f),
            singleLine = true
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
fun LocationInfoFormDarkPreview() {
    ReaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LocationInfoForm()
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
fun LocationInfoFormLightPreview() {
    ReaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LocationInfoForm()
        }
    }
}

