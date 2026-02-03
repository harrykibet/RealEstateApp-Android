package com.estatia.realestate.apps.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.IconButton
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.estatia.realestate.apps.core.designsystem.component.DynamicAsyncImage
import com.estatia.realestate.apps.core.model.property.Property
import com.estatia.realestate.apps.core.domain.interfaces.IExoplayer


@Composable
fun PropertyCard(
    modifier: Modifier = Modifier,
    property: Property,
    onPropertyClick: (Property) -> Unit,
    onLikeClick: (Property) -> Unit,
    onCommentClick: (Property) -> Unit,
    onShareClick: (Property) -> Unit,
    exoPlayer: IExoplayer // Pass the ExoPlayer interface
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column {
            // Media (Image carousel or video placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                if (property.videosAvailable && property.videoUrls.isNotEmpty()) {
                    val mediaUrl = property.videoUrls.first()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Video",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        val context = LocalContext.current
                        val playerView = remember { PlayerView(context) }

                        LaunchedEffect(mediaUrl) {
                            exoPlayer.attachPlayerToView(playerView, mediaUrl)
                        }

                        DisposableEffect(mediaUrl) {
                            onDispose {
                                exoPlayer.detachPlayer()
                                exoPlayer.releasePlayer(mediaUrl)
                            }
                        }

                        AndroidView(
                            factory = { playerView },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    ImagePager(property.imageUrls)
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(15.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    // Dot indicator placeholder
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(property.imageUrls.size) {
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }

                    property.title?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    property.description?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionButton(
                            property = property,
                            icon = Icons.Default.Favorite,
                            count = property.likesCount.toString(),
                            onClick = onLikeClick
                        )
                        ActionButton(
                            property = property,
                            icon = Icons.AutoMirrored.Default.Comment,
                            count = property.commentsCount.toString(),
                            onClick = onCommentClick
                        )
                        ActionButton(
                            property = property,
                            icon = Icons.Default.Share,
                            count = property.sharesCount.toString(),
                            onClick = onShareClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImagePager(imageUrls: List<String>) {
    val pagerState = rememberPagerState { imageUrls.size }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        DynamicAsyncImage(
            imageUrl = imageUrls[page],
            contentDescription = "Property Image",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ActionButton(
    property: Property,
    icon: ImageVector,
    count: String,
    onClick: (Property) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 12.dp)
    ) {
        IconButton(onClick = { onClick(property) }) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = count,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
