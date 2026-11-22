package com.practice.trialapp.domain.repository

import com.practice.trialapp.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun observeProducts(): Flow<List<Product>>

    // writes go to the server only, they'll throw exc if there is no internet
    suspend fun addProduct(product: Product)

    suspend fun updateProduct(product: Product)

    suspend fun deleteProduct(pId: String)
}
