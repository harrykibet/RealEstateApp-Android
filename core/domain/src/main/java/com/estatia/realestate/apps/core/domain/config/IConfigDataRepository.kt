package com.estatia.realestate.apps.core.domain.config

import com.estatia.realestate.apps.core.common.exceptions.AppResult

interface IConfigDataRepository {
    suspend fun fetchRemoteConfig(): AppResult<String?>
}
