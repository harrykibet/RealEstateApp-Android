package com.application.real_estate_app.feature_property.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.real_estate_app.core_design_system.component.ReaBackground
import com.application.real_estate_app.core_design_system.component.RoundedElevatedTextField
import com.application.real_estate_app.core_design_system.theme.ReaTheme

@Composable
fun MediaUploadsForm() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RoundedElevatedTextField(
            value = "",
            onValueChange = {},
            label = "Image URLs (comma separated)",
            modifier = Modifier.fillMaxWidth(0.9f),
            singleLine = false
        )
        Spacer(Modifier.height(24.dp))

        RoundedElevatedTextField(
            value = "",
            onValueChange = {},
            label = "Video URLs (comma separated)",
            modifier = Modifier.fillMaxWidth(0.9f),
            singleLine = false
        )
        Spacer(Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Videos Available")
            Spacer(Modifier.width(8.dp))
            Checkbox(
                checked = false,
                onCheckedChange = {}
            )
        }
    }
}


@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 400
)

@Composable
fun MediaUploadsFormDarkPreview() {
    ReaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MediaUploadsForm()
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
fun MediaUploadsFormLightPreview() {
    ReaTheme {
        ReaBackground {
            MediaUploadsForm()
        }
    }
}

