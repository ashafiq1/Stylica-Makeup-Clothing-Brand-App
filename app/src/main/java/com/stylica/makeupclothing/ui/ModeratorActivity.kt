package com.stylica.makeupclothing.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.stylica.makeupclothing.R
import com.stylica.makeupclothing.adapter.OrderAdapter
import com.stylica.makeupclothing.adapter.OrderItem
import com.stylica.makeupclothing.model.Order
import com.stylica.makeupclothing.utils.DatabaseProvider
import com.stylica.makeupclothing.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ModeratorActivity : AppCompatActivity() {

    // Tabs (3 tabs now: Dashboard | Orders | Add Product)
    private lateinit var tabDashboard: TextView
    private lateinit var tabOrders: TextView
    private lateinit var tabAddProduct: TextView

    // Sections
    private lateinit var sectionDashboard: View
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
        setupOrdersAdapter()
        setupTabs()
        setupDashboardCardClicks()
        setupAddProductForm()

        findViewById<MaterialButton>(R.id.buttonModeratorLogout).setOnClickListener {
            SessionManager(this).logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        loadDashboard()
        loadOrders()
    }

    private fun bindViews() {
        tabDashboard = findViewById(R.id.tabDashboard)
        tabOrders = findViewById(R.id.tabOrders)
        tabAddProduct = findViewById(R.id.tabAddProduct)

        sectionDashboard = findViewById(R.id.sectionDashboard)
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

        layoutEmptyOrders = findViewById(R.id.layoutEmptyOrders)
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders)

        editAddProductName = findViewById(R.id.editAddProductName)
        spinnerAddProductCategory = findViewById(R.id.spinnerAddProductCategory)
        editAddProductPrice = findViewById(R.id.editAddProductPrice)
        editAddProductImageUrl = findViewById(R.id.editAddProductImageUrl)
        editAddProductDescription = findViewById(R.id.editAddProductDescription)
    }

    private fun setupOrdersAdapter() {
        recyclerViewOrders.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(orderItems) { order, nextStatus ->
            updateOrderStatus(order, nextStatus)
        }
        recyclerViewOrders.adapter = orderAdapter
    }

    private fun setupTabs() {
        tabDashboard.setOnClickListener { switchTab(0) }
        tabOrders.setOnClickListener { switchTab(1) }
        tabAddProduct.setOnClickListener { switchTab(2) }
        switchTab(0)
    }

    private fun switchTab(index: Int) {
        val tabs = listOf(tabDashboard, tabOrders, tabAddProduct)
        val sections = listOf(sectionDashboard, sectionOrders, sectionAddProduct)

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

    private fun setupDashboardCardClicks() {
        // Revenue, Sold Out, Orders cards → switch to Orders tab
        findViewById<CardView>(R.id.cardModRevenue).setOnClickListener { switchTab(1) }
        findViewById<CardView>(R.id.cardModSoldOut).setOnClickListener { switchTab(1) }
        findViewById<CardView>(R.id.cardModOrders).setOnClickListener { switchTab(1) }
        // Products card → Add Product tab
        findViewById<CardView>(R.id.cardModProducts).setOnClickListener { switchTab(2) }
        // Order stat numbers → Orders tab
        textPendingOrders.setOnClickListener { switchTab(1) }
        textConfirmedOrders.setOnClickListener { switchTab(1) }
        textDeliveredOrders.setOnClickListener { switchTab(1) }
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

            } catch (e: Exception) {
                Toast.makeText(this@ModeratorActivity, "Failed to load dashboard", Toast.LENGTH_SHORT).show()
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

        val vendorId = SessionManager(this).getUserId()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val newProduct = com.stylica.makeupclothing.model.Product(
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
                Toast.makeText(this@ModeratorActivity, "'$name' submitted for admin review", Toast.LENGTH_LONG).show()
                editAddProductName.text?.clear()
                editAddProductPrice.text?.clear()
                editAddProductImageUrl.text?.clear()
                editAddProductDescription.text?.clear()
                spinnerAddProductCategory.setSelection(0)
                loadDashboard()
            } catch (e: Exception) {
                Toast.makeText(this@ModeratorActivity, "Failed to submit product", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
