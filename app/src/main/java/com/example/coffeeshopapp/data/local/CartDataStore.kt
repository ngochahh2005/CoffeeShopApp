package com.example.coffeeshopapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.coffeeshopapp.data.model.entity.CartItem
import com.example.coffeeshopapp.data.model.entity.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.cartDataStore: DataStore<Preferences> by preferencesDataStore(name = "cart_prefs")

object CartDataStore {
    private val CART_ITEMS_KEY = stringPreferencesKey("cart_items")
    private val gson = Gson()
    private val cartItemListType = object : TypeToken<List<CartItem>>() {}.type

    fun cartItemsFlow(context: Context): Flow<List<CartItem>> {
        return context.cartDataStore.data.map { prefs ->
            deserialize(prefs[CART_ITEMS_KEY])
        }
    }

    fun cartCountFlow(context: Context): Flow<Int> {
        return cartItemsFlow(context).map { items ->
            items.sumOf { it.quantity }
        }
    }

    suspend fun addProduct(context: Context, product: Product, quantity: Int = 1) {
        if (quantity <= 0) return
        context.cartDataStore.edit { prefs ->
            val currentItems = deserialize(prefs[CART_ITEMS_KEY]).toMutableList()
            val index = currentItems.indexOfFirst { it.productId == product.id }
            if (index >= 0) {
                val existing = currentItems[index]
                currentItems[index] = existing.copy(quantity = existing.quantity + quantity)
            } else {
                currentItems.add(
                    CartItem(
                        productId = product.id,
                        name = product.name,
                        price = product.price,
                        imageUrl = product.imageUrl,
                        quantity = quantity
                    )
                )
            }
            prefs[CART_ITEMS_KEY] = serialize(currentItems)
        }
    }

    suspend fun updateQuantity(context: Context, productId: String, quantity: Int) {
        context.cartDataStore.edit { prefs ->
            val currentItems = deserialize(prefs[CART_ITEMS_KEY]).toMutableList()
            val updatedItems = if (quantity <= 0) {
                currentItems.filterNot { it.productId == productId }
            } else {
                currentItems.map { item ->
                    if (item.productId == productId) item.copy(quantity = quantity) else item
                }
            }
            prefs[CART_ITEMS_KEY] = serialize(updatedItems)
        }
    }

    suspend fun removeProduct(context: Context, productId: String) {
        updateQuantity(context, productId, 0)
    }

    suspend fun clear(context: Context) {
        context.cartDataStore.edit { prefs ->
            prefs.remove(CART_ITEMS_KEY)
        }
    }

    private fun serialize(items: List<CartItem>): String = gson.toJson(items)

    private fun deserialize(raw: String?): List<CartItem> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            gson.fromJson<List<CartItem>>(raw, cartItemListType) ?: emptyList()
        }.getOrElse { emptyList() }
    }
}
