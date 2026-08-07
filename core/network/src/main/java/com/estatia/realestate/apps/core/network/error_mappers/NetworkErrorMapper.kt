package com.estatia.realestate.apps.core.network.error_mappers

import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.network.interfaces.INetworkErrorMapper
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

internal class NetworkErrorMapper @Inject constructor()
    : INetworkErrorMapper {


    override fun map(
        throwable: Throwable
    ): NetworkException {


        return when(throwable) {


            is HttpException -> {

                when(throwable.code()) {


                    401 ->
                        NetworkException.Unauthorized


                    429 ->
                        NetworkException.RateLimited


                    in 500..599 ->
                        NetworkException.ServerError(
                            throwable.code()
                        )


                    else ->
                        NetworkException.ClientError(
                            throwable.code()
                        )
                }
            }


            is SocketTimeoutException ->
                NetworkException.Timeout


            is IOException ->
                NetworkException.ConnectionFailed

            else ->
                NetworkException.Unknown(
                    throwable
                )
        }
    }
}
