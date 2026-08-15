package com.estatia.realestate.apps.feature.property.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.estatia.realestate.apps.core.designsystem.component.DynamicAsyncImage
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.testing.data.MockProperties
import com.estatia.realestate.apps.core.ui.DevicePreviews
import com.estatia.realestate.apps.core.localization.api.LocalCurrencyFormatter
import com.estatia.realestate.apps.core.localization.api.LocalMeasurementFormatter
import com.estatia.realestate.apps.core.localization.R as LocalizationR
import com.estatia.realestate.apps.feature.property.ui.management.viewmodels.PropertyDetailsUiState
import com.estatia.realestate.apps.feature.property.ui.management.viewmodels.PropertyDetailsViewModel
import com.estatia.realestate.apps.feature.property.ui.management.viewmodels.PropertyDetailsVideoPlaybackViewModel
import com.estatia.realestate.apps.core.player_ui.screens.PlaybackErrorView
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState
import com.estatia.realestate.apps.core.player_ui.screens.EngineVideoPlayer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.estatia.realestate.apps.core.model.property.MediaType
import androidx.media3.common.Player

@Composable
fun PropertyDetailsRoute(
    propertyId: String,
    onBackClick: () -> Unit,
    viewModel: PropertyDetailsViewModel = hiltViewModel(),
    playbackViewModel: PropertyDetailsVideoPlaybackViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val playbackUiState by playbackViewModel.uiState.collectAsStateWithLifecycle()
    val isMuted by playbackViewModel.isMuted.collectAsStateWithLifecycle()

    LaunchedEffect(propertyId) {
        viewModel.loadProperty(propertyId)
    }

    PropertyDetailsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        playbackUiState = playbackUiState,
        isMuted = isMuted,
        onMuteToggle = playbackViewModel::toggleMute,
        onPlaybackRetry = playbackViewModel::retry,
        getPlayer = { id, uri, type, score -> playbackViewModel.getPlayer(id, uri, type, score) },
        onPausePlayback = playbackViewModel::pause,
        isMediaActive = playbackViewModel::isMediaActive
    )
}

@Composable
fun PropertyDetailsScreen(
    uiState: PropertyDetailsUiState,
    onBackClick: () -> Unit,
    playbackUiState: PlayerUiState,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    onPlaybackRetry: () -> Unit,
    getPlayer: suspend (String, Uri, MediaType, Float) -> Player,
    onPausePlayback: () -> Unit,
    isMediaActive: (String) -> Boolean,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (uiState) {
                PropertyDetailsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is PropertyDetailsUiState.Success -> {
                    PropertyDetailsContent(
                        property = uiState.property,
                        onBackClick = onBackClick,
                        playbackUiState = playbackUiState,
                        isMuted = isMuted,
                        onMuteToggle = onMuteToggle,
                        onPlaybackRetry = onPlaybackRetry,
                        getPlayer = getPlayer,
                        onPausePlayback = onPausePlayback,
                        isMediaActive = isMediaActive
                    )
                }

                is PropertyDetailsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EstatiaText(text = uiState.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertyDetailsContent(
    property: PropertyDomainModel,
    onBackClick: () -> Unit,
    playbackUiState: PlayerUiState,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    onPlaybackRetry: () -> Unit,
    getPlayer: suspend (String, Uri, MediaType, Float) -> Player,
    onPausePlayback: () -> Unit,
    isMediaActive: (String) -> Boolean,
) {
    val scrollState = rememberScrollState()
    val images = property.imageUrls.filter { it.isNotBlank() }
    val videos = property.directVideoUrls.filter { it.isNotBlank() }
    val allMedia = images + videos
    val pagerState = rememberPagerState { allMedia.size }
    val currencyFormatter = LocalCurrencyFormatter.current
    val measurementFormatter = LocalMeasurementFormatter.current

    val formattedPrice = property.price?.let {
        currencyFormatter.formatCurrency(it.amount, "KES") // TODO: Get from Region
    } ?: stringResource(LocalizationR.string.feature_property_details_price_on_request)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Media Gallery
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                if (allMedia.isNotEmpty()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val mediaUrl = allMedia[page]
                        val isVideo = mediaUrl in videos

                        if (isVideo) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                val videoUri = (property.hlsUrls.getOrNull(videos.indexOf(mediaUrl)) ?: mediaUrl).toUri()
                                EngineVideoPlayer(
                                    mediaId = property.id.value,
                                    uri = videoUri,
                                    mediaType = MediaType.VOD,
                                    matchScore = property.matchScore,
                                    getPlayer = getPlayer,
                                    onPause = onPausePlayback,
                                    isActive = isMediaActive(property.id.value),
                                    isMuted = isMuted,
                                    onMuteToggle = onMuteToggle,
                                    modifier = Modifier.fillMaxSize()
                                )

                                if (playbackUiState is PlayerUiState.Error) {
                                    PlaybackErrorView(
                                        errorState = playbackUiState,
                                        onRetry = onPlaybackRetry,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.7f))
                                    )
                                }
                            }
                        } else {
                            DynamicAsyncImage(
                                imageUrl = mediaUrl,
                                contentDescription = stringResource(LocalizationR.string.feature_property_details_gallery_cd),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Page Indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        EstatiaText(
                            text = "${pagerState.currentPage + 1}/${allMedia.size}",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        EstatiaText(stringResource(LocalizationR.string.feature_property_details_no_media))
                    }
                }
            }

            // Property Details
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        EstatiaText(
                            text = property.title.ifBlank { stringResource(LocalizationR.string.feature_home_no_properties_found) }, // Using existing fallback
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            EstatiaText(
                                text = property.address ?: property.county ?: stringResource(LocalizationR.string.feature_property_details_location_unknown),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    EstatiaText(
                        text = formattedPrice,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(24.dp))

                // Stats Section
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatItem(
                        label = stringResource(LocalizationR.string.feature_property_details_label_bedrooms),
                        value = property.bedrooms?.toString() ?: "-"
                    )
                    StatItem(
                        label = stringResource(LocalizationR.string.feature_property_details_label_bathrooms),
                        value = property.bathrooms?.toString() ?: "-"
                    )
                    StatItem(
                        label = stringResource(LocalizationR.string.feature_property_details_label_area),
                        value = property.areaSize?.let { measurementFormatter.formatArea(it) } ?: "-"
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                EstatiaText(
                    text = stringResource(LocalizationR.string.feature_property_details_section_about),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                EstatiaText(
                    text = property.description ?: stringResource(LocalizationR.string.feature_property_details_no_description),
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                EstatiaButton(
                    onClick = { /* TODO: Contact Owner */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EstatiaText(
                        stringResource(
                            LocalizationR.string.feature_property_details_button_contact,
                            property.ownerName ?: stringResource(LocalizationR.string.feature_property_details_owner_fallback)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Overlay Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .statusBarsPadding()
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape),
            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(LocalizationR.string.feature_property_details_back_cd)
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        EstatiaText(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        EstatiaText(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Preview(name = "Property Details - Success", showBackground = true)
@DevicePreviews
@Composable
fun PropertyDetailsScreenSuccessPreview() {
    EstatiaTheme {
        EstatiaBackground {
            PropertyDetailsScreen(
                uiState = PropertyDetailsUiState.Success(MockProperties.single()),
                onBackClick = {},
                playbackUiState = PlayerUiState.Idle,
                isMuted = false,
                onMuteToggle = {},
                onPlaybackRetry = {},
                getPlayer = { _, _, _, _ -> throw Exception("Not implemented") },
                onPausePlayback = {},
                isMediaActive = { false }
            )
        }
    }
}

@Preview(name = "Property Details - Success (Swahili)", showBackground = true, locale = "sw")
@Composable
fun PropertyDetailsScreenSwahiliPreview() {
    EstatiaTheme {
        EstatiaBackground {
            PropertyDetailsScreen(
                uiState = PropertyDetailsUiState.Success(MockProperties.single()),
                onBackClick = {},
                playbackUiState = PlayerUiState.Idle,
                isMuted = false,
                onMuteToggle = {},
                onPlaybackRetry = {},
                getPlayer = { _, _, _, _ -> throw Exception("Not implemented") },
                onPausePlayback = {},
                isMediaActive = { false }
            )
        }
    }
}

@Preview(name = "Property Details - Loading", showBackground = true)
@DevicePreviews
@Composable
fun PropertyDetailsScreenLoadingPreview() {
    EstatiaTheme {
        EstatiaBackground {
            PropertyDetailsScreen(
                uiState = PropertyDetailsUiState.Loading,
                onBackClick = {},
                playbackUiState = PlayerUiState.Idle,
                onPlaybackRetry = {},
                getPlayer = { _, _, _, _ -> throw Exception("Not implemented") },
                onPausePlayback = {},
                isMediaActive = { false }
            )
        }
    }
}

@Preview(name = "Property Details - Error", showBackground = true)
@DevicePreviews
@Composable
fun PropertyDetailsScreenErrorPreview() {
    EstatiaTheme {
        EstatiaBackground {
            PropertyDetailsScreen(
                uiState = PropertyDetailsUiState.Error("Failed to load property details. Please try again."),
                onBackClick = {},
                playbackUiState = PlayerUiState.Idle,
                onPlaybackRetry = {},
                getPlayer = { _, _, _, _ -> throw Exception("Not implemented") },
                onPausePlayback = {},
                isMediaActive = { false }
            )
        }
    }
}
