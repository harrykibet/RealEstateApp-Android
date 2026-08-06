package com.estatia.realestate.apps.core.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.model.property.ListingUiModel
import com.estatia.realestate.apps.core.localization.api.LocalCurrencyFormatter

@Composable
fun PropertyInfoOverlay(
    listing: ListingUiModel,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = LocalCurrencyFormatter.current
    val formattedPrice = listing.price?.let {
        currencyFormatter.formatCurrency(it, "KES") // TODO: Get currency from Region
    } ?: "Price on request"

    val textShadow = Shadow(
        color = Color.Black.copy(alpha = 0.5f),
        offset = Offset(2f, 2f),
        blurRadius = 4f
    )

    Column(modifier = modifier) {

        // Price
        EstatiaText(
            text = formattedPrice,
            style = MaterialTheme.typography.headlineSmall.copy(
                shadow = textShadow,
                fontWeight = FontWeight.ExtraBold
            ),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        // User Handle
        EstatiaText(
            text = "@${listing.ownerName}",
            style = MaterialTheme.typography.bodyLarge.copy(
                shadow = textShadow,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Property Title
        EstatiaText(
            text = listing.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                shadow = textShadow,
                fontWeight = FontWeight.SemiBold
            ),
            color = Color.White,
            maxLines = 2
        )

        // Description
        listing.description?.let {
            Spacer(modifier = Modifier.height(4.dp))
            EstatiaText(
                text = it,
                style = MaterialTheme.typography.bodyMedium.copy(
                    shadow = textShadow
                ),
                color = Color.White,
                maxLines = 3,
                lineHeight = 18.sp
            )
        }
    }
}
