package com.estatia.realestate.apps.core.testing.generators

import com.estatia.realestate.apps.core.model.feature.PaymentMethod
import com.estatia.realestate.apps.core.model.property.Money
import kotlin.random.Random

/**
 * Generator for payment data models.
 */
object PaymentGenerator {
    fun generateAmount(): Money = Money(Random.nextDouble(1000.0, 100000.0))
    
    fun generateMethod(): PaymentMethod = listOf(
        PaymentMethod.CreditCard,
        PaymentMethod.PayPal,
        PaymentMethod.ApplePay,
        PaymentMethod.GooglePay
    ).random()
}
