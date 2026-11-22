package com.practice.trialapp.presentation.product

import com.practice.trialapp.domain.model.Product

enum class SortOption(val label: String) {
    NAME_ASC("Name (A-Z)"),
    NAME_DESC("Name (Z-A)"),
    PRICE_ASC("Price (Low-High)"),
    PRICE_DESC("Price (High-Low)")
}

data class ProductUiState(
    val isLoading: Boolean = true,
    val products: List<Product> = emptyList(),
    val totalProducts: Int = 0,
    val totalPrice: Double = 0.0,
    val sortOption: SortOption = SortOption.NAME_ASC,
    val showAddEditDialog: Boolean = false,
    val selectedProduct: Product? = null,   // if its null -> add mode, else edit mode
    val productToDelete: Product? = null,
    val isSaving: Boolean = false,
    val message: String? = null   // one-time snackbar text (errors / offline info)
)

data class ProductListData(
    val products: List<Product>,
    val sortOption: SortOption,
    val totalProducts: Int,
    val totalPrice: Double
)
