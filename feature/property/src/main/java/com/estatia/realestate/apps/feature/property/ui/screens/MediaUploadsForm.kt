package com.estatia.realestate.apps.feature.property.ui.screens

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
import com.estatia.realestate.apps.core.designsystem.component.ReaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaTextField
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme

@Composable
fun MediaUploadsForm() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EstatiaTextField(
            value = "",
            onValueChange = {},
            label = "Image URLs (comma separated)",
            modifier = Modifier.fillMaxWidth(0.9f),
            singleLine = false
        )
        Spacer(Modifier.height(24.dp))

        EstatiaTextField(
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
    EstatiaTheme {
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
    EstatiaTheme {
        ReaBackground {
            MediaUploadsForm()
        }
    }
}

