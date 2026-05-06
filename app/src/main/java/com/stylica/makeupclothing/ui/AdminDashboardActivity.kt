package com.stylica.makeupclothing.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stylica.makeupclothing.R
import com.stylica.makeupclothing.adapter.AdminProductAdapter
import com.stylica.makeupclothing.utils.Constants
import com.stylica.makeupclothing.utils.DatabaseProvider
import com.stylica.makeupclothing.utils.SessionManager
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
        recyclerView.isNestedScrollingEnabled = false

        adminProductAdapter = AdminProductAdapter(products) { product ->
            confirmDeleteProduct(product)
        }
        recyclerView.adapter = adminProductAdapter

        findViewById<Button>(R.id.buttonAddProduct).setOnClickListener { showAddProductDialog() }
        findViewById<Button>(R.id.buttonViewUsers).setOnClickListener { showUsersDialog() }
        findViewById<Button>(R.id.buttonViewOrders).setOnClickListener { showOrdersDialog() }
        findViewById<Button>(R.id.buttonAdminLogout).setOnClickListener { logout() }

        // Clickable stat cards
        val scrollView = findViewById<ScrollView>(R.id.adminScrollView)
        val productsHeader = findViewById<TextView>(R.id.textProductsHeader)

        findViewById<CardView>(R.id.cardProducts).setOnClickListener {
            scrollView.post { scrollView.smoothScrollTo(0, productsHeader.top) }
        }
        findViewById<CardView>(R.id.cardRevenue).setOnClickListener { showRevenueDialog() }
        findViewById<CardView>(R.id.cardUsers).setOnClickListener { showUsersDialog() }
        findViewById<CardView>(R.id.cardOrders).setOnClickListener { showOrdersDialog() }
        findViewById<CardView>(R.id.cardModerators).setOnClickListener { showModeratorsDialog() }
        findViewById<CardView>(R.id.cardCouriers).setOnClickListener { showCouriersDialog() }

        loadDashboard()
    }

    private fun loadDashboard() {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(applicationContext)

                val allProducts = database.productDao().getAllProducts()
                val allUsers = database.userDao().getAllUsers()
                val allOrders = database.orderDao().getAllOrders()
                val moderators = allUsers.filter { it.role == Constants.ROLE_MODERATOR }

                // Update stat cards
                findViewById<TextView>(R.id.textViewTotalProducts).text = allProducts.size.toString()
                findViewById<TextView>(R.id.textViewTotalUsers).text = allUsers.size.toString()
                findViewById<TextView>(R.id.textViewTotalOrders).text = allOrders.size.toString()
                findViewById<TextView>(R.id.textViewTotalModerators).text = moderators.size.toString()
                findViewById<TextView>(R.id.textViewCouriers).text = "4" // DHL, FedEx, UPS, Local

                // Revenue this month
                val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
                val thisMonthOrders = allOrders.filter { it.orderDate.startsWith(currentMonth) }
                var revenue = 0.0
                thisMonthOrders.forEach { order ->
                    val product = allProducts.find { it.id == order.productId }
                    revenue += (product?.price ?: 0.0) * order.quantity
                }
                findViewById<TextView>(R.id.textViewTotalRevenue).text = "Rs ${revenue.toLong()}"

                // Payment summary
                val cashCount = allOrders.count { it.paymentMode == Constants.PAYMENT_MODE_CASH }
                val cardCount = allOrders.count { it.paymentMode == Constants.PAYMENT_MODE_CARD }
                val onlineCount = allOrders.count { it.paymentMode == Constants.PAYMENT_MODE_ONLINE }
                val pendingOrders = allOrders.count { it.status == Constants.ORDER_STATUS_PENDING }
                val deliveredOrders = allOrders.count { it.status == Constants.ORDER_STATUS_DELIVERED }
                findViewById<TextView>(R.id.textViewPaymentSummary).text =
                    "Cash: $cashCount  |  Card: $cardCount  |  Online: $onlineCount\n" +
                    "Pending Orders: $pendingOrders  |  Delivered: $deliveredOrders"

                // Product list
                adminProductAdapter.updateProducts(allProducts)

            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Failed to load dashboard", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddProductDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val editName = dialogView.findViewById<EditText>(R.id.editTextProductName)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerProductCategory)
        val editImageUrl = dialogView.findViewById<EditText>(R.id.editTextProductImageUrl)
        val editPrice = dialogView.findViewById<EditText>(R.id.editTextProductPrice)
        val editDescription = dialogView.findViewById<EditText>(R.id.editTextProductDescription)

        val categories = arrayOf(
            Constants.CATEGORY_MAKEUP, Constants.CATEGORY_CLOTHING,
            Constants.CATEGORY_ACCESSORIES, Constants.CATEGORY_SHOES
        )
        spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

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
                    name = name, category = category, subcategory = null, price = price,
                    description = description.ifEmpty { null }, imageUrl = imageUrl.ifEmpty { null },
                    registrationDate = getCurrentDate(), approved = true, vendorId = null
                )
                addProduct(product)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addProduct(product: Product) {
        lifecycleScope.launch {
            try {
                DatabaseProvider.getDatabase(applicationContext).productDao().insertProduct(product)
                Toast.makeText(this@AdminDashboardActivity, "Product added!", Toast.LENGTH_SHORT).show()
                loadDashboard()
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Failed to add product", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmDeleteProduct(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Delete '${product.name}'?")
            .setPositiveButton("Delete") { _, _ -> deleteProduct(product) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProduct(product: Product) {
        lifecycleScope.launch {
            try {
                DatabaseProvider.getDatabase(applicationContext).productDao().deleteProduct(product)
                Toast.makeText(this@AdminDashboardActivity, "Product deleted", Toast.LENGTH_SHORT).show()
                loadDashboard()
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Failed to delete product", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRevenueDialog() {
        lifecycleScope.launch {
            try {
                val db = DatabaseProvider.getDatabase(applicationContext)
                val orders = db.orderDao().getAllOrders()
                val products = db.productDao().getAllProducts()
                val productMap = products.associateBy { it.id }

                val delivered = orders.filter { it.status == Constants.ORDER_STATUS_DELIVERED }
                val confirmed = orders.filter { it.status == "confirmed" }
                val pending = orders.filter { it.status == Constants.ORDER_STATUS_PENDING }

                fun revenue(list: List<com.stylica.makeupclothing.model.Order>) =
                    list.sumOf { (productMap[it.productId]?.price ?: 0.0) * it.quantity }.toLong()

                val msg = "Delivered Orders: ${delivered.size}\nRevenue Earned: Rs ${revenue(delivered)}\n\n" +
                          "Confirmed Orders: ${confirmed.size}\nExpected: Rs ${revenue(confirmed)}\n\n" +
                          "Pending Orders: ${pending.size}\nPotential: Rs ${revenue(pending)}"

                AlertDialog.Builder(this@AdminDashboardActivity)
                    .setTitle("Revenue Breakdown")
                    .setMessage(msg)
                    .setPositiveButton("Close", null)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Failed to load revenue", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showModeratorsDialog() {
        lifecycleScope.launch {
            try {
                val users = DatabaseProvider.getDatabase(applicationContext).userDao().getAllUsers()
                val moderators = users.filter { it.role == Constants.ROLE_MODERATOR }
                if (moderators.isEmpty()) {
                    Toast.makeText(this@AdminDashboardActivity, "No moderators found", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val list = moderators.map { "👤  ${it.name}  |  @${it.contact}  |  ${it.registrationDate.take(10)}" }.toTypedArray()
                AlertDialog.Builder(this@AdminDashboardActivity)
                    .setTitle("Moderators (${moderators.size})")
                    .setItems(list, null)
                    .setPositiveButton("Close", null)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Failed to load moderators", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCouriersDialog() {
        val couriers = arrayOf(
            "🚚  TCS  —  Cash on Delivery, Express",
            "🚚  Leopards  —  Same Day, Overnight",
            "🚚  DHL  —  International, Tracked",
            "🚚  PostEx  —  COD Specialist, Pakistan-wide"
        )
        AlertDialog.Builder(this)
            .setTitle("Courier Partners (4)")
            .setItems(couriers, null)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showUsersDialog() {
        lifecycleScope.launch {
            try {
                val users = DatabaseProvider.getDatabase(applicationContext).userDao().getAllUsers()
                if (users.isEmpty()) { Toast.makeText(this@AdminDashboardActivity, "No users found", Toast.LENGTH_SHORT).show(); return@launch }
                val list = users.map { "${it.name}  |  ${it.contact}  |  ${it.role.uppercase()}" }.toTypedArray()
                AlertDialog.Builder(this@AdminDashboardActivity)
                    .setTitle("All Users (${users.size})")
                    .setItems(list, null)
                    .setPositiveButton("Close", null).show()
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Failed to load users", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showOrdersDialog() {
        lifecycleScope.launch {
            try {
                val orders = DatabaseProvider.getDatabase(applicationContext).orderDao().getAllOrders()
                if (orders.isEmpty()) { Toast.makeText(this@AdminDashboardActivity, "No orders yet", Toast.LENGTH_SHORT).show(); return@launch }
                val list = orders.map {
                    "Order #${it.id}  |  User: ${it.userId}  |  Qty: ${it.quantity}  |  ${it.status.uppercase()}  |  ${it.courier}"
                }.toTypedArray()
                AlertDialog.Builder(this@AdminDashboardActivity)
                    .setTitle("All Orders (${orders.size})")
                    .setItems(list, null)
                    .setPositiveButton("Close", null).show()
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Failed to load orders", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logout() {
        SessionManager(this).logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun getCurrentDate(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
}
