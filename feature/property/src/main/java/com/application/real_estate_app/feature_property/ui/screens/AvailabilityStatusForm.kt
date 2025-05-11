package com.application.real_estate_app.feature_property.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AvailabilityStatusForm() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Available")
            Spacer(Modifier.width(8.dp))
            Checkbox(
                checked = false,
                onCheckedChange = {}
            )
        }
        Spacer(Modifier.height(8.dp))

        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Deposit Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Lease Terms") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )
    }
}
