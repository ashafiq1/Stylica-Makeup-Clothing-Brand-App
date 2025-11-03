package repository

import data.AppDatabase
import model.Product

class ProductRepository(private val database: AppDatabase) {
    
    suspend fun insertProduct(product: Product) {
        database.productDao().insertProduct(product)
    }
    
    suspend fun updateProduct(product: Product) {
        database.productDao().updateProduct(product)
    }
    
    suspend fun deleteProduct(product: Product) {
        database.productDao().deleteProduct(product)
    }
    
    suspend fun getAllProducts(): List<Product> {
        return database.productDao().getAllProducts()
    }
    
    suspend fun searchByName(name: String): List<Product> {
        return database.productDao().searchByName("%$name%")
    }
    
    suspend fun searchByCategory(category: String): List<Product> {
        return database.productDao().searchByCategory(category)
    }
}
