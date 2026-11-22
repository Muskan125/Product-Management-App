package com.practice.trialapp.data.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.practice.trialapp.domain.model.Product

object ProductMapper {

    private const val FIELD_NAME = "name"
    private const val FIELD_COST = "cost"
    private const val FIELD_QUANTITY = "quantity"

    // returns null for broken data instead of crashing the whole list
    fun fromDocument(doc: DocumentSnapshot): Product? {
        val name = doc.getString(FIELD_NAME) ?: return null
        val cost = doc.getDouble(FIELD_COST) ?: return null
        val quantity = doc.getLong(FIELD_QUANTITY)?.toInt() ?: return null

        return Product(
            pId = doc.id,
            name = name,
            quantity = quantity,
            cost = cost
        )
    }

    fun toFirestoreMap(product: Product): Map<String, Any> = mapOf(
        FIELD_NAME to product.name,
        FIELD_COST to product.cost,
        FIELD_QUANTITY to product.quantity
    )
}
