package com.application.real_estate_app.core_network.utils

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val niaDispatcher: ReaDispatchers)

enum class ReaDispatchers {
    Default,
    IO,
}
