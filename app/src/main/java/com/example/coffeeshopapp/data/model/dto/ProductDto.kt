package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("basePrice") val basePrice: Double,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName(value = "categoryId", alternate = ["category_id"]) val categoryId: Long,
    @SerializedName(value = "isActive", alternate = ["active", "is_active"]) val isActive: Boolean?,
    @SerializedName(value = "isDeleted", alternate = ["deleted", "is_deleted"]) val isDeleted: Boolean? = null,
    @SerializedName(value = "rating", alternate = ["averageRating", "avgRating", "average_rating", "avg_rating"]) val rating: Double? = null,
    @SerializedName(value = "reviewers", alternate = ["reviewCount", "reviewsCount", "totalReviews", "review_count", "reviews_count", "total_reviews"]) val reviewers: Int? = null,
    @SerializedName("sizes") val size: List<ProductSizeDto>
)
