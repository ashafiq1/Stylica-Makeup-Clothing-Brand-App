package com.stylica.makeupclothing.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Int,
    val amount: Double,
    val paymentMode: String, // "cash", "card", "online"
    val paymentDate: String,
    val status: String // "pending", "completed", "failed"
)
