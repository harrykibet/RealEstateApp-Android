package com.estatia.realestate.apps.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme

/**
 * Estatia section card.
 * Professionalized with [EstatiaCardDefaults.CardCornerRadius] and a subtle border for a modern flat look.
 */
@Composable
fun EstatiaSectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(EstatiaCardDefaults.CardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            EstatiaText(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Preview(
    name = "SectionCard - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 400
)
@Composable
fun EstatiaSectionCardLightPreview() {
    EstatiaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(Modifier.padding(16.dp)) {
                EstatiaSectionCard(title = "Basic Details") {
                    EstatiaTextField(
                        value = "Luxury Villa",
                        onValueChange = {},
                        label = "Title"
                    )
                    Spacer(Modifier.height(16.dp))
                    EstatiaTextField(
                        value = "A beautiful sunset villa...",
                        onValueChange = {},
                        label = "Description"
                    )
                }
            }
        }
    }
}

@Preview(
    name = "SectionCard - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 400
)
@Composable
fun EstatiaSectionCardDarkPreview() {
    EstatiaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(Modifier.padding(16.dp)) {
                EstatiaSectionCard(title = "Basic Details") {
                    EstatiaTextField(
                        value = "Luxury Villa",
                        onValueChange = {},
                        label = "Title"
                    )
                    Spacer(Modifier.height(16.dp))
                    EstatiaTextField(
                        value = "A beautiful sunset villa...",
                        onValueChange = {},
                        label = "Description"
                    )
                }
            }
        }
    }
}

object EstatiaCardDefaults {
    val CardCornerRadius = 12.dp
}
