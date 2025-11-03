package model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val subcategory: String?,
    val price: Double,
    val description: String?,
    val imageUrl: String?,
    val registrationDate: String,
    val approved: Boolean = false,
    val vendorId: Int?
)
