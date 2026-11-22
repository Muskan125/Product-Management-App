package com.practice.trialapp.data.repository

import com.practice.trialapp.data.mapper.ProductMapper
import com.practice.trialapp.data.remote.FirestoreDataSource
import com.practice.trialapp.di.DefaultDispatcher
import com.practice.trialapp.di.IoDispatcher
import com.practice.trialapp.domain.model.Product
import com.practice.trialapp.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val dataSource: FirestoreDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ProductRepository {

    override fun observeProducts(): Flow<List<Product>> {
        return dataSource.observeProducts().map { docs ->
            docs.mapNotNull { ProductMapper.fromDocument(it) }
        }.flowOn(defaultDispatcher)
    }

    override suspend fun addProduct(product: Product) = withContext(ioDispatcher) {
        val id = product.pId.ifBlank { dataSource.generateProductId() }
        dataSource.addProduct(id, ProductMapper.toFirestoreMap(product))
    }

    override suspend fun updateProduct(product: Product) = withContext(ioDispatcher) {
        dataSource.updateProduct(product.pId, ProductMapper.toFirestoreMap(product))
    }

    override suspend fun deleteProduct(pId: String) = withContext(ioDispatcher) {
        dataSource.deleteProduct(pId)
    }
}
