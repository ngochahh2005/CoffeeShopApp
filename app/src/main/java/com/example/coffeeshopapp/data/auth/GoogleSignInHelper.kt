package com.example.coffeeshopapp.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption

/**
 * Helper class for Google Sign-In using Android Credential Manager.
 *
 * Web Client ID lấy từ Google Cloud Console (cùng project với BE).
 */
object GoogleSignInHelper {

    // Web Client ID từ Google Cloud Console (phải khớp với google.client-id trong application.properties của BE)
    const val WEB_CLIENT_ID = "370933304710-bj55m7fove17vm3vkgmsc3mt3lidfoif.apps.googleusercontent.com"

    /**
     * Launches Google Sign-In via Credential Manager.
     * Returns the Google ID token string if successful, or null if failed/cancelled.
     */
    suspend fun signIn(context: Context): String? {
        return runCatching {
            val credentialManager = CredentialManager.create(context)

            // Đây là luồng dành cho nút "Đăng nhập với Google", ổn định hơn so với lấy credential chung.
            val signInOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID).build()
            val signInRequest = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()

            credentialManager.getIdToken(context, signInRequest)
        }.getOrElse { firstError ->
            firstError.printStackTrace()

            // Fallback: mở danh sách tất cả tài khoản Google nếu máy không hỗ trợ SignInWithGoogleOption tốt.
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
                secondError.printStackTrace()
                null
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
