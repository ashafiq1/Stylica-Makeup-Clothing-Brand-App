package data

import androidx.room.*
import model.Order

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("SELECT * FROM orders WHERE userId = :userId")
    suspend fun getOrdersByUser(userId: Int): List<Order>

    @Query("SELECT * FROM orders")
    suspend fun getAllOrders(): List<Order>
}