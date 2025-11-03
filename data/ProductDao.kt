package data

import androidx.room.*
import model.Product

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * FROM products WHERE name LIKE :name")
    suspend fun searchByName(name: String): List<Product>

    @Query("SELECT * FROM products WHERE category = :category")
    suspend fun searchByCategory(category: String): List<Product>

    @Query("SELECT * FROM products WHERE registrationDate = :date")
    suspend fun searchByRegistrationDate(date: String): List<Product>

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>
}