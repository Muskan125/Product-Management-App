package com.practice.trialapp.domain.model

data class Product(
    val pId: String,
    val name: String,
    val quantity: Int,
    val cost: Double
) {
    val totalPrice: Double
        get() = cost * quantity
}