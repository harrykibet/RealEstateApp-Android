package com.estatia.realestate.apps.core.common.system

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val estatiaDispatcher: EstatiaDispatchers)

enum class EstatiaDispatchers {
    Default,
    IO,
    Main
}
