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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

object FavoritesDataStore {
    private val FAVORITES_KEY = stringPreferencesKey("favorites")

    fun favoritesFlow(context: Context): Flow<Set<String>> {
        return context.dataStore.data
            .map { prefs ->
                val raw = prefs[FAVORITES_KEY] ?: ""
                if (raw.isBlank()) emptySet() else raw.split(",").toSet()
            }
    }

    suspend fun toggleFavorite(context: Context, id: String) {
        val current = context.dataStore.data.first()[FAVORITES_KEY] ?: ""
        val set = if (current.isBlank()) mutableSetOf<String>() else current.split(",").toMutableSet()
        if (set.contains(id)) set.remove(id) else set.add(id)
        val newRaw = set.joinToString(",")
        context.dataStore.edit { prefs ->
            prefs[FAVORITES_KEY] = newRaw
        }
    }

    suspend fun setFavorites(context: Context, ids: Set<String>) {
        val newRaw = ids.joinToString(",")
        context.dataStore.edit { prefs ->
            prefs[FAVORITES_KEY] = newRaw
        }
    }
}
