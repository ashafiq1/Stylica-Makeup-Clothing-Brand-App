package com.stylica.makeupclothing.data

import androidx.room.*
import com.stylica.makeupclothing.model.Payment

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment)

    @Update
    suspend fun updatePayment(payment: Payment)

    @Query("SELECT * FROM payments WHERE orderId = :orderId")
    suspend fun getPaymentsByOrder(orderId: Int): List<Payment>

    @Query("SELECT * FROM payments")
    suspend fun getAllPayments(): List<Payment>
}
