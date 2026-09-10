package com.estatia.realestate.apps.core.model.property

import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@DomainModel
@JvmInline
value class Money(val amount: Double)
