package com.estatia.realestate.apps.core.designsystem.component


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme


/**
 * Estatia tab. Wraps Material 3 [Tab] and shifts text label down.
 *
 * @param selected Whether this tab is selected or not.
 * @param onClick The callback to be invoked when this tab is selected.
 * @param modifier Modifier to be applied to the tab.
 * @param enabled Controls the enabled state of the tab. When `false`, this tab will not be
 * clickable and will appear disabled to accessibility services.
 * @param text The text label content.
 */
@Composable
fun EstatiaTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
) {
    Tab(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        text = {
            val style = MaterialTheme.typography.labelLarge.copy(textAlign = TextAlign.Center)
            ProvideTextStyle(
                value = style,
                content = {
                    Box(modifier = Modifier.padding(top = EstatiaTabDefaults.TabTopPadding)) {
                        text()
                    }
                },
            )
        },
    )
}

/**
 * Estatia tab row. Wraps Material 3 [TabRow].
 *
 * @param selectedTabIndex The index of the currently selected tab.
 * @param modifier Modifier to be applied to the tab row.
 * @param tabs The tabs inside this tab row. Typically, this will be multiple [EstatiaTab]s. Each element
 * inside this lambda will be measured and placed evenly across the row, each taking up equal space.
 */

@Composable
fun EstatiaTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    tabs: @Composable () -> Unit,
) {
    SecondaryTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(
                    selectedTabIndex,
                    matchContentSize = false
                ),
                height = 2.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        divider = {
            HorizontalDivider()
        }
    ) {
        tabs()
    }
}

@ThemePreviews
@Composable
fun TabsPreview() {
    EstatiaTheme {
        val titles = listOf("home", "profile")
        EstatiaTabRow(selectedTabIndex = 0) {
            titles.forEachIndexed { index, title ->
                EstatiaTab(
                    selected = index == 0,
                    onClick = { },
                    text = { EstatiaText(text = title) },
                )
            }
        }
    }
}

object EstatiaTabDefaults {
    val TabTopPadding = 7.dp
}
