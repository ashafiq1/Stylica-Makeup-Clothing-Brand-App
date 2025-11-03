package com.stylica.makeupclothing.repository

import com.stylica.makeupclothing.data.AppDatabase
import com.stylica.makeupclothing.model.Order

class OrderRepository(private val database: AppDatabase) {
    
    suspend fun insertOrder(order: Order) {
        database.orderDao().insertOrder(order)
    }
    
    suspend fun updateOrder(order: Order) {
        database.orderDao().updateOrder(order)
    }
    
    suspend fun deleteOrder(order: Order) {
        database.orderDao().deleteOrder(order)
    }
    
    suspend fun getOrdersByUser(userId: Int): List<Order> {
        return database.orderDao().getOrdersByUser(userId)
    }
    
    suspend fun getAllOrders(): List<Order> {
        return database.orderDao().getAllOrders()
    }
}
