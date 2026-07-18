package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.common.exceptions.DomainMappableException
import com.estatia.realestate.apps.core.common.exceptions.SearchException
import com.estatia.realestate.apps.core.data.interfaces.DataExceptionMapper
import javax.inject.Inject

class SearchExceptionMapper @Inject constructor()
    : DataExceptionMapper<SearchException> {


    override fun map(
        exception: DomainMappableException
    ): SearchException {


        return when(exception){


            is DatabaseException ->
                SearchException.QueryFailed


            else ->
                SearchException.Unknown(exception as Throwable)
        }
    }
}