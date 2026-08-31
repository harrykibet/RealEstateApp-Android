package com.estatia.realestate.apps.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstatiaTopAppBar(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int? = null,
    navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String? = null,
    actionIcon: ImageVector? = null,
    actionIconContentDescription: String? = null,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            titleRes?.let {
                EstatiaText(text = stringResource(id = it))
            }
        },
        navigationIcon = {
            if (navigationIcon != null && navigationIconContentDescription != null) {
                IconButton(
                    onClick = onNavigationClick,
                    modifier = Modifier.testTag("EstatiaTopAppBarNavIcon")
                ) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        actions = {
            if (actionIcon != null && actionIconContentDescription != null) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = actionIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        colors = colors,
        modifier = modifier.testTag("EstatiaTopAppBar"),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Top App Bar")
@Composable
private fun EstatiaTopAppBarPreview() {
    EstatiaTheme {
        EstatiaTopAppBar(
            titleRes = android.R.string.untitled,
            navigationIcon = EstatiaIcons.Search,
            navigationIconContentDescription = "Navigation icon",
            actionIcon = EstatiaIcons.MoreVert,
            actionIconContentDescription = "Action icon",
        )
    }
}
