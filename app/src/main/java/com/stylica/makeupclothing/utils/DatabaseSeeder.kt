package com.stylica.makeupclothing.utils

import android.content.Context
import com.stylica.makeupclothing.data.AppDatabase
import com.stylica.makeupclothing.model.Product
import com.stylica.makeupclothing.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

object DatabaseSeeder {
    
    fun seedDatabase(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = DatabaseProvider.getDatabase(context)
            
            // Check if already seeded
            val existingProducts = database.productDao().getAllProducts()
            if (existingProducts.isNotEmpty()) {
                return@launch // Already seeded
            }
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            
            // Create Admin User
            val adminSalt = PasswordUtils.generateSalt()
            val adminPasswordHash = PasswordUtils.hashPassword("admin123", adminSalt)
            val adminUser = User(
                id = 0,
                name = "Admin User",
                gender = "Male",
                contact = "admin",
                registrationDate = currentDate,
                role = Constants.ROLE_ADMIN,
                passwordHash = adminPasswordHash,
                passwordSalt = adminSalt
            )
            
            // Create Moderator User
            val modSalt = PasswordUtils.generateSalt()
            val modPasswordHash = PasswordUtils.hashPassword("mod123", modSalt)
            val moderatorUser = User(
                id = 0,
                name = "Moderator",
                gender = "Female",
                contact = "moderator",
                registrationDate = currentDate,
                role = Constants.ROLE_MODERATOR,
                passwordHash = modPasswordHash,
                passwordSalt = modSalt
            )
            
            // Create Regular User
            val userSalt = PasswordUtils.generateSalt()
            val userPasswordHash = PasswordUtils.hashPassword("user123", userSalt)
            val regularUser = User(
                id = 0,
                name = "Demo User",
                gender = "Male",
                contact = "user",
                registrationDate = currentDate,
                role = Constants.ROLE_USER,
                passwordHash = userPasswordHash,
                passwordSalt = userSalt
            )
            
            // Insert users
            database.userDao().insertUser(adminUser)
            database.userDao().insertUser(moderatorUser)
            database.userDao().insertUser(regularUser)
            
            // Create Dummy Products - Makeup Category
            val makeupProducts = listOf(
                Product(
                    id = 0,
                    name = "Matte Red Lipstick",
                    category = "makeup",
                    subcategory = "lips",
                    price = 299.0,
                    description = "Long-lasting matte finish red lipstick. Perfect for all occasions.",
                    imageUrl = "https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=300",
                    registrationDate = currentDate,
                    approved = true,
                    vendorId = 1
                ),
                Product(
                    id = 0,
                    name = "Nude Lipstick",
                    category = "makeup",
                    subcategory = "lips",
                    price = 349.0,
                    description = "Natural nude lipstick for everyday wear.",
                    imageUrl = "https://images.unsplash.com/photo-1631214524020-7e18db9a8f92?w=300",
                    registrationDate = currentDate,
                    approved = true,
                    vendorId = 1
                ),
                Product(
                    id = 0,
                    name = "Eyeshadow Palette",
                    category = "makeup",
                    subcategory = "eyes",
                    price = 899.0,
                    description = "12 stunning shades eyeshadow palette with matte and shimmer finishes.",
                    imageUrl = "https://images.unsplash.com/photo-1512496015851-a90fb38ba796?w=300",
                    registrationDate = currentDate,
                    approved = true,
                    vendorId = 1
                ),
                Product(
                    id = 0,
                    name = "Mascara - Volumizing",
                    category = "makeup",
                    subcategory = "eyes",
                    price = 499.0,
                    description = "Volumizing mascara for dramatic lashes. Waterproof formula.",
                    imageUrl = "https://images.unsplash.com/photo-1631730486572-226d1f595b68?w=300",
                    registrationDate = currentDate,
                    approved = true,
                    vendorId = 1
                ),
                Product(
                    id = 0,
                    name = "Foundation - Beige",
                    category = "makeup",
                    subcategory = "face",
                    price = 799.0,
                    description = "Full coverage foundation with SPF 30. Perfect match for medium skin tones.",
                    imageUrl = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=300",
                    registrationDate = currentDate,
                    approved = true,
                    vendorId = 1
                ),
                Product(
                    id = 0,
                    name = "Blush - Pink Glow",
                    category = "makeup",
                    subcategory = "face",
                    price = 399.0,
                    description = "Natural pink blush for a healthy glow. Blendable and long-lasting.",
                    imageUrl = "https://images.unsplash.com/photo-1596704017254-9b121068fe58?w=300",
                    registrationDate = currentDate,
                    approved = true,
                    vendorId = 1
                )
            )
            
            // Create Dummy Products - Clothing Category
            val clothingProducts = listOf(
                Product(
                    id = 0,
                    name = "Floral Summer Dress",
                    category = "clothing",
                    subcategory = "dresses",
                    price = 1999.0,
                    description = "Beautiful floral print summer dress. Light and comfortable fabric.",
                    imageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=300",
                    registrationDate = currentDate,
                    approved = true,
                    vendorId = 1
                ),
                Product(
                    id = 0,
                    name = "Denim Jacket",
                    category = "clothing",
                    subcategory = "jackets",
                    price = 2499.0,
                    description = "Classic denim jacket. Perfect for casual outings.",
                    imageUrl = "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=300",
                    registrationDate = currentDate,
                    approved = true,
                    vendorId = 1
                ),
                Product(
                    id = 0,
                    name = "White Cotton T-Shirt",
                    category = "clothing",
                    subcategory = "tops",
                    price = 599.0,
                    description = "Basic white cotton t-shirt. Comfortable and breathable.",
                    imageUrl = "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=300",
                    registrationDate = currentDate,
                    approved = true,
                    vendorId = 1
                ),
                Product(
                    id = 0,
                    name = "Black Skinny Jeans",
                    category = "clothing",
                    subcategory = "jeans",
                    price = 1499.0,
                    description = "Stretchy black skinny jeans. Perfect fit and comfortable.",
                    imageUrl = "https://images.unsplash.com/photo-1542272604-787c3835535d?w=300",
                    registrationDate = currentDate,
                    approved = true,
                    vendorId = 1
                )
            )
            
            // Create Dummy Products - Accessories Category
            val accessoryProducts = listOf(
                Product(
                    id = 0,
                    name = "Gold Hoop Earrings",
                    category = "accessories",
                    subcategory = "jewelry",
                    price = 899.0,
                    description = "Elegant gold-plated hoop earrings. Hypoallergenic.",
                    imageUrl = "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=300",
                    registrationDate = currentDate,
                    approved = true,
                    vendorId = 1
                ),
                Product(
                    id = 0,
                    name = "Designer Sunglasses",
                    category = "accessories",
                    subcategory = "eyewear",
                    price = 1299.0,
                    description = "Stylish designer sunglasses with UV protection.",
                    imageUrl = "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=300",
                    registrationDate = currentDate,
                    approved = true,
                    vendorId = 1
                ),
                Product(
                    id = 0,
                    name = "Leather Handbag",
                    category = "accessories",
                    subcategory = "bags",
                    price = 3499.0,
                    description = "Premium leather handbag. Spacious with multiple compartments.",
                    imageUrl = "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=300",
                    registrationDate = currentDate,
                    approved = true,
                    vendorId = 1
                ),
                Product(
                    id = 0,
                    name = "Silk Scarf",
                    category = "accessories",
                    subcategory = "scarves",
                    price = 799.0,
                    description = "Luxurious silk scarf with beautiful patterns.",
                    imageUrl = "https://images.unsplash.com/photo-1601924357840-3e0d38e08851?w=300",
                    registrationDate = currentDate,
                    approved = true,
                    vendorId = 1
                )
            )
            
            // Insert all products
            (makeupProducts + clothingProducts + accessoryProducts).forEach { product ->
                database.productDao().insertProduct(product)
            }
        }
    }
}
