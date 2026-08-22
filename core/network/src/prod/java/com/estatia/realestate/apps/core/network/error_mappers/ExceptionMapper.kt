package com.estatia.realestate.apps.core.network.error_mappers

import com.amplifyframework.AmplifyException
import com.amplifyframework.api.ApiException
import com.amplifyframework.auth.AuthException as AmplifyAuthException
import com.amplifyframework.storage.StorageException as AmplifyStorageException
import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.network.di.AwsMapper
import com.estatia.realestate.apps.core.network.di.FirebaseMapper
import com.estatia.realestate.apps.core.network.interfaces.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.StorageException as FirebaseStorageException
import javax.inject.Inject

/**
 * Robust entry point for error mapping.
 * Routes exceptions to provider-specific mappers (Firebase or AWS) based on the exception type.
 */
class ExceptionMapper @Inject constructor(
    private val networkMapper: INetworkErrorMapper,
    @FirebaseMapper private val firebaseAuthMapper: IAuthExceptionMapper,
    @AwsMapper private val awsAuthMapper: IAuthExceptionMapper,
    @FirebaseMapper private val firebaseDatabaseMapper: IDatabaseErrorMapper,
    @AwsMapper private val awsDatabaseMapper: IDatabaseErrorMapper,
    @FirebaseMapper private val firebaseStorageMapper: IStorageErrorMapper,
    @AwsMapper private val awsStorageMapper: IStorageErrorMapper,
    @FirebaseMapper private val firebaseInfraMapper: IInfrastructureErrorMapper,
    @AwsMapper private val awsInfraMapper: IInfrastructureErrorMapper
) : IExceptionMapper {


    override fun map(
        throwable: Throwable
    ): AppException {

        return when (throwable) {
            // Firebase specific exceptions
            is FirebaseAuthException -> firebaseAuthMapper.map(throwable)
            is FirebaseFirestoreException -> firebaseDatabaseMapper.map(throwable)
            is FirebaseStorageException -> firebaseStorageMapper.map(throwable)
            is FirebaseException -> firebaseInfraMapper.map(throwable)
            
            // AWS Amplify specific exceptions
            is AmplifyAuthException -> awsAuthMapper.map(throwable)
            is ApiException -> awsDatabaseMapper.map(throwable)
            is AmplifyStorageException -> awsStorageMapper.map(throwable)
            is AmplifyException -> awsInfraMapper.map(throwable)

            // Fallback to general network errors (OkHttp, etc.)
            else -> networkMapper.map(throwable)
        }
    }
}
