package com.example.coffeeshopapp.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption

object GoogleSignInHelper {
    const val WEB_CLIENT_ID = "370933304710-887psjbi23csauc0apo315ppsko6k0nb.apps.googleusercontent.com"

    suspend fun signIn(context: Context): String? {
        return runCatching {
            val credentialManager = CredentialManager.create(context)

            val signInOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID).build()
            val signInRequest = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()

            credentialManager.getIdToken(context, signInRequest)
        }.getOrElse { firstError ->
            android.util.Log.e("GoogleSignIn", "Lỗi SignInWithGoogleOption: ${firstError.message}", firstError)

            runCatching {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(WEB_CLIENT_ID)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                credentialManager.getIdToken(context, request)
            }.getOrElse { secondError ->
                android.util.Log.e("GoogleSignIn", "Lỗi GetGoogleIdOption (Fallback): ${secondError.message}", secondError)

                throw secondError
            }
        }
    }

    private suspend fun CredentialManager.getIdToken(
        context: Context,
        request: GetCredentialRequest
    ): String {
        val result: GetCredentialResponse = getCredential(
            request = request,
            context = context
        )

        val credential = result.credential
        return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }
}
