package com.estatia.realestate.apps.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.ui.DevicePreviews
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.estatia.realestate.apps.navigation.EstatiaNavHost
import com.estatia.realestate.apps.navigation.TopLevelDestination
import com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons
import com.estatia.realestate.apps.core.designsystem.theme.GradientColors
import com.estatia.realestate.apps.core.designsystem.theme.LocalGradientColors
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaGradientBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.designsystem.component.EstatiaTopAppBar
import com.estatia.realestate.apps.core.designsystem.component.EstatiaNavigationSuiteScaffold
import kotlin.reflect.KClass
import com.estatia.realestate.apps.feature.settings.R as settingsR
import com.estatia.realestate.apps.R

@Composable
fun EstatiaApp(
    appState: EstatiaAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    val unreadDestinations by appState.topLevelDestinationsWithUnreadResources
        .collectAsStateWithLifecycle()
    val isUserAuthenticated by appState.isUserAuthenticated.collectAsStateWithLifecycle()
    val currentDestination = appState.currentDestination

    EstatiaAppContent(
        isOffline = isOffline,
        unreadDestinations = unreadDestinations,
        isUserAuthenticated = isUserAuthenticated,
        currentDestination = currentDestination,
        currentTopLevelDestination = appState.currentTopLevelDestination,
        windowAdaptiveInfo = windowAdaptiveInfo,
        onNavigateToTopLevelDestination = appState::navigateToTopLevelDestination,
        onNavigateToSearch = appState::navigateToSearch,
        modifier = modifier,
    ) {
        EstatiaNavHost(
            appState = appState,
            isUserAuthenticated = isUserAuthenticated,
        )
    }
}

@Composable
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
)
internal fun EstatiaAppContent(
    isOffline: Boolean,
    unreadDestinations: Set<TopLevelDestination>,
    isUserAuthenticated: Boolean?,
    currentDestination: NavDestination?,
    currentTopLevelDestination: TopLevelDestination?,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    onNavigateToTopLevelDestination: (TopLevelDestination) -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shouldShowGradientBackground =
        currentTopLevelDestination == TopLevelDestination.HOME

    EstatiaBackground(modifier = modifier) {
        EstatiaGradientBackground(
            gradientColors = if (shouldShowGradientBackground) {
                LocalGradientColors.current
            } else {
                GradientColors()
            },
        ) {
            val snackbarHostState = remember { SnackbarHostState() }

            // If user is not connected to the internet show a snack bar to inform them.
            val notConnectedMessage = stringResource(R.string.not_connected)
            LaunchedEffect(isOffline) {
                if (isOffline) {
                    snackbarHostState.showSnackbar(
                        message = notConnectedMessage,
                        duration = Indefinite,
                    )
                }
            }

            // If auth state is loading, don't show anything (Splash screen is likely still visible)
            if (isUserAuthenticated == null) return@EstatiaGradientBackground

            val notificationDotColor = MaterialTheme.colorScheme.tertiary

            EstatiaNavigationSuiteScaffold(
                navigationSuiteItems = {
                    if (isUserAuthenticated) {
                        TopLevelDestination.entries.forEach { destination ->
                            val hasUnread = unreadDestinations.contains(destination)
                            val selected = currentDestination
                                .isRouteInHierarchy(destination.baseRoute)
                            item(
                                selected = selected,
                                onClick = { onNavigateToTopLevelDestination(destination) },
                                icon = destination.unselectedIcon,
                                selectedIcon = destination.selectedIcon,
                                label = { EstatiaText(stringResource(destination.iconTextId)) },
                                modifier =
                                Modifier
                                    .testTag("EstatiaNavItem")
                                    .then(
                                        if (hasUnread) {
                                            Modifier.notificationDot(notificationDotColor)
                                        } else {
                                            Modifier
                                        },
                                    ),
                            )
                        }
                    }
                },
                windowAdaptiveInfo = windowAdaptiveInfo,
                layoutType = if (isUserAuthenticated) {
                    NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)
                } else {
                    NavigationSuiteType.None
                },
            ) {
                Scaffold(
                    modifier = Modifier.semantics {
                        testTagsAsResourceId = true
                    },
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    snackbarHost = {
                        SnackbarHost(
                            snackbarHostState,
                            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
                        )
                    },
                ) { padding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                            .consumeWindowInsets(padding),
                    ) {
                        content()

                        if (currentTopLevelDestination != null && currentTopLevelDestination.showSearch) {
                            EstatiaTopAppBar(
                                navigationIcon = EstatiaIcons.Search,
                                navigationIconContentDescription = stringResource(
                                    id = settingsR.string.feature_settings_top_app_bar_navigation_icon_description,
                                ),
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Transparent,
                                ),
                                onNavigationClick = onNavigateToSearch,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun Modifier.notificationDot(color: Color): Modifier =
    drawWithContent {
        drawContent()
        drawCircle(
            color,
            radius = 5.dp.toPx(),
            // This is based on the dimensions of the NavigationBar's "indicator pill";
            // however, its parameters are private, so we must depend on them implicitly
            // (NavigationBarTokens.ActiveIndicatorWidth = 64.dp)
            center = center + Offset(
                64.dp.toPx() * .45f,
                (32.dp.toPx() * -.45f) - 6.dp.toPx(),
            ),
        )
    }

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false

@DevicePreviews
@Composable
fun EstatiaAppPreviewAuthenticated() {
    EstatiaTheme {
        EstatiaAppContent(
            isOffline = false,
            unreadDestinations = setOf(TopLevelDestination.HOME),
            isUserAuthenticated = true,
            currentDestination = null,
            currentTopLevelDestination = TopLevelDestination.HOME,
            windowAdaptiveInfo = currentWindowAdaptiveInfo(),
            onNavigateToTopLevelDestination = {},
            onNavigateToSearch = {},
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                EstatiaText("Authenticated Content")
            }
        }
    }
}

@DevicePreviews
@Composable
fun EstatiaAppPreviewUnauthenticated() {
    EstatiaTheme {
        EstatiaAppContent(
            isOffline = false,
            unreadDestinations = emptySet(),
            isUserAuthenticated = false,
            currentDestination = null,
            currentTopLevelDestination = null,
            windowAdaptiveInfo = currentWindowAdaptiveInfo(),
            onNavigateToTopLevelDestination = {},
            onNavigateToSearch = {},
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                EstatiaText("Unauthenticated Content (Login)")
            }
        }
    }
}

@Preview
@Composable
fun EstatiaAppPreviewOffline() {
    EstatiaTheme {
        EstatiaAppContent(
            isOffline = true,
            unreadDestinations = emptySet(),
            isUserAuthenticated = true,
            currentDestination = null,
            currentTopLevelDestination = TopLevelDestination.HOME,
            windowAdaptiveInfo = currentWindowAdaptiveInfo(),
            onNavigateToTopLevelDestination = {},
            onNavigateToSearch = {},
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                EstatiaText("Offline Content")
            }
        }
    }
}
