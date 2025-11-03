package com.stylica.makeupclothing.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stylica.makeupclothing.R
import kotlinx.coroutines.launch
import com.stylica.makeupclothing.model.Product
import com.stylica.makeupclothing.utils.DatabaseProvider

class ModeratorActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val pendingProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moderator)

        recyclerView = findViewById(R.id.recyclerViewPendingProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadPendingProducts()
    }

    private fun loadPendingProducts() {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(applicationContext)
                val allProducts = database.productDao().getAllProducts()
                
                pendingProducts.clear()
                pendingProducts.addAll(allProducts.filter { !it.approved })

                // TODO: Setup adapter with approve/reject buttons
                Toast.makeText(
                    this@ModeratorActivity,
                    "${pendingProducts.size} products pending approval",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(this@ModeratorActivity, "Failed to load products", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showApprovalDialog(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Approve Product")
            .setMessage("Do you want to approve '${product.name}'?")
            .setPositiveButton("Approve") { _, _ ->
                approveProduct(product)
            }
            .setNegativeButton("Reject") { _, _ ->
                rejectProduct(product)
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun approveProduct(product: Product) {
        lifecycleScope.launch {
            try {
                val approvedProduct = product.copy(approved = true)
                val database = DatabaseProvider.getDatabase(applicationContext)
                database.productDao().updateProduct(approvedProduct)
                
                Toast.makeText(this@ModeratorActivity, "Product approved", Toast.LENGTH_SHORT).show()
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
                
                Toast.makeText(this@ModeratorActivity, "Product rejected", Toast.LENGTH_SHORT).show()
                loadPendingProducts()
            } catch (e: Exception) {
                Toast.makeText(this@ModeratorActivity, "Failed to reject product", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
