package com.stylica.makeupclothing.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.stylica.makeupclothing.R
import com.stylica.makeupclothing.data.AppDatabase
import kotlinx.coroutines.launch
import com.stylica.makeupclothing.model.Product
import java.text.SimpleDateFormat
import java.util.*

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private val products = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "stylica_database"
        ).build()

        recyclerView = findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val buttonAddProduct = findViewById<Button>(R.id.buttonAddProduct)
        val buttonViewUsers = findViewById<Button>(R.id.buttonViewUsers)
        val buttonViewOrders = findViewById<Button>(R.id.buttonViewOrders)

        buttonAddProduct.setOnClickListener {
            showAddProductDialog()
        }

        buttonViewUsers.setOnClickListener {
            Toast.makeText(this, "View Users - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        buttonViewOrders.setOnClickListener {
            Toast.makeText(this, "View Orders - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        loadProducts()
    }

    private fun showAddProductDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val editName = dialogView.findViewById<EditText>(R.id.editTextProductName)
        val editCategory = dialogView.findViewById<EditText>(R.id.editTextProductCategory)
        val editPrice = dialogView.findViewById<EditText>(R.id.editTextProductPrice)
        val editDescription = dialogView.findViewById<EditText>(R.id.editTextProductDescription)

        AlertDialog.Builder(this)
            .setTitle("Add Product")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = editName.text.toString()
                val category = editCategory.text.toString()
                val priceStr = editPrice.text.toString()
                val description = editDescription.text.toString()

                if (name.isNotEmpty() && category.isNotEmpty() && priceStr.isNotEmpty()) {
                    val product = Product(
                        name = name,
                        category = category,
                        subcategory = null,
                        price = priceStr.toDouble(),
                        description = description,
                        imageUrl = null,
                        registrationDate = getCurrentDate(),
                        approved = true,
                        vendorId = null
                    )
                    addProduct(product)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addProduct(product: Product) {
        lifecycleScope.launch {
            database.productDao().insertProduct(product)
            Toast.makeText(this@AdminDashboardActivity, "Product added!", Toast.LENGTH_SHORT).show()
            loadProducts()
        }
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            products.clear()
            products.addAll(database.productDao().getAllProducts())
            // TODO: Update RecyclerView adapter
            Toast.makeText(this@AdminDashboardActivity, "Loaded ${products.size} products", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
