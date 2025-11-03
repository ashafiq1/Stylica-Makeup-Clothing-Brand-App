package ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import d.R
import kotlinx.coroutines.launch
import model.CartItem
import model.Product
import utils.DatabaseProvider
import java.text.SimpleDateFormat
import java.util.*

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var product: Product
    private var userId: Int = 1 // TODO: Get from session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Get product ID from intent
        val productId = intent.getIntExtra("PRODUCT_ID", -1)
        if (productId == -1) {
            Toast.makeText(this, "Invalid product", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadProductDetails(productId)
    }

    private fun loadProductDetails(productId: Int) {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(applicationContext)
                val products = database.productDao().getAllProducts()
                product = products.find { it.id == productId } ?: run {
                    Toast.makeText(this@ProductDetailActivity, "Product not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                setupUI()
            } catch (e: Exception) {
                Toast.makeText(this@ProductDetailActivity, "Error loading product", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupUI() {
        val imageView = findViewById<ImageView>(R.id.imageViewProductDetail)
        val nameTextView = findViewById<TextView>(R.id.textViewProductName)
        val categoryTextView = findViewById<TextView>(R.id.textViewProductCategory)
        val priceTextView = findViewById<TextView>(R.id.textViewProductPrice)
        val descriptionTextView = findViewById<TextView>(R.id.textViewProductDescription)
        val addToCartButton = findViewById<Button>(R.id.buttonAddToCart)
        val buyNowButton = findViewById<Button>(R.id.buttonBuyNow)

        nameTextView.text = product.name
        categoryTextView.text = "Category: ${product.category}"
        priceTextView.text = "$${product.price}"
        descriptionTextView.text = product.description ?: "No description available"

        // Load image with Glide
        Glide.with(this)
            .load(product.imageUrl)
            .placeholder(R.drawable.ic_menu_gallery)
            .error(R.drawable.ic_menu_gallery)
            .centerCrop()
            .into(imageView)

        addToCartButton.setOnClickListener {
            addToCart()
        }

        buyNowButton.setOnClickListener {
            buyNow()
        }
    }

    private fun addToCart() {
        lifecycleScope.launch {
            try {
                val cartItem = CartItem(
                    userId = userId,
                    productId = product.id,
                    quantity = 1,
                    addedDate = getCurrentDate()
                )

                val database = DatabaseProvider.getDatabase(applicationContext)
                database.cartItemDao().insertCartItem(cartItem)

                Toast.makeText(this@ProductDetailActivity, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ProductDetailActivity, "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buyNow() {
        // TODO: Navigate to checkout
        Toast.makeText(this, "Buy Now - Coming Soon", Toast.LENGTH_SHORT).show()
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
