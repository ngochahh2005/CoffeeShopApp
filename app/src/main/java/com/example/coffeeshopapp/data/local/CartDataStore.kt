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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.json.JSONArray

private val Context.cartDataStore: DataStore<Preferences> by preferencesDataStore(name = "cart_prefs")

object CartDataStore {
    private val gson = Gson()

    fun cartItemsFlow(context: Context): Flow<List<CartItem>> {
        return AuthDataStore.userIdFlow(context).combine(context.cartDataStore.data) { userId, prefs ->
            deserialize(prefs[cartItemsKey(userId)])
        }
    }

    fun cartCountFlow(context: Context): Flow<Int> {
        return cartItemsFlow(context).map { items ->
            items.size
        }
    }

    suspend fun addProduct(context: Context, product: Product, quantity: Int = 1) {
        if (quantity <= 0) return
        val key = cartItemsKey(AuthDataStore.readUserIdBlocking(context))
        context.cartDataStore.edit { prefs ->
            val currentItems = deserialize(prefs[key]).toMutableList()
            val index = currentItems.indexOfFirst { it.productId == product.id }
            if (index >= 0) {
                val existing = currentItems[index]
                currentItems[index] = existing.copy(quantity = existing.quantity + quantity)
            } else {
                currentItems.add(
                    CartItem(
                        productId = product.id,
                        nameAtAdd = product.name,
                        priceAtAdd = product.price,
                        imageUrlAtAdd = product.imageUrl,
                        quantity = quantity
                    )
                )
            }
            prefs[key] = serialize(currentItems)
        }
    }

    suspend fun updateQuantity(context: Context, productId: String, quantity: Int) {
        val key = cartItemsKey(AuthDataStore.readUserIdBlocking(context))
        context.cartDataStore.edit { prefs ->
            val currentItems = deserialize(prefs[key]).toMutableList()
            val updatedItems = if (quantity <= 0) {
                currentItems.filterNot { it.productId == productId }
            } else {
                currentItems.map { item ->
                    if (item.productId == productId) item.copy(quantity = quantity) else item
                }
            }
            prefs[key] = serialize(updatedItems)
        }
    }

    suspend fun removeProduct(context: Context, productId: String) {
        updateQuantity(context, productId, 0)
    }

    suspend fun clear(context: Context) {
        val key = cartItemsKey(AuthDataStore.readUserIdBlocking(context))
        context.cartDataStore.edit { prefs ->
            prefs.remove(key)
        }
    }

    private fun serialize(items: List<CartItem>): String = gson.toJson(items)

    private fun deserialize(raw: String?): List<CartItem> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i) ?: continue
                    val productId = obj.optString("productId").trim()
                    if (productId.isBlank()) continue

                    val quantity = obj.optInt("quantity", 1)
                    val nameAtAdd = when {
                        obj.has("nameAtAdd") -> obj.optString("nameAtAdd")
                        else -> obj.optString("name")
                    }
                    val priceAtAdd = when {
                        obj.has("priceAtAdd") -> obj.optLong("priceAtAdd", 0L)
                        else -> obj.optLong("price", 0L)
                    }
                    val imageUrlAtAdd = when {
                        obj.has("imageUrlAtAdd") -> obj.optString("imageUrlAtAdd").takeIf { it.isNotBlank() }
                        else -> obj.optString("imageUrl").takeIf { it.isNotBlank() }
                    }

                    add(
                        CartItem(
                            productId = productId,
                            nameAtAdd = nameAtAdd,
                            priceAtAdd = priceAtAdd,
                            imageUrlAtAdd = imageUrlAtAdd,
                            quantity = quantity
                        )
                    )
                }
            }
        }.getOrElse { emptyList() }
    }

    private fun cartItemsKey(userId: String?): Preferences.Key<String> {
        val suffix = userId?.trim().takeUnless { it.isNullOrBlank() } ?: "guest"
        return stringPreferencesKey("cart_items_$suffix")
    }
}
