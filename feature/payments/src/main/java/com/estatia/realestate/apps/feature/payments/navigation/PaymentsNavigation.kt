package com.estatia.realestate.apps.feature.payments.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.estatia.realestate.apps.core.model.feature.PaymentResult
import com.estatia.realestate.apps.core.navigation.routes.PaymentBaseRoute
import com.estatia.realestate.apps.core.navigation.routes.PaymentNavConstants.PAYMENT_RESULT_KEY
import com.estatia.realestate.apps.core.navigation.routes.PaymentRoute
import com.estatia.realestate.apps.feature.payments.ui.PaymentsRoute as PaymentsScreen

fun NavController.navigateToPayment(
    referenceId: String,
    amount: Double,
    currency: String,
    context: com.estatia.realestate.apps.core.model.feature.PaymentContext,
    navOptions: NavOptions? = null
) = navigate(route = PaymentRoute(referenceId, amount, currency, context), navOptions)

fun NavGraphBuilder.paymentGraph(
    onPaymentDone: (PaymentResult) -> Unit
) {
    navigation<PaymentBaseRoute>(startDestination = PaymentRoute::class) {
        composable<PaymentRoute> {
            PaymentsScreen(
                onPaymentDone = onPaymentDone
            )
        }
    }
}

fun NavController.setPaymentResult(result: PaymentResult) {
    previousBackStackEntry?.savedStateHandle?.set(PAYMENT_RESULT_KEY, result)
}
