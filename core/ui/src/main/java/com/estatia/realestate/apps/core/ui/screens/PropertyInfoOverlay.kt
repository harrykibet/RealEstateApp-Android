package com.estatia.realestate.apps.core.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel

@Composable
fun PropertyInfoOverlay(
    property: PropertyDomainModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        Text(
            text = property.title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            maxLines = 2
        )

        property.description?.let {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                maxLines = 3
            )
        }
    }
}