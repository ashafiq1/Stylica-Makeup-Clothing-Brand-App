package com.stylica.makeupclothing.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.stylica.makeupclothing.R
import com.stylica.makeupclothing.adapter.OrderAdapter
import com.stylica.makeupclothing.adapter.OrderItem
import com.stylica.makeupclothing.adapter.PendingProductAdapter
import com.stylica.makeupclothing.model.Order
import com.stylica.makeupclothing.model.Product
import com.stylica.makeupclothing.utils.Constants
import com.stylica.makeupclothing.utils.DatabaseProvider
import com.stylica.makeupclothing.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ModeratorActivity : AppCompatActivity() {

    // Tab views
    private lateinit var tabDashboard: TextView
    private lateinit var tabApprovals: TextView
    private lateinit var tabOrders: TextView
    private lateinit var tabAddProduct: TextView

    // Sections
    private lateinit var sectionDashboard: View
    private lateinit var sectionApprovals: LinearLayout
    private lateinit var sectionOrders: LinearLayout
    private lateinit var sectionAddProduct: View

    // Dashboard stats
    private lateinit var textRevenue: TextView
    private lateinit var textTotalProducts: TextView
    private lateinit var textSoldOut: TextView
    private lateinit var textTotalOrders: TextView
    private lateinit var textPendingOrders: TextView
    private lateinit var textConfirmedOrders: TextView
    private lateinit var textDeliveredOrders: TextView
    private lateinit var textHasDescription: TextView
    private lateinit var textHasImage: TextView
    private lateinit var textMissingInfo: TextView

    // Approvals
    private lateinit var textViewPendingCount: TextView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var recyclerViewPending: RecyclerView
    private lateinit var pendingProductAdapter: PendingProductAdapter
    private val pendingProducts = mutableListOf<Product>()

    // Orders
    private lateinit var layoutEmptyOrders: LinearLayout
    private lateinit var recyclerViewOrders: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private val orderItems = mutableListOf<OrderItem>()

    // Add Product
    private lateinit var editAddProductName: TextInputEditText
    private lateinit var spinnerAddProductCategory: Spinner
    private lateinit var editAddProductPrice: TextInputEditText
    private lateinit var editAddProductImageUrl: TextInputEditText
    private lateinit var editAddProductDescription: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moderator)

        bindViews()
        setupAdapters()
        setupTabs()
        setupAddProductForm()

        // Logout
        findViewById<MaterialButton>(R.id.buttonModeratorLogout).setOnClickListener {
            SessionManager(this).logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Load dashboard by default
        loadDashboard()
        loadPendingProducts()
        loadOrders()
    }

    private fun bindViews() {
        tabDashboard = findViewById(R.id.tabDashboard)
        tabApprovals = findViewById(R.id.tabApprovals)
        tabOrders = findViewById(R.id.tabOrders)
        tabAddProduct = findViewById(R.id.tabAddProduct)

        sectionDashboard = findViewById(R.id.sectionDashboard)
        sectionApprovals = findViewById(R.id.sectionApprovals)
        sectionOrders = findViewById(R.id.sectionOrders)
        sectionAddProduct = findViewById(R.id.sectionAddProduct)

        textRevenue = findViewById(R.id.textRevenue)
        textTotalProducts = findViewById(R.id.textTotalProducts)
        textSoldOut = findViewById(R.id.textSoldOut)
        textTotalOrders = findViewById(R.id.textTotalOrders)
        textPendingOrders = findViewById(R.id.textPendingOrders)
        textConfirmedOrders = findViewById(R.id.textConfirmedOrders)
        textDeliveredOrders = findViewById(R.id.textDeliveredOrders)
        textHasDescription = findViewById(R.id.textHasDescription)
        textHasImage = findViewById(R.id.textHasImage)
        textMissingInfo = findViewById(R.id.textMissingInfo)

        textViewPendingCount = findViewById(R.id.textViewPendingCount)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        recyclerViewPending = findViewById(R.id.recyclerViewPendingProducts)

        layoutEmptyOrders = findViewById(R.id.layoutEmptyOrders)
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders)

        editAddProductName = findViewById(R.id.editAddProductName)
        spinnerAddProductCategory = findViewById(R.id.spinnerAddProductCategory)
        editAddProductPrice = findViewById(R.id.editAddProductPrice)
        editAddProductImageUrl = findViewById(R.id.editAddProductImageUrl)
        editAddProductDescription = findViewById(R.id.editAddProductDescription)
    }

    private fun setupAdapters() {
        recyclerViewPending.layoutManager = LinearLayoutManager(this)
        pendingProductAdapter = PendingProductAdapter(
            products = pendingProducts,
            onApprove = { product -> showApprovalDialog(product) },
            onReject = { product -> confirmRejectProduct(product) }
        )
        recyclerViewPending.adapter = pendingProductAdapter

        recyclerViewOrders.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(orderItems) { order, nextStatus ->
            updateOrderStatus(order, nextStatus)
        }
        recyclerViewOrders.adapter = orderAdapter
    }

    private fun setupTabs() {
        tabDashboard.setOnClickListener { switchTab(0) }
        tabApprovals.setOnClickListener { switchTab(1) }
        tabOrders.setOnClickListener { switchTab(2) }
        tabAddProduct.setOnClickListener { switchTab(3) }
        switchTab(0) // Default
    }

    private fun switchTab(index: Int) {
        val tabs = listOf(tabDashboard, tabApprovals, tabOrders, tabAddProduct)
        val sections = listOf(sectionDashboard, sectionApprovals, sectionOrders, sectionAddProduct)

        tabs.forEachIndexed { i, tab ->
            if (i == index) {
                tab.setBackgroundColor(0xFFE91E8C.toInt())
                tab.setTextColor(0xFFFFFFFF.toInt())
            } else {
                tab.setBackgroundColor(0xFFFFFFFF.toInt())
                tab.setTextColor(0xFFE91E8C.toInt())
            }
        }

        sections.forEachIndexed { i, section ->
            section.visibility = if (i == index) View.VISIBLE else View.GONE
        }
    }

    private fun setupAddProductForm() {
        val categories = listOf("makeup", "clothing", "accessories", "shoes")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAddProductCategory.adapter = adapter

        findViewById<MaterialButton>(R.id.buttonSubmitProduct).setOnClickListener {
            submitNewProduct()
        }
    }

    // ========== DASHBOARD ==========

    private fun loadDashboard() {
        lifecycleScope.launch {
            try {
                val db = DatabaseProvider.getDatabase(applicationContext)
                val allProducts = db.productDao().getAllProducts()
                val approvedProducts = allProducts.filter { it.approved }
                val allOrders = db.orderDao().getAllOrders()

                // Revenue: sum of delivered orders × product price
                val productMap = allProducts.associateBy { it.id }
                val deliveredOrders = allOrders.filter { it.status == "delivered" }
                val revenue = deliveredOrders.sumOf { order ->
                    val price = productMap[order.productId]?.price ?: 0.0
                    order.quantity * price
                }

                val soldOut = approvedProducts.count { it.stock == 0 }
                val pending = allOrders.count { it.status == "pending" }
                val confirmed = allOrders.count { it.status == "confirmed" }
                val delivered = allOrders.count { it.status == "delivered" }

                // Quality stats (all approved products)
                val hasDesc = approvedProducts.count { !it.description.isNullOrBlank() }
                val hasImg = approvedProducts.count { !it.imageUrl.isNullOrBlank() }
                val missingInfo = approvedProducts.count {
                    it.description.isNullOrBlank() || it.imageUrl.isNullOrBlank()
                }

                textRevenue.text = "Rs ${revenue.toLong()}"
                textTotalProducts.text = "${approvedProducts.size}"
                textSoldOut.text = "$soldOut"
                textTotalOrders.text = "${allOrders.size}"
                textPendingOrders.text = "$pending"
                textConfirmedOrders.text = "$confirmed"
                textDeliveredOrders.text = "$delivered"
                textHasDescription.text = "$hasDesc / ${approvedProducts.size}"
                textHasImage.text = "$hasImg / ${approvedProducts.size}"
                textMissingInfo.text = "$missingInfo"

                textViewPendingCount.text = "${allOrders.filter { it.status == "pending" }.size} pending orders · ${allProducts.filter { !it.approved }.size} awaiting approval"
            } catch (e: Exception) {
                Toast.makeText(this@ModeratorActivity, "Failed to load dashboard", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ========== APPROVALS ==========

    private fun loadPendingProducts() {
        lifecycleScope.launch {
            try {
                val db = DatabaseProvider.getDatabase(applicationContext)
                val pending = db.productDao().getAllProducts().filter { !it.approved }
                pendingProductAdapter.updateProducts(pending)
                if (pending.isEmpty()) {
                    recyclerViewPending.visibility = View.GONE
                    layoutEmptyState.visibility = View.VISIBLE
                } else {
                    recyclerViewPending.visibility = View.VISIBLE
                    layoutEmptyState.visibility = View.GONE
                }
            } catch (e: Exception) {
                Toast.makeText(this@ModeratorActivity, "Failed to load pending products", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showApprovalDialog(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Approve Product")
            .setMessage("Approve '${product.name}'?\n\nCategory: ${product.category}\nPrice: Rs ${product.price.toLong()}")
            .setPositiveButton("Approve") { _, _ -> approveProduct(product) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmRejectProduct(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Reject Product")
            .setMessage("Reject and delete '${product.name}'? This cannot be undone.")
            .setPositiveButton("Reject") { _, _ -> rejectProduct(product) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun approveProduct(product: Product) {
        lifecycleScope.launch {
            try {
                val db = DatabaseProvider.getDatabase(applicationContext)
                db.productDao().updateProduct(product.copy(approved = true))
                Toast.makeText(this@ModeratorActivity, "'${product.name}' approved!", Toast.LENGTH_SHORT).show()
                loadPendingProducts()
                loadDashboard()
            } catch (e: Exception) {
                Toast.makeText(this@ModeratorActivity, "Failed to approve product", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun rejectProduct(product: Product) {
        lifecycleScope.launch {
            try {
                val db = DatabaseProvider.getDatabase(applicationContext)
                db.productDao().deleteProduct(product)
                Toast.makeText(this@ModeratorActivity, "'${product.name}' rejected", Toast.LENGTH_SHORT).show()
                loadPendingProducts()
                loadDashboard()
            } catch (e: Exception) {
                Toast.makeText(this@ModeratorActivity, "Failed to reject product", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ========== ORDERS ==========

    private fun loadOrders() {
        lifecycleScope.launch {
            try {
                val db = DatabaseProvider.getDatabase(applicationContext)
                val allOrders = db.orderDao().getAllOrders()
                val productMap = db.productDao().getAllProducts().associateBy { it.id }

                val items = allOrders.map { order ->
                    val product = productMap[order.productId]
                    OrderItem(
                        order = order,
                        productName = product?.name ?: "Unknown Product",
                        productPrice = product?.price ?: 0.0
                    )
                }.sortedByDescending { it.order.orderDate }

                orderAdapter.updateItems(items)

                if (items.isEmpty()) {
                    recyclerViewOrders.visibility = View.GONE
                    layoutEmptyOrders.visibility = View.VISIBLE
                } else {
                    recyclerViewOrders.visibility = View.VISIBLE
                    layoutEmptyOrders.visibility = View.GONE
                }
            } catch (e: Exception) {
                Toast.makeText(this@ModeratorActivity, "Failed to load orders", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateOrderStatus(order: Order, newStatus: String) {
        lifecycleScope.launch {
            try {
                val db = DatabaseProvider.getDatabase(applicationContext)
                db.orderDao().updateOrder(order.copy(status = newStatus))
                Toast.makeText(this@ModeratorActivity, "Order marked as $newStatus", Toast.LENGTH_SHORT).show()
                loadOrders()
                loadDashboard()
            } catch (e: Exception) {
                Toast.makeText(this@ModeratorActivity, "Failed to update order", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ========== ADD PRODUCT ==========

    private fun submitNewProduct() {
        val name = editAddProductName.text?.toString()?.trim() ?: ""
        val category = spinnerAddProductCategory.selectedItem?.toString() ?: ""
        val priceStr = editAddProductPrice.text?.toString()?.trim() ?: ""
        val imageUrl = editAddProductImageUrl.text?.toString()?.trim() ?: ""
        val description = editAddProductDescription.text?.toString()?.trim() ?: ""

        if (name.isEmpty()) {
            editAddProductName.error = "Product name is required"
            return
        }
        val price = priceStr.toDoubleOrNull()
        if (price == null || price <= 0) {
            editAddProductPrice.error = "Enter a valid price"
            return
        }

        val sessionManager = SessionManager(this)
        val vendorId = sessionManager.getUserId()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val newProduct = Product(
            name = name,
            category = category,
            subcategory = null,
            price = price,
            description = description.ifEmpty { null },
            imageUrl = imageUrl.ifEmpty { null },
            registrationDate = dateFormat.format(Date()),
            approved = false,
            vendorId = vendorId
        )

        lifecycleScope.launch {
            try {
                val db = DatabaseProvider.getDatabase(applicationContext)
                db.productDao().insertProduct(newProduct)
                Toast.makeText(this@ModeratorActivity, "'$name' submitted for approval", Toast.LENGTH_LONG).show()
                // Clear form
                editAddProductName.text?.clear()
                editAddProductPrice.text?.clear()
                editAddProductImageUrl.text?.clear()
                editAddProductDescription.text?.clear()
                spinnerAddProductCategory.setSelection(0)
                // Refresh data
                loadPendingProducts()
                loadDashboard()
            } catch (e: Exception) {
                Toast.makeText(this@ModeratorActivity, "Failed to submit product", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
