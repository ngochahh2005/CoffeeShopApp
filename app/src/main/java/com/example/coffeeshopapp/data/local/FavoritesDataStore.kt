package com.example.coffeeshopapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

object FavoritesDataStore {
    fun favoritesFlow(context: Context): Flow<Set<String>> {
        return AuthDataStore.userIdFlow(context).combine(context.dataStore.data) { userId, prefs ->
            val key = favoritesKey(userId)
            val raw = prefs[key] ?: ""
            val result = if (raw.isBlank()) emptySet() else raw.split(",").toSet()
//            android.util.Log.d("FavoritesDataStore", "favoritesFlow: userId=$userId, key=$key, favorites=$result")
            result
        }
    }

    suspend fun toggleFavorite(context: Context, id: String) {
        val userId = AuthDataStore.userIdFlow(context).first()
        val key = favoritesKey(userId)
        var newSet: Set<String> = emptySet()
        // perform atomic read-modify-write inside edit to avoid race between concurrent toggles
        context.dataStore.edit { prefs ->
            val current = prefs[key] ?: ""
            val mutable = if (current.isBlank()) mutableSetOf<String>() else current.split(",").toMutableSet()
            if (mutable.contains(id)) mutable.remove(id) else mutable.add(id)
            prefs[key] = mutable.joinToString(",")
            newSet = mutable.toSet()
        }
//        android.util.Log.d("FavoritesDataStore", "toggleFavorite: userId=$userId, key=$key, id=$id, favorites=$newSet")
        // debug toast removed: prefer logging only
    //        android.util.Log.d("FavoritesDataStore", "toggleFavorite: userId=$userId, key=$key, id=$id, favorites=$newSet")
    }

    suspend fun setFavorites(context: Context, ids: Set<String>) {
        val userId = AuthDataStore.userIdFlow(context).first()
        val key = favoritesKey(userId)
        context.dataStore.edit { prefs ->
            prefs[key] = ids.joinToString(",")
        }
    }

    private fun favoritesKey(userId: String?): Preferences.Key<String> {
        val suffix = userId?.trim().takeUnless { it.isNullOrBlank() } ?: "guest"
        return stringPreferencesKey("favorites_$suffix")
    }
}
