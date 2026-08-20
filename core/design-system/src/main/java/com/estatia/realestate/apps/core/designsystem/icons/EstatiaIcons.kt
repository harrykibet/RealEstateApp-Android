package com.estatia.realestate.apps.core.designsystem.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.ShortText
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Upcoming
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Grid3x3
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Upcoming
import androidx.compose.material.icons.rounded.ViewDay
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Estatia icons. Material icons are [ImageVector]s, custom icons are typically [ImageVector]s as well.
 */
object EstatiaIcons {
    val Home: ImageVector = Icons.Rounded.Home
    val HomeBorder: ImageVector = Icons.Outlined.Home

    val Bookmark: ImageVector = Icons.Rounded.Bookmark
    val BookmarkBorder: ImageVector = Icons.Rounded.BookmarkBorder

    val Check : ImageVector = Icons.Rounded.Check

    val Search: ImageVector = Icons.Rounded.Search
    val SearchRounded: ImageVector = Icons.Rounded.Search
    val SearchBorder: ImageVector = Icons.Outlined.Search

    val Add: ImageVector = Icons.Rounded.Add
    val AddCircle: ImageVector = Icons.Rounded.AddCircle
    val AddCircleOutline: ImageVector = Icons.Rounded.AddCircleOutline

    val Favorites: ImageVector = Icons.Rounded.Favorite
    val FavoriteBorder: ImageVector = Icons.Rounded.FavoriteBorder

    val Profile: ImageVector = Icons.Rounded.Person
    val ProfileBorder: ImageVector = Icons.Outlined.Person

    val Chat: ImageVector = Icons.AutoMirrored.Rounded.Chat
    val ChatBorder: ImageVector = Icons.AutoMirrored.Outlined.Chat

    val ArrowBack = Icons.AutoMirrored.Rounded.ArrowBack
    val Bookmarks = Icons.Rounded.Bookmarks
    val BookmarksBorder = Icons.Rounded.Bookmarks // Using Rounded as fallback if Outlined is missing
    val Close = Icons.Rounded.Close
    val Grid3x3 = Icons.Rounded.Grid3x3
    val MoreVert = Icons.Rounded.MoreVert
    val Person = Icons.Rounded.Person
    val Settings = Icons.Rounded.Settings
    val ShortText = Icons.AutoMirrored.Rounded.ShortText
    val Upcoming = Icons.Rounded.Upcoming
    val UpcomingBorder = Icons.Outlined.Upcoming
    val ViewDay = Icons.Rounded.ViewDay
}
