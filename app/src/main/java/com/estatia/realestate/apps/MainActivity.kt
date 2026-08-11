package com.estatia.realestate.apps

import android.os.Bundle
import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import androidx.tracing.trace
import com.estatia.realestate.apps.core.localization.api.LocalTimeZone
import com.estatia.realestate.apps.core.analytics.LocalAnalyticsHelper
import com.estatia.realestate.apps.ui.rememberEstatiaAppState
import com.estatia.realestate.apps.ui.EstatiaApp
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.localization.api.*
import com.estatia.realestate.apps.core.analytics.IAnalyticsHelper
import com.estatia.realestate.apps.core.domain.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import com.estatia.realestate.apps.core.player_engine.core.IPlayerManager
import com.estatia.realestate.apps.core.player_ui.core.LocalSurfacePool
import com.estatia.realestate.apps.core.player_ui.core.SurfacePool
import com.estatia.realestate.apps.util.isSystemInDarkTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var lazyStats: dagger.Lazy<JankStats>

    @Inject
    lateinit var networkStateProvider: INetworkStateProvider

    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    @Inject
    lateinit var currencyFormatter: CurrencyFormatter

    @Inject
    lateinit var numberFormatter: NumberFormatter

    @Inject
    lateinit var measurementFormatter: MeasurementFormatter

    @Inject
    lateinit var analyticsHelper: IAnalyticsHelper

    @Inject
    lateinit var authRepository: IAuthRepository

    @Inject
    lateinit var playerManager: IPlayerManager

    @Inject
    lateinit var surfacePool: SurfacePool

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Simply follow the system dark/light theme
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                isSystemInDarkTheme()
                    .distinctUntilChanged()
                    .collect { darkTheme ->
                        trace("EstatiaEdgeToEdge") {
                            enableEdgeToEdge(
                                statusBarStyle = SystemBarStyle.auto(
                                    lightScrim = android.graphics.Color.TRANSPARENT,
                                    darkScrim = android.graphics.Color.TRANSPARENT,
                                ) { darkTheme },
                                navigationBarStyle = SystemBarStyle.auto(
                                    lightScrim = lightScrim,
                                    darkScrim = darkScrim,
                                ) { darkTheme },
                            )
                        }
                    }
            }
        }

        // Ensure the uiState is collected so the flow starts emitting Success
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {}
            }
        }

        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.shouldKeepSplashScreen() }

        setContent {
            val appState = rememberEstatiaAppState(
                networkStateProvider = networkStateProvider,
                timeZoneMonitor = timeZoneMonitor,
                authRepository = authRepository,
            )

            val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()
            val isSystemDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
            val isInPiPMode by viewModel.isInPiPMode.collectAsStateWithLifecycle()

            CompositionLocalProvider(
                LocalAnalyticsHelper provides analyticsHelper,
                LocalTimeZone provides currentTimeZone,
                LocalCurrencyFormatter provides currencyFormatter,
                LocalNumberFormatter provides numberFormatter,
                LocalMeasurementFormatter provides measurementFormatter,
                LocalSurfacePool provides surfacePool,
            ) {
                EstatiaTheme(darkTheme = isSystemDarkTheme) {
                    EstatiaApp(
                        appState = appState,
                        isInPiPMode = isInPiPMode
                    )
                }
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        val supportsPiP = packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        if (supportsPiP && playerManager.isPlaying()) {
            enterPictureInPictureMode(PictureInPictureParams.Builder().build())
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        viewModel.updatePiPMode(isInPictureInPictureMode)
    }

    override fun onResume() {
        super.onResume()
        lazyStats.get().isTrackingEnabled = true
    }

    override fun onPause() {
        super.onPause()
        lazyStats.get().isTrackingEnabled = false
    }
}

private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)
