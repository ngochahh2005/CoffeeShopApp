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
import com.example.coffeeshopapp.data.model.dto.FavoriteDto
import com.example.coffeeshopapp.data.model.dto.ProductDto
import com.example.coffeeshopapp.data.model.entity.Category
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.data.remote.NetworkClient
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
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import com.example.coffeeshopapp.data.TokenProvider
import com.example.coffeeshopapp.utils.getErrorMessage
import kotlin.math.ln

data class HomeUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val trendingItems: List<Product> = emptyList(),
    val favoriteItems: List<Product> = emptyList(),
    val loadingFavorites: Set<String> = emptySet(),
    val allProduct: List<Product> = emptyList(),
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

    val filteredProducts: StateFlow<List<Product>> = _uiState
        .combine(snapshotFlow { searchKeyWords }) { state, query ->
            if (query.isBlank()) {
                state.allProduct
            } else {
                state.allProduct.filter { it.name.contains(query, ignoreCase = true) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            AuthDataStore.userIdFlow(getApplication()).collect {
                loadData(forceRefresh = true)
            }
        }

        loadData()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun loadData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (_uiState.value.trendingItems.isNotEmpty() && !forceRefresh) return@launch

            _uiState.update { it.copy(isLoading = true) }

            try {
                val productResponse = NetworkClient.api.getProduct()
                if (productResponse.result != null) {
                    val products = productResponse.result
                    val hasToken = !TokenProvider.token.isNullOrBlank()
                    val serverFavorites = if (hasToken) {
                        try {
                            fetchFavoriteProductsFromServer()
                        } catch (ex: Exception) {
                            null
                        }
                    } else {
                        null
                    }
                    val favoriteIds = serverFavorites
                        ?.map { it.id }
                        ?.toSet()
                        ?: FavoritesDataStore.favoritesFlow(getApplication()).first()

                    val allProduct = products.map { product ->
                        product.toProduct(isFavorite = favoriteIds.contains(product.id.toString()))
                    }

                    val finalAllProduct = allProduct.withTrendingFlags()
                    val trendingIds = finalAllProduct.filter { it.isTrending }.map { it.id }.toSet()

                    val allCategory = try {
                        val catResp = NetworkClient.api.getCategories()
                        catResp.result?.map { category ->
                            Category(
                                id = category.id,
                                name = category.name,
                                imageUrl = category.imageUrl?.toFullImageUrl(),
                                displayOrder = category.displayOrder,
                                isActive = category.isActiveResolved()
                            )
                        } ?: coffeeCategories
                    } catch (ex: Exception) {
                        coffeeCategories
                    }

                    _uiState.update {
                        it.copy(
                            categories = allCategory,
                            trendingItems = finalAllProduct.filter { trendingIds.contains(it.id) },
                            allProduct = finalAllProduct,
                            favoriteItems = finalAllProduct.favoriteItems(favoriteIds, serverFavorites),
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    delay(1000)
                    _uiState.update {
                        it.copy(
                            categories = emptyList(),
                            trendingItems = emptyList(),
                            favoriteItems = emptyList(),
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
        val idLong = productId.toLongOrNull()
        if (idLong == null) {
            _uiState.update { it.copy(error = "Invalid product id") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loadingFavorites = it.loadingFavorites + productId) }
            try {
                val response = NetworkClient.api.toggleFavorite(idLong)
                if (response.result == null) {
                    _uiState.update { it.copy(error = response.message ?: "Không cập nhật được yêu thích") }
                } else {
                    syncFavoritesFromServer()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.getErrorMessage()) }
            } finally {
                _uiState.update { it.copy(loadingFavorites = it.loadingFavorites - productId) }
            }
        }
    }

    fun refreshProductsAfterReview() {
        loadData(forceRefresh = true)
    }

    private suspend fun syncFavoritesFromServer() {
        val favoriteProducts = fetchFavoriteProductsFromServer()
        val favoriteIds = favoriteProducts.map { it.id }.toSet()

        _uiState.update { state ->
            val favoriteItems = state.allProduct.favoriteItems(favoriteIds, favoriteProducts)
            state.copy(
                favoriteItems = favoriteItems,
                allProduct = state.allProduct.map { product ->
                    product.copy(isFavorite = favoriteIds.contains(product.id))
                },
                trendingItems = state.trendingItems.map { product ->
                    product.copy(isFavorite = favoriteIds.contains(product.id))
                }
            )
        }
    }

    private suspend fun fetchFavoriteProductsFromServer(): List<Product> {
        val response = NetworkClient.api.getFavorites()
        val favorites = response.result ?: emptyList()
        FavoritesDataStore.setFavorites(
            context = getApplication(),
            ids = favorites.map { it.productId.toString() }.toSet()
        )
        val currentTrendingIds = _uiState.value.trendingItems.map { it.id }.toSet()
        return favorites.map { favorite -> favorite.toProduct().copy(isTrending = currentTrendingIds.contains(favorite.productId.toString())) }
    }

    private fun FavoriteDto.toProduct(): Product {
        return Product(
            id = productId.toString(),
            name = productName,
            price = basePrice.toLong(),
            imageUrl = imageUrl.toFullImageUrl(),
            isFavorite = true
        )
    }

    private fun ProductDto.toProduct(isFavorite: Boolean): Product {
        return Product(
            id = id.toString(),
            name = name,
            price = basePrice.toLong(),
            description = description.orEmpty(),
            imageUrl = imageUrl.toFullImageUrl(),
            categoryId = categoryId,
            sizes = size,
            rating = rating ?: 0.0,
            reviewers = reviewers ?: 0,
            isFavorite = isFavorite,
        )
    }

    private fun List<Product>.withTrendingFlags(): List<Product> {
        val trendingIds = sortedByDescending { product ->
            val normalizedRating = product.rating / 5.0
            val popularityScore = ln(product.reviewers.toDouble() + 1.0)
            (normalizedRating * 0.5) + (popularityScore * 0.5)
        }.take(10).map { it.id }.toSet()

        return map { product -> product.copy(isTrending = trendingIds.contains(product.id)) }
    }

    private fun List<Product>.favoriteItems(
        favoriteIds: Set<String>,
        serverFavorites: List<Product>?
    ): List<Product> {
        val productsById = associateBy { it.id }
        val enrichedFavorites = favoriteIds.mapNotNull { id ->
            productsById[id]?.copy(isFavorite = true)
        }
        val missingServerFavorites = serverFavorites.orEmpty()
            .filterNot { favorite -> productsById.containsKey(favorite.id) }
            .map { favorite -> favorite.copy(isFavorite = true) }

        return enrichedFavorites + missingServerFavorites
    }



    private val _flyAnimationEvent = MutableSharedFlow<Pair<String, Offset>>()
    val flyAnimationEvent = _flyAnimationEvent.asSharedFlow()

    fun addToCart(productId: String, offset: Offset) {
        viewModelScope.launch {
            addToCartById(productId)
            _flyAnimationEvent.emit(Pair(productId, offset))
        }
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                CartDataStore.addProduct(getApplication(), product, quantity)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.getErrorMessage()) }
            }
        }
    }

    fun addToCartById(productId: String) {
        val product = _uiState.value.allProduct.find { it.id == productId } ?: return
        
        // Default to first size if product has sizes but none selected
        val productWithDefaultSize = if (product.selectedSizeName == null && product.sizes.isNotEmpty()) {
            val firstSize = product.sizes.minByOrNull { size ->
                when (size.sizeName.uppercase()) {
                    "S" -> 0
                    "M" -> 1
                    "L" -> 2
                    else -> 3
                }
            } ?: product.sizes.first()

            product.copy(
                selectedSizeName = firstSize.sizeName,
                selectedSizePriceExtra = firstSize.priceExtra.toLong()
            )
        } else {
            product
        }
        
        addToCart(productWithDefaultSize)
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
