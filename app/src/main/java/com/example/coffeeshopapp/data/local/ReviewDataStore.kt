package com.example.coffeeshopapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.reviewDataStore: DataStore<Preferences> by preferencesDataStore(name = "review_prefs")

object ReviewDataStore {
    private val REVIEWED_ORDERS_KEY = stringSetPreferencesKey("reviewed_orders")

    fun reviewedOrdersFlow(context: Context): Flow<Set<String>> {
        return context.reviewDataStore.data.map { prefs ->
            prefs[REVIEWED_ORDERS_KEY] ?: emptySet()
        }
    }

    suspend fun markOrderAsReviewed(context: Context, orderId: Long) {
        if (orderId == 0L) return
        context.reviewDataStore.edit { prefs ->
            val current = prefs[REVIEWED_ORDERS_KEY] ?: emptySet()
            prefs[REVIEWED_ORDERS_KEY] = current + orderId.toString()
        }
    }
}
