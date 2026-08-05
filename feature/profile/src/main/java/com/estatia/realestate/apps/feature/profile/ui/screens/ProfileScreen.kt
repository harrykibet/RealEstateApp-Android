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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaOutlinedButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.ui.R

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    profileImage: Painter = painterResource(R.drawable.ic_launcher_round),
    name: String = "Harry Kemboi",
    email: String = "truman948@gmail.com",
    stats: ProfileStats = ProfileStats(),
    onEditProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("My Listings", "Favorites", "Reviews")

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
                painter = profileImage,
                contentDescription = stringResource(R.string.profile_picture),
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
                        text = name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    UserBadge(label = "Agent")
                }

                EstatiaText(
                    text = email,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(12.dp))

                EstatiaText(
                    text = "Professional Real Estate Agent specializing in residential properties in Nairobi. Helping you find your dream home with ease.",
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
                ProfileStatItem(label = "Properties", value = formatStatCount(stats.propertyCount))
                ProfileStatItem(label = "Followers", value = formatStatCount(stats.followerCount))
                ProfileStatItem(label = "Following", value = formatStatCount(stats.followingCount))
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
                    EstatiaText(text = "Edit Profile", fontSize = 14.sp)
                }
                EstatiaOutlinedButton(
                    onClick = { /* TODO: Share */ },
                    modifier = Modifier.weight(1f),
                ) {
                    EstatiaText(text = "Share Profile", fontSize = 14.sp)
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
                    text = "No ${tabs[selectedTabIndex]} yet",
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
                contentDescription = "Settings",
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
                profileImage = painterResource(id = R.drawable.ic_launcher_round),
                name = "Harry Kemboi",
                email = "truman948@gmail.com",
                stats = ProfileStats(
                    propertyCount = 12,
                    followerCount = 1200,
                    followingCount = 450
                )
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
                profileImage = painterResource(id = R.drawable.ic_launcher_round),
                name = "Harry Kemboi",
                email = "truman948@gmail.com",
                stats = ProfileStats(
                    propertyCount = 5,
                    followerCount = 850,
                    followingCount = 210
                )
            )
        }
    }
}

/**
 * Data class to model profile statistics.
 */
data class ProfileStats(
    val propertyCount: Int = 0,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
)

/**
 * Formats a count into a social-media style string (e.g., 1.2k, 1.5M).
 */
private fun formatStatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format(Locale.US, "%.1fM", count / 1_000_000f).replace(".0", "")
        count >= 1_000 -> String.format(Locale.US, "%.1fk", count / 1_000f).replace(".0", "")
        else -> count.toString()
    }
}
