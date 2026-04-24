package com.example.coffeeshopapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.model.dto.*
import com.example.coffeeshopapp.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.coffeeshopapp.utils.getErrorMessage

data class DashboardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val overview: DashboardOverviewDto? = null,
    val revenuePoints: List<RevenuePointDto> = emptyList(),
    val revenueGroupBy: String = "DAY",
    val topProducts: List<TopProductDto> = emptyList(),
    val recentOrders: List<OrderDto> = emptyList(),
    val recentReviews: List<ReviewDto> = emptyList()
)

class DashboardViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val overviewRes = repository.getOverview()
                val topRes = repository.getTopProducts()
                val ordersRes = repository.getRecentOrders()
                val reviewsRes = repository.getRecentReviews()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        overview = if (isSuccess(overviewRes.code)) overviewRes.result else null,
                        topProducts = if (isSuccess(topRes.code)) topRes.result ?: emptyList() else emptyList(),
                        recentOrders = if (isSuccess(ordersRes.code)) ordersRes.result ?: emptyList() else emptyList(),
                        recentReviews = if (isSuccess(reviewsRes.code)) reviewsRes.result ?: emptyList() else emptyList()
                    )
                }
                loadRevenue()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun switchRevenueGroupBy(groupBy: String) {
        _uiState.update { it.copy(revenueGroupBy = groupBy) }
        loadRevenue()
    }

    private fun loadRevenue() {
        viewModelScope.launch {
            try {
                val groupBy = _uiState.value.revenueGroupBy
                val now = java.time.LocalDate.now()
                val (from, to) = when (groupBy) {
                    "MONTH" -> {
                        // Whole current year
                        val from = now.withDayOfYear(1).toString()
                        val to = now.toString()
                        from to to
                    }
                    else -> {
                        // Current month
                        val from = now.withDayOfMonth(1).toString()
                        val to = now.toString()
                        from to to
                    }
                }
                val res = repository.getRevenue(from, to, groupBy)
                if (isSuccess(res.code)) {
                    _uiState.update { it.copy(revenuePoints = res.result ?: emptyList()) }
                }
            } catch (_: Exception) { }
        }
    }

    private fun isSuccess(code: Int): Boolean = code == 200 || code == 1000 || code == 0
}
