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

    fun tokenFlow(context: Context): Flow<String?> {
        return context.authDataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }
    }

    suspend fun setToken(context: Context, token: String?) {
        context.authDataStore.edit { prefs ->
            if (token == null) prefs.remove(TOKEN_KEY) else prefs[TOKEN_KEY] = token
        }
    }

    suspend fun readTokenBlocking(context: Context): String? {
        return context.authDataStore.data.first()[TOKEN_KEY]
    }
}
