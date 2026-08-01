package com.estatia.realestate.apps.core.domain.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult

interface IConfigDataRepository {
    suspend fun fetchRemoteConfig(): AppResult<String?>
}
