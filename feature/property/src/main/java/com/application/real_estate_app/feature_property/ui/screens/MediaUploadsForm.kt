package com.application.real_estate_app.feature_property.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MediaUploadsForm() {
    Column {
        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Image URLs (comma separated)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )
        Spacer(Modifier.height(8.dp))

        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Video URLs (comma separated)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )
        Spacer(Modifier.height(8.dp))

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
