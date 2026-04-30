package com.example.coffeeshopapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

object AuthDataStore {
    private val TOKEN_KEY = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val ROLES_KEY = stringPreferencesKey("roles")
    private val PROVIDER_KEY = stringPreferencesKey("provider")

    fun tokenFlow(context: Context): Flow<String?> {
        return context.authDataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }
    }

    fun refreshTokenFlow(context: Context): Flow<String?> {
        return context.authDataStore.data.map { prefs ->
            prefs[REFRESH_TOKEN_KEY]
        }
    }

    suspend fun setToken(context: Context, token: String?, refreshToken: String? = null) {
        context.authDataStore.edit { prefs ->
            if (token == null) prefs.remove(TOKEN_KEY) else prefs[TOKEN_KEY] = token
            if (refreshToken == null) prefs.remove(REFRESH_TOKEN_KEY) else prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    fun userIdFlow(context: Context): Flow<String?> {
        return context.authDataStore.data.map { prefs ->
            prefs[USER_ID_KEY]
        }
    }

    suspend fun setUserId(context: Context, userId: String?) {
        context.authDataStore.edit { prefs ->
            if (userId.isNullOrBlank()) prefs.remove(USER_ID_KEY) else prefs[USER_ID_KEY] = userId
        }
    }

    fun rolesFlow(context: Context): Flow<List<String>> {
        return context.authDataStore.data.map { prefs ->
            prefs[ROLES_KEY]
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?: emptyList()
        }
    }

    suspend fun setRoles(context: Context, roles: List<String>) {
        context.authDataStore.edit { prefs ->
            if (roles.isEmpty()) {
                prefs.remove(ROLES_KEY)
            } else {
                prefs[ROLES_KEY] = roles.joinToString(",")
            }
        }
    }

    fun providerFlow(context: Context): Flow<String> {
        return context.authDataStore.data.map { prefs ->
            prefs[PROVIDER_KEY] ?: "LOCAL"
        }
    }

    suspend fun setProvider(context: Context, provider: String) {
        context.authDataStore.edit { prefs ->
            prefs[PROVIDER_KEY] = provider
        }
    }

    suspend fun readTokenBlocking(context: Context): String? {
        return context.authDataStore.data.first()[TOKEN_KEY]
    }

    suspend fun readRefreshTokenBlocking(context: Context): String? {
        return context.authDataStore.data.first()[REFRESH_TOKEN_KEY]
    }

    suspend fun readUserIdBlocking(context: Context): String? {
        return context.authDataStore.data.first()[USER_ID_KEY]
    }

    suspend fun clearAll(context: Context) {
        context.authDataStore.edit { it.clear() }
    }
}
