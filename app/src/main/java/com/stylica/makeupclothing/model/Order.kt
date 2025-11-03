package com.stylica.makeupclothing.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val productId: Int,
    val quantity: Int,
    val orderDate: String,
    val status: String, // e.g., "pending", "confirmed", "delivered"
    val courier: String?,
    val paymentMode: String?
)
