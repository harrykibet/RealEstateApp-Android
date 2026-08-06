package com.estatia.realestate.apps.feature.profile.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaOutlinedButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.localization.api.LocalNumberFormatter
import com.estatia.realestate.apps.core.localization.R as LocalizationR
import com.estatia.realestate.apps.feature.profile.ui.state.ProfileStats
import com.estatia.realestate.apps.feature.profile.ui.state.ProfileUiState
import com.estatia.realestate.apps.feature.profile.ui.viewmodels.ProfileViewModel

@Composable
internal fun ProfileRoute(
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(
        viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        uiState = uiState,
        onEditProfileClick = onEditProfileClick,
        onSettingsClick = onSettingsClick,
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val numberFormatter = LocalNumberFormatter.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(LocalizationR.string.feature_profile_tab_listings),
        stringResource(LocalizationR.string.feature_profile_tab_favorites),
        stringResource(LocalizationR.string.feature_profile_tab_reviews)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Image (Centered & Large)
            Image(
                painter = painterResource(com.estatia.realestate.apps.core.ui.R.drawable.ic_launcher_round), // TODO: Use uiState.profilePictureUrl
                contentDescription = stringResource(LocalizationR.string.feature_profile_picture),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // User Identity (Centered)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EstatiaText(
                        text = uiState.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    UserBadge(label = uiState.userType)
                }

                EstatiaText(
                    text = uiState.email,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(12.dp))

                EstatiaText(
                    text = uiState.bio,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Stats Row (Centered)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProfileStatItem(
                    label = stringResource(LocalizationR.string.feature_profile_stats_properties),
                    value = numberFormatter.formatCompactNumber(uiState.stats.propertyCount)
                )
                ProfileStatItem(
                    label = stringResource(LocalizationR.string.feature_profile_stats_followers),
                    value = numberFormatter.formatCompactNumber(uiState.stats.followerCount)
                )
                ProfileStatItem(
                    label = stringResource(LocalizationR.string.feature_profile_stats_following),
                    value = numberFormatter.formatCompactNumber(uiState.stats.followingCount)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons (Centered)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                EstatiaButton(
                    onClick = onEditProfileClick,
                    modifier = Modifier.weight(1f),
                ) {
                    EstatiaText(
                        text = stringResource(LocalizationR.string.feature_profile_button_edit),
                        fontSize = 14.sp
                    )
                }
                EstatiaOutlinedButton(
                    onClick = { /* TODO: Share */ },
                    modifier = Modifier.weight(1f),
                ) {
                    EstatiaText(
                        text = stringResource(LocalizationR.string.feature_profile_button_share),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                divider = {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                },
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            EstatiaText(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                    )
                }
            }

            // Tab Content Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp), // Fixed height for scrollable container demo
                contentAlignment = Alignment.Center,
            ) {
                EstatiaText(
                    text = stringResource(LocalizationR.string.feature_profile_empty_state, tabs[selectedTabIndex]),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = EstatiaIcons.MoreVert,
                contentDescription = stringResource(LocalizationR.string.feature_profile_settings_cd),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun ProfileStatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        EstatiaText(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        EstatiaText(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun UserBadge(label: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(4.dp),
    ) {
        EstatiaText(
            text = label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 400,
)
@Composable
fun ProfileScreenLightPreview() {
    EstatiaTheme {
        EstatiaBackground {
            ProfileScreen(
                uiState = ProfileUiState(
                    name = "Harry Kemboi",
                    email = "truman948@gmail.com",
                    bio = "Professional Real Estate Agent specializing in residential properties in Nairobi.",
                    userType = "Agent",
                    stats = ProfileStats(
                        propertyCount = 12,
                        followerCount = 1200,
                        followingCount = 450
                    )
                ),
                onEditProfileClick = {},
                onSettingsClick = {}
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
@Composable
fun ProfileScreenDarkPreview() {
    EstatiaTheme {
        EstatiaBackground {
            ProfileScreen(
                uiState = ProfileUiState(
                    name = "Harry Kemboi",
                    email = "truman948@gmail.com",
                    bio = "Professional Real Estate Agent specializing in residential properties in Nairobi.",
                    userType = "Agent",
                    stats = ProfileStats(
                        propertyCount = 5,
                        followerCount = 850,
                        followingCount = 210
                    )
                ),
                onEditProfileClick = {},
                onSettingsClick = {}
            )
        }
    }
}

@Preview(
    name = "Swahili Mode",
    showBackground = true,
    locale = "sw",
    widthDp = 400,
)
@Composable
fun ProfileScreenSwahiliPreview() {
    EstatiaTheme {
        EstatiaBackground {
            ProfileScreen(
                uiState = ProfileUiState(
                    name = "Harry Kemboi",
                    email = "truman948@gmail.com",
                    bio = "Wakala wa Mali isiyohamishika aliyebobea katika nyumba za makazi huko Nairobi.",
                    userType = "Wakala",
                    stats = ProfileStats(
                        propertyCount = 15,
                        followerCount = 2500,
                        followingCount = 300
                    )
                ),
                onEditProfileClick = {},
                onSettingsClick = {}
            )
        }
    }
}
