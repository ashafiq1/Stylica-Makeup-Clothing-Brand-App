package com.stylica.makeupclothing.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stylica.makeupclothing.R
import com.stylica.makeupclothing.adapter.AdminProductAdapter
import com.stylica.makeupclothing.utils.Constants
import com.stylica.makeupclothing.utils.DatabaseProvider
import kotlinx.coroutines.launch
import com.stylica.makeupclothing.model.Product
import java.text.SimpleDateFormat
import java.util.*

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adminProductAdapter: AdminProductAdapter
    private val products = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        recyclerView = findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adminProductAdapter = AdminProductAdapter(products) { product ->
            confirmDeleteProduct(product)
        }
        recyclerView.adapter = adminProductAdapter

        findViewById<Button>(R.id.buttonAddProduct).setOnClickListener {
            showAddProductDialog()
        }

        findViewById<Button>(R.id.buttonViewUsers).setOnClickListener {
            showUsersDialog()
        }

        findViewById<Button>(R.id.buttonViewOrders).setOnClickListener {
            showOrdersDialog()
        }

        loadProducts()
    }

    private fun showAddProductDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val editName = dialogView.findViewById<EditText>(R.id.editTextProductName)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerProductCategory)
        val editImageUrl = dialogView.findViewById<EditText>(R.id.editTextProductImageUrl)
        val editPrice = dialogView.findViewById<EditText>(R.id.editTextProductPrice)
        val editDescription = dialogView.findViewById<EditText>(R.id.editTextProductDescription)

        val categories = arrayOf(
            Constants.CATEGORY_MAKEUP,
            Constants.CATEGORY_CLOTHING,
            Constants.CATEGORY_ACCESSORIES
        )
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        AlertDialog.Builder(this)
            .setTitle("Add New Product")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = editName.text.toString().trim()
                val category = spinnerCategory.selectedItem.toString()
                val imageUrl = editImageUrl.text.toString().trim()
                val priceStr = editPrice.text.toString().trim()
                val description = editDescription.text.toString().trim()

                if (name.isEmpty() || priceStr.isEmpty()) {
                    Toast.makeText(this, "Name and price are required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val price = priceStr.toDoubleOrNull()
                if (price == null || price <= 0) {
                    Toast.makeText(this, "Enter a valid price", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val product = Product(
                    name = name,
                    category = category,
                    subcategory = null,
                    price = price,
                    description = description.ifEmpty { null },
                    imageUrl = imageUrl.ifEmpty { null },
                    registrationDate = getCurrentDate(),
                    approved = true,
                    vendorId = null
                )
                addProduct(product)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addProduct(product: Product) {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(applicationContext)
                database.productDao().insertProduct(product)
                Toast.makeText(this@AdminDashboardActivity, "Product added!", Toast.LENGTH_SHORT).show()
                loadProducts()
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Failed to add product", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmDeleteProduct(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete '${product.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                deleteProduct(product)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProduct(product: Product) {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(applicationContext)
                database.productDao().deleteProduct(product)
                Toast.makeText(this@AdminDashboardActivity, "Product deleted", Toast.LENGTH_SHORT).show()
                loadProducts()
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Failed to delete product", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(applicationContext)
                val allProducts = database.productDao().getAllProducts()
                adminProductAdapter.updateProducts(allProducts)
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Failed to load products", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showUsersDialog() {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(applicationContext)
                val users = database.userDao().getAllUsers()
                if (users.isEmpty()) {
                    Toast.makeText(this@AdminDashboardActivity, "No users found", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val userList = users.map { "${it.name} (${it.contact}) — ${it.role}" }.toTypedArray()
                AlertDialog.Builder(this@AdminDashboardActivity)
                    .setTitle("All Users (${users.size})")
                    .setItems(userList, null)
                    .setPositiveButton("Close", null)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Failed to load users", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showOrdersDialog() {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(applicationContext)
                val orders = database.orderDao().getAllOrders()
                if (orders.isEmpty()) {
                    Toast.makeText(this@AdminDashboardActivity, "No orders yet", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val orderList = orders.map {
                    "Order #${it.id} | User: ${it.userId} | Qty: ${it.quantity} | ${it.status}"
                }.toTypedArray()
                AlertDialog.Builder(this@AdminDashboardActivity)
                    .setTitle("All Orders (${orders.size})")
                    .setItems(orderList, null)
                    .setPositiveButton("Close", null)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Failed to load orders", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }
}
