package com.stylica.makeupclothing.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stylica.makeupclothing.R
import com.stylica.makeupclothing.adapter.PendingProductAdapter
import kotlinx.coroutines.launch
import com.stylica.makeupclothing.model.Product
import com.stylica.makeupclothing.utils.DatabaseProvider
import com.stylica.makeupclothing.utils.SessionManager

class ModeratorActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewPendingCount: TextView
    private lateinit var pendingProductAdapter: PendingProductAdapter
    private val pendingProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moderator)

        recyclerView = findViewById(R.id.recyclerViewPendingProducts)
        textViewPendingCount = findViewById(R.id.textViewPendingCount)
        recyclerView.layoutManager = LinearLayoutManager(this)

        pendingProductAdapter = PendingProductAdapter(
            products = pendingProducts,
            onApprove = { product -> showApprovalDialog(product) },
            onReject = { product -> confirmRejectProduct(product) }
        )
        recyclerView.adapter = pendingProductAdapter

        findViewById<Button>(R.id.buttonModeratorLogout).setOnClickListener {
            SessionManager(this).logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        loadPendingProducts()
    }

    private fun loadPendingProducts() {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(applicationContext)
                val allProducts = database.productDao().getAllProducts()
                val pending = allProducts.filter { !it.approved }

                pendingProductAdapter.updateProducts(pending)
                textViewPendingCount.text = "${pending.size} product(s) pending approval"
            } catch (e: Exception) {
                Toast.makeText(this@ModeratorActivity, "Failed to load products", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showApprovalDialog(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Approve Product")
            .setMessage("Approve '${product.name}'?\n\nCategory: ${product.category}\nPrice: $${product.price}")
            .setPositiveButton("Approve") { _, _ ->
                approveProduct(product)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmRejectProduct(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Reject Product")
            .setMessage("Reject and delete '${product.name}'? This cannot be undone.")
            .setPositiveButton("Reject") { _, _ ->
                rejectProduct(product)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun approveProduct(product: Product) {
        lifecycleScope.launch {
            try {
                val approvedProduct = product.copy(approved = true)
                val database = DatabaseProvider.getDatabase(applicationContext)
                database.productDao().updateProduct(approvedProduct)
                Toast.makeText(this@ModeratorActivity, "'${product.name}' approved!", Toast.LENGTH_SHORT).show()
                loadPendingProducts()
            } catch (e: Exception) {
                Toast.makeText(this@ModeratorActivity, "Failed to approve product", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun rejectProduct(product: Product) {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(applicationContext)
                database.productDao().deleteProduct(product)
                Toast.makeText(this@ModeratorActivity, "'${product.name}' rejected and removed", Toast.LENGTH_SHORT).show()
                loadPendingProducts()
            } catch (e: Exception) {
                Toast.makeText(this@ModeratorActivity, "Failed to reject product", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
