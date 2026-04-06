package com.example.coffeeshopapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

object ProductDataStore{
    private val PRODUCT_KEY = stringPreferencesKey("products")

    fun productFlow(context: Context): Flow<Set<String>> {
        return context.dataStore.data
            .map { prefs ->
                val raw = prefs[PRODUCT_KEY] ?: ""
                if (raw.isBlank()) emptySet() else raw.split(",").toSet()
            }
    }
}