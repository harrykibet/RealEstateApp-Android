package com.estatia.realestate.apps.feature.settings

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.ui.DevicePreviews
import com.estatia.realestate.apps.core.analytics.TrackScreenViewEvent
import com.estatia.realestate.apps.core.analytics.LocalAnalyticsHelper

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(
        viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    ),
) {
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()

    SettingsScreen(
        settingsUiState = settingsUiState,
        onBackClick = onBackClick,
        onLogoutClick = onLogoutClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    settingsUiState: SettingsUiState,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    EstatiaText(
                        text = stringResource(R.string.feature_settings_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            when (settingsUiState) {
                SettingsUiState.Loading -> {
                    EstatiaText(
                        text = stringResource(R.string.feature_settings_loading),
                        modifier = Modifier.padding(16.dp),
                    )
                }

                is SettingsUiState.Success -> {
                    // Content reserved for future non-theme settings
                }
            }

            SettingsSectionTitle(text = "Support")
            SettingsNavigationRow(
                text = stringResource(R.string.feature_settings_privacy_policy),
                onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) },
            )
            SettingsNavigationRow(
                text = stringResource(R.string.feature_settings_brand_guidelines),
                onClick = { uriHandler.openUri(BRAND_GUIDELINES_URL) },
            )
            SettingsNavigationRow(
                text = stringResource(R.string.feature_settings_feedback),
                onClick = { uriHandler.openUri(FEEDBACK_URL) },
            )

            SettingsSectionTitle(text = "Account")
            SettingsNavigationRow(
                text = stringResource(R.string.feature_settings_logout),
                onClick = onLogoutClick,
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    TrackScreenViewEvent(
        screenName = "Settings",
        analyticsHelper = LocalAnalyticsHelper.current
    )
}

@Composable
private fun SettingsSectionTitle(text: String) {
    EstatiaText(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun SettingsNavigationRow(
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EstatiaText(text = text, modifier = Modifier.weight(1f), fontSize = 16.sp)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 400,
)
@DevicePreviews
@Composable
fun SettingsScreenLightPreview() {
    EstatiaTheme {
        EstatiaBackground {
            SettingsScreen(
                settingsUiState = SettingsUiState.Success(UserEditableSettings()),
                onBackClick = {},
                onLogoutClick = {},
            )
        }
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 400,
)
@DevicePreviews
@Composable
fun SettingsScreenDarkPreview() {
    EstatiaTheme {
        EstatiaBackground {
            SettingsScreen(
                settingsUiState = SettingsUiState.Success(UserEditableSettings()),
                onBackClick = {},
                onLogoutClick = {},
            )
        }
    }
}

private const val PRIVACY_POLICY_URL = "https://policies.google.com/privacy"
private const val BRAND_GUIDELINES_URL = "https://developer.android.com/distribute/marketing-tools/brand-guidelines"
private const val FEEDBACK_URL = "https://goo.gle/nia-app-feedback"
