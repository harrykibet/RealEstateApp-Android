package com.estatia.realestate.apps.core.common.system

import com.estatia.realestate.apps.core.common.BuildConfig
import com.estatia.realestate.apps.core.common.interfaces.BuildEnvironment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidBuildEnvironment @Inject constructor() : BuildEnvironment {
    override val isDebug: Boolean = BuildConfig.DEBUG
}
