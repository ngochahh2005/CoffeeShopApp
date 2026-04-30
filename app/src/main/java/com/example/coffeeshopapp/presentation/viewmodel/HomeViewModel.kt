package com.example.coffeeshopapp.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.local.CartDataStore
import com.example.coffeeshopapp.data.local.FavoritesDataStore
import com.example.coffeeshopapp.data.local.AuthDataStore
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

data class HomeUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val trendingItems: List<Product> = emptyList(),
    val loadingFavorites: Set<String> = emptySet(),
    val error: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    var searchKeyWords by mutableStateOf("")
    private set

    fun onSearchKeyWordsChange(newValue: String) {
        searchKeyWords = newValue
    }

    init {
        // When the authenticated user changes, force refresh data so favorites are user-specific
        viewModelScope.launch {
            AuthDataStore.userIdFlow(getApplication()).collect {
                loadData(forceRefresh = true)
            }
        }

        loadData()
        // Observe local favorites and keep trendingItems' isFavorite in sync
        viewModelScope.launch {
            FavoritesDataStore.favoritesFlow(getApplication()).collect { favs ->
                _uiState.update { state ->
                    val updated = state.trendingItems.map { item ->
                        if (favs.contains(item.id)) item.copy(isFavorite = true) else item.copy(isFavorite = false)
                    }
                    state.copy(trendingItems = updated)
                }
                android.util.Log.d("HomeViewModel", "favoritesFlow collected, applied to trendingItems: $favs")
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // Tai du lieu tu server
     fun loadData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (_uiState.value.trendingItems.isNotEmpty() && !forceRefresh) return@launch

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
                    android.util.Log.d("HomeViewModel", "loadData: savedFavs=$savedFavs")
                    // debug toast removed: rely on logs instead
                    android.util.Log.d("HomeViewModel", "loadData: loaded favorites size=${savedFavs.size}")

                    val productMap = products.map { p ->
                        Product(
                            id = p.id.toString(),
                            name = p.name,
                            price = p.basePrice.toLong(),
                            description = p.description.orEmpty(),
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
        // save original state to revert on error
        val originalState = _uiState.value
        
        // toggle isFavorite immediately in trendingItems for instant UI feedback
        _uiState.update { state ->
            val updated = state.trendingItems.map { item ->
                if (item.id == productId) item.copy(isFavorite = !item.isFavorite) else item
            }
            state.copy(trendingItems = updated, loadingFavorites = state.loadingFavorites + productId)
        }
        android.util.Log.d("HomeViewModel", "toggleFavorite START: productId=$productId, originalIsFavorite=${originalState.trendingItems.find { it.id == productId }?.isFavorite}")

        var hadError = false
        viewModelScope.launch {
            val idLong = productId.toLongOrNull()
            if (idLong == null) {
                _uiState.update { it.copy(error = "Invalid product id") }
                hadError = true
            }

            try {
                FavoritesDataStore.toggleFavorite(getApplication(), productId)
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "toggleFavorite: FavoritesDataStore.toggleFavorite failed for productId=$productId", e)
                hadError = true
                _uiState.update { it.copy(error = e.getErrorMessage()) }
            }

            // read back local favorites after write to observe key and set
            try {
                val localFavs = FavoritesDataStore.favoritesFlow(getApplication()).first()
                android.util.Log.d("HomeViewModel", "toggleFavorite: local favorites after write for productId=$productId => $localFavs")
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "toggleFavorite: reading local favorites failed", e)
            }

            if (idLong != null) {
                try {
                    val resp = NetworkClient.api.toggleFavorite(idLong)
                    resp.result?.let { favStatus ->
                        // trust API response as source of truth
                        _uiState.update { state ->
                            val synced = state.trendingItems.map { item ->
                                if (item.id == favStatus.productId.toString()) item.copy(isFavorite = favStatus.favorited)
                                else item
                            }
                            state.copy(trendingItems = synced)
                        }
                        hadError = false
                    }
                } catch (e: Exception) {
                    hadError = true
                    _uiState.update { it.copy(error = e.getErrorMessage()) }
                }
            }

            // always run cleanup to remove loading state and revert on error
            if (hadError) {
                _uiState.update { it.copy(
                    trendingItems = originalState.trendingItems,
                    loadingFavorites = it.loadingFavorites - productId
                ) }
            } else {
                _uiState.update { it.copy(loadingFavorites = it.loadingFavorites - productId) }
            }
        }
    }



    private val _flyAnimationEvent = MutableSharedFlow<Pair<String, Offset>>()
    val flyAnimationEvent = _flyAnimationEvent.asSharedFlow()

    fun addToCart(productId: String, offset: Offset) {
        viewModelScope.launch {
            addToCartById(productId)
            _flyAnimationEvent.emit(Pair(productId, offset))
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            try {
                CartDataStore.addProduct(getApplication(), product)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.getErrorMessage()) }
            }
        }
    }

    fun addToCartById(productId: String) {
        val product = _uiState.value.trendingItems.find { it.id == productId } ?: return
        addToCart(product)
    }

    var isShowSheet by mutableStateOf(false)
    private set
    var selectedProductId by mutableStateOf<String?>(null)
    private set

    fun onDismiss() {
        selectedProductId = null
        isShowSheet = false
    }

    fun showProduct(product: Product) {
        selectedProductId = product.id
        isShowSheet = true
    }
}
