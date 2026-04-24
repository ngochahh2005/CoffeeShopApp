package com.example.coffeeshopapp.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.local.FavoritesDataStore
import com.example.coffeeshopapp.data.coffeeCategories
import com.example.coffeeshopapp.data.model.entity.Category
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.data.trendingCoffeeList
import com.example.coffeeshopapp.utils.toFullImageUrl
import com.example.coffeeshopapp.utils.isActiveResolved
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.example.coffeeshopapp.utils.getErrorMessage

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    var searchKeyWords by mutableStateOf("")
    private set

    fun onSearchKeyWordsChange(newValue: String) {
        searchKeyWords = newValue
    }

    init {
        loadData()
        observeFavorites()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // Tai du lieu tu server
     fun loadData() {
        viewModelScope.launch {
            if (_uiState.value.trendingItems.isNotEmpty()) return@launch

            _uiState.update { it.copy(isLoading = true) }

            try {
                val productResponse = NetworkClient.api.getProduct()
                if (productResponse.result != null) {
                    val products = productResponse.result
                    val savedFavs = try {
                        FavoritesDataStore.favoritesFlow(getApplication()).first()
                    } catch (ex: Exception) {
                        emptySet<String>()
                    }

                    val productMap = products.map { p ->
                        Product(
                            id = p.id.toString(),
                            name = p.name,
                            price = p.basePrice.toLong(),
                            imageUrl = p.imageUrl.toFullImageUrl(),
                            rating = 0.0,
                            reviewers = 0,
                            isFavorite = savedFavs.contains(p.id.toString())
                        )
                    }

                    val categoryMap = try {
                        val catResp = NetworkClient.api.getCategories()
                        catResp.result?.map { c ->
                            Category(
                                id = c.id,
                                name = c.name,
                                imageUrl = c.imageUrl?.toFullImageUrl(),
                                displayOrder = c.displayOrder,
                                isActive = c.isActiveResolved()
                            )
                        } ?: coffeeCategories
                    } catch (ex: Exception) {
                        coffeeCategories
                    }

                    _uiState.update {
                        it.copy(
                            categories = categoryMap,
                            trendingItems = productMap,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    delay(1000)
                    _uiState.update {
                        it.copy(
                            categories = coffeeCategories,
                            trendingItems = trendingCoffeeList,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.getErrorMessage()
                    )
                }
            }
        }
    }

    fun toggleFavorite(productId: String) {
        val previous = _uiState.value.trendingItems

        _uiState.update { state ->
            val updated = state.trendingItems.map { item ->
                if (item.id == productId) item.copy(isFavorite = !item.isFavorite) else item
            }
            state.copy(trendingItems = updated, loadingFavorites = state.loadingFavorites + productId)
        }

        viewModelScope.launch {
            val idLong = productId.toLongOrNull()
            if (idLong == null) {
                _uiState.update { it.copy(error = "Invalid product id") }
                return@launch
            }

            try {
                FavoritesDataStore.toggleFavorite(getApplication(), productId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.getErrorMessage()) }
            }

            try {
                val resp = NetworkClient.api.toggleFavorite(idLong)
                resp.result?.let { favStatus ->
                    _uiState.update { state ->
                        val synced = state.trendingItems.map { item ->
                            if (item.id == favStatus.productId.toString()) item.copy(isFavorite = favStatus.favorited)
                            else item
                        }
                        state.copy(trendingItems = synced)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.getErrorMessage()) }
            } finally {
                _uiState.update { it.copy(loadingFavorites = it.loadingFavorites - productId) }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            FavoritesDataStore.favoritesFlow(getApplication()).collect { favSet ->
                _uiState.update { state ->
                    val updated = state.trendingItems.map { item ->
                        item.copy(isFavorite = favSet.contains(item.id))
                    }
                    state.copy(trendingItems = updated)
                }
            }
        }
    }


    private val _flyAnimationEvent = MutableSharedFlow<Pair<String, Offset>>()
    val flyAnimationEvent = _flyAnimationEvent.asSharedFlow()

    fun addToCart(coffeeId: String, offset: Offset) {
        viewModelScope.launch {
            _flyAnimationEvent.emit(Pair(coffeeId, offset))
            println("ÄÃ£ thÃªm mÃ³n $coffeeId vÃ o giá» hÃ ng táº¡i vá»‹ trÃ­ $offset")
        }
    }
}


data class HomeUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val trendingItems: List<Product> = emptyList(),
    val loadingFavorites: Set<String> = emptySet(),
    val error: String? = null
)
