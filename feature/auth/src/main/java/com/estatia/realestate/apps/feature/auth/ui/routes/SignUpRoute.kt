package com.estatia.realestate.apps.feature.auth.ui.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.estatia.realestate.apps.feature.auth.events.SignUpEvent
import com.estatia.realestate.apps.feature.auth.ui.screens.SignUpScreen
import com.estatia.realestate.apps.feature.auth.viewModels.SignUpViewModel

@Composable
fun SignUpRoute(
    onSignUpSuccess: () -> Unit,
    onAlreadyHaveAccount: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                SignUpEvent.Success -> onSignUpSuccess()
            }
        }
    }

    SignUpScreen(
        state = state,
        onAction = viewModel::onAction,
        onAlreadyHaveAccountClick = onAlreadyHaveAccount
    )
}

