package com.estatia.realestate.apps.core.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.model.property.ListingUiModel

@Composable
fun PropertyInfoOverlay(
    listing: ListingUiModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        EstatiaText(
            text = listing.title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            maxLines = 2
        )

        listing.description?.let {
            Spacer(modifier = Modifier.height(6.dp))
            EstatiaText(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                maxLines = 3
            )
        }
    }
}
