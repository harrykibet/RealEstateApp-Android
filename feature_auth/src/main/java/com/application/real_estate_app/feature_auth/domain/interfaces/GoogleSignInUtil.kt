package com.application.real_estate_app.feature_auth.domain.interfaces

import android.content.Context
import com.application.real_estate_app.feature_auth.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import javax.inject.Singleton

@Singleton
object GoogleSignInUtil {
    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        //TODO("Deprecated Apis")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }
}
