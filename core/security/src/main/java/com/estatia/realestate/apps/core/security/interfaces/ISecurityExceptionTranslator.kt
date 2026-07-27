package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.common.exceptions.SecurityException

interface ISecurityExceptionTranslator {
    fun translate(throwable: Throwable, default: SecurityException): SecurityException
}
