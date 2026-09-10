package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
interface ISecurityExceptionTranslator {
    fun translate(throwable: Throwable, default: SecurityException): SecurityException
}
