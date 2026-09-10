package com.estatia.realestate.apps.core.domain.config

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
interface IConfigDataRepository {
    suspend fun fetchRemoteConfig(): AppResult<String?>
}
