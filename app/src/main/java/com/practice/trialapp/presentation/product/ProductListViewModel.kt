package com.practice.trialapp.presentation.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practice.trialapp.domain.model.Product
import com.practice.trialapp.di.DefaultDispatcher
import com.practice.trialapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val sortOption = MutableStateFlow(SortOption.NAME_ASC)

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            combine(repository.observeProducts(), sortOption) { products, sort ->
                ProductListData(
                    products = sortProducts(products, sort),
                    sortOption = sort,
                    totalProducts = products.size,
                    totalPrice = products.sumOf { it.totalPrice }
                )
            }
                // sorting + totals run on background thread, only collect{} is om main
                .flowOn(defaultDispatcher)
                .catch { e ->
                    Log.e(TAG, "observeProducts failed", e)
                    _uiState.update {
                        it.copy(isLoading = false, message = "Couldn't load products. ${e.message.orEmpty()}")
                    }
                }
                .collect { data ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            products = data.products,
                            sortOption = data.sortOption,
                            totalProducts = data.totalProducts,
                            totalPrice = data.totalPrice
                        )
                    }
                }
        }
    }

    private fun sortProducts(list: List<Product>, sort: SortOption): List<Product> {
        return when (sort) {
            SortOption.NAME_ASC -> list.sortedBy { it.name.lowercase() }
            SortOption.NAME_DESC -> list.sortedByDescending { it.name.lowercase() }
            SortOption.PRICE_ASC -> list.sortedBy { it.totalPrice }
            SortOption.PRICE_DESC -> list.sortedByDescending { it.totalPrice }
        }
    }

    fun onSortChange(option: SortOption) {
        sortOption.value = option
    }

    fun onAddClick() {
        _uiState.update { it.copy(showAddEditDialog = true, selectedProduct = null) }
    }

    fun onEditClick(product: Product) {
        _uiState.update { it.copy(showAddEditDialog = true, selectedProduct = product) }
    }

    fun onDialogDismiss() {
        if (_uiState.value.isSaving) return
        _uiState.update { it.copy(showAddEditDialog = false, selectedProduct = null) }
    }

    fun saveProduct(name: String, cost: Double, quantity: Int) {
        if (name.isBlank() || cost <= 0 || quantity <= 0) {
            _uiState.update { it.copy(message = "Please enter valid product details") }
            return
        }

        val editing = _uiState.value.selectedProduct
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                if (editing == null) {
                    repository.addProduct(
                        Product(pId = "", name = name.trim(), quantity = quantity, cost = cost)
                    )
                } else {
                    repository.updateProduct(
                        editing.copy(name = name.trim(), quantity = quantity, cost = cost)
                    )
                }
                _uiState.update {
                    it.copy(isSaving = false, showAddEditDialog = false, selectedProduct = null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "saveProduct failed", e)
                _uiState.update {
                    it.copy(isSaving = false, message = errorText(e, "Failed to save product"))
                }
            }
        }
    }

    fun onDeleteClick(product: Product) {
        _uiState.update { it.copy(productToDelete = product) }
    }

    fun onDeleteDismiss() {
        _uiState.update { it.copy(productToDelete = null) }
    }

    fun confirmDelete() {
        val product = _uiState.value.productToDelete ?: return
        _uiState.update { it.copy(productToDelete = null) }

        viewModelScope.launch {
            try {
                repository.deleteProduct(product.pId)
            } catch (e: Exception) {
                Log.e(TAG, "deleteProduct failed", e)
                _uiState.update { it.copy(message = errorText(e, "Failed to delete ${product.name}")) }
            }
        }
    }

    // no internet gets its own msg, everything else has the common msg
    private fun errorText(e: Exception, fallback: String): String {
        return if (e is IOException) "${e.message}. Please try again when you're online" else fallback
    }

    fun onMessageShown() {
        _uiState.update { it.copy(message = null) }
    }

    companion object {
        private const val TAG = "ProductListVM"
    }
}
