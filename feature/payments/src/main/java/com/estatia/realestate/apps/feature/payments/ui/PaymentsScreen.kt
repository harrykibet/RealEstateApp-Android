package com.estatia.realestate.apps.feature.payments.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.testTag
import com.estatia.realestate.apps.core.designsystem.component.EstatiaButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.model.feature.PaymentResult
import com.estatia.realestate.apps.feature.payments.PaymentsScreenState
import com.estatia.realestate.apps.feature.payments.PaymentsUiState
import com.estatia.realestate.apps.feature.payments.PaymentsViewModel

@Composable
fun PaymentsRoute(
    onPaymentDone: (PaymentResult) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaymentsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.uiState) {
        val uiState = state.uiState
        if (uiState is PaymentsUiState.Success) {
            onPaymentDone(PaymentResult.Success(uiState.transactionId))
        } else if (uiState is PaymentsUiState.Error) {
            // We could stay on screen to allow retry, or pass back error
        }
    }

    PaymentsScreen(
        state = state,
        onPaymentClick = viewModel::processPayment,
        onBackClick = { onPaymentDone(PaymentResult.Cancelled) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(
    state: PaymentsScreenState,
    onPaymentClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    EstatiaText(
                        text = "Checkout",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBackClick) {
                        androidx.compose.material3.Icon(
                            imageVector = com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EstatiaText(
                    text = "Amount to pay: ${state.currency} ${state.amount}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                EstatiaText(
                    text = "Payment for: ${state.referenceId}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.padding(16.dp))

                when (state.uiState) {
                    is PaymentsUiState.Idle -> {
                        EstatiaButton(
                            onClick = onPaymentClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("PayNowButton")
                        ) {
                            EstatiaText("Pay Now")
                        }
                    }
                    is PaymentsUiState.Processing -> {
                        CircularProgressIndicator()
                        EstatiaText("Processing payment...")
                    }
                    is PaymentsUiState.Success -> {
                        EstatiaText("Payment successful!", color = MaterialTheme.colorScheme.primary)
                    }
                    is PaymentsUiState.Error -> {
                        EstatiaText(state.uiState.message, color = MaterialTheme.colorScheme.error)
                        EstatiaButton(
                            onClick = onPaymentClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            EstatiaText("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Spacer(modifier: Modifier) {
    Box(modifier = modifier)
}
