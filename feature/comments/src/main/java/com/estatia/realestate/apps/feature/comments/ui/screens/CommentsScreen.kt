package com.estatia.realestate.apps.feature.comments.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.model.feature.CommentDomainModel
import com.estatia.realestate.apps.core.ui.DevicePreviews
import com.estatia.realestate.apps.feature.comments.actions.CommentsAction
import com.estatia.realestate.apps.feature.comments.state.CommentsUiState

@Composable
fun CommentsScreen(
    state: CommentsUiState,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onAction: (CommentsAction) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {


            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(state.comments) { comment ->
                    CommentItem(comment)
                }
            }


            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier.weight(1f),
                    value = state.input,
                    onValueChange = {
                        onAction(CommentsAction.InputChanged(it))
                    },
                    placeholder = { Text("Write a comment…") }
                )


                IconButton(
                    onClick = {
                        onAction(CommentsAction.SendComment)
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: CommentDomainModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(comment.authorName, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(comment.message)
    }
}


@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 400
)

@DevicePreviews
@Composable
fun CommentsScreenLightPreview() {
    EstatiaTheme {
        EstatiaBackground {
            CommentsScreen(
                state = CommentsUiState(),
                snackbarHostState = SnackbarHostState(),
                onAction = {},
                onBack = {}
            )
        }
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 400
)

@DevicePreviews
@Composable
fun CommentsScreenDarkPreview() {
    EstatiaTheme {
        EstatiaBackground {
            CommentsScreen(
                state = CommentsUiState(),
                snackbarHostState = SnackbarHostState(),
                onAction = {},
                onBack = {}
            )
        }
    }
}