package com.estatia.realestate.apps.feature.search.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.designsystem.component.EstatiaTextField
import com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.model.property.toListingUiModel
import com.estatia.realestate.apps.core.ui.DevicePreviews
import com.estatia.realestate.apps.core.ui.screens.PropertyFeedScreen
import com.estatia.realestate.apps.feature.comments.actions.CommentsAction
import com.estatia.realestate.apps.feature.comments.ui.screens.CommentSheetContent
import com.estatia.realestate.apps.feature.comments.ui.viewmodels.CommentsViewModel
import com.estatia.realestate.apps.feature.search.ui.SearchUiState
import com.estatia.realestate.apps.feature.search.ui.viewmodels.SearchViewModel

@Composable
fun SearchRoute(
    onNavigateToPropertyDetail: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    SearchScreen(
        uiState = uiState,
        onSearch = viewModel::searchProperties,
        onClearHistory = viewModel::clearSearchHistory,
        onNavigateToPropertyDetail = onNavigateToPropertyDetail
    )
}

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onSearch: (String) -> Unit,
    onClearHistory: () -> Unit,
    onNavigateToPropertyDetail: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                EstatiaTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "Search properties...",
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Trigger search when user presses search on keyboard
                // Note: EstatiaTextField doesn't expose KeyboardActions currently, 
                // but let's assume it should or use a simpler field if needed.
                // For now, let's just use the query if Success state comes from outside.
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (uiState) {
                is SearchUiState.Initial, SearchUiState.Loading -> {
                    if (uiState is SearchUiState.Loading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is SearchUiState.History -> {
                    SearchHistorySection(
                        history = uiState.history,
                        onHistoryItemClick = {
                            searchQuery = it
                            onSearch(it)
                        },
                        onClearHistory = onClearHistory
                    )
                }

                is SearchUiState.Success -> {
                    val listings = remember(uiState.results) {
                        uiState.results.map { it.toListingUiModel() }
                    }
                    
                    if (listings.isEmpty()) {
                        EmptySearchResults()
                    } else {
                        PropertyFeedScreen(
                            listings = listings,
                            onLikeClick = {},
                            onShareClick = {},
                            onPropertyClick = { onNavigateToPropertyDetail(it.id) },
                            commentsContent = { propertyId ->
                                val commentsViewModel: CommentsViewModel = hiltViewModel()
                                LaunchedEffect(propertyId) {
                                    commentsViewModel.onAction(CommentsAction.Load(propertyId))
                                }
                                val commentsState by commentsViewModel.state.collectAsState()
                                CommentSheetContent(
                                    state = commentsState,
                                    onAction = commentsViewModel::onAction
                                )
                            }
                        )
                    }
                }

                is SearchUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EstatiaText(text = uiState.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
    
    // Auto-search if query is typed and enter is pressed (simplified for now)
    LaunchedEffect(searchQuery) {
        if (searchQuery.length > 2) {
            onSearch(searchQuery)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchHistorySection(
    history: List<String>,
    onHistoryItemClick: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EstatiaText(
                text = "Recent Searches",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            TextButton(onClick = onClearHistory) {
                EstatiaText(text = "Clear All", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (history.isEmpty()) {
            EstatiaText(
                text = "No recent searches",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                history.forEach { item ->
                    SuggestionChip(
                        onClick = { onHistoryItemClick(item) },
                        label = { EstatiaText(text = item) },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySearchResults() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = EstatiaIcons.SearchBorder,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        EstatiaText(
            text = "No properties found",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        EstatiaText(
            text = "Try searching for a different location or property type.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@DevicePreviews
@Composable
fun SearchScreenHistoryPreview() {
    EstatiaTheme {
        EstatiaBackground {
            SearchScreen(
                uiState = SearchUiState.History(listOf("Nairobi", "Apartment", "Westlands")),
                onSearch = {},
                onClearHistory = {},
                onNavigateToPropertyDetail = {},
            )
        }
    }
}
