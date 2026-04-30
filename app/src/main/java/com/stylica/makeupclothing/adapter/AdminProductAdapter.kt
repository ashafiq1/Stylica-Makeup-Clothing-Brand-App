package com.stylica.makeupclothing.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stylica.makeupclothing.R
import com.stylica.makeupclothing.model.Product

class AdminProductAdapter(
    private val products: MutableList<Product>,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder>() {

    class AdminProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewAdminProductName)
        val categoryTextView: TextView = itemView.findViewById(R.id.textViewAdminProductCategory)
        val priceTextView: TextView = itemView.findViewById(R.id.textViewAdminProductPrice)
        val statusTextView: TextView = itemView.findViewById(R.id.textViewAdminProductStatus)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDeleteProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_product, parent, false)
        return AdminProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminProductViewHolder, position: Int) {
        val product = products[position]
        holder.nameTextView.text = product.name
        holder.categoryTextView.text = product.category.replaceFirstChar { it.uppercase() }
        holder.priceTextView.text = "Rs ${product.price.toLong()}"

        if (product.approved) {
            holder.statusTextView.text = "Approved"
            holder.statusTextView.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            holder.statusTextView.text = "Pending"
            holder.statusTextView.setTextColor(Color.parseColor("#FFC107"))
        }

        holder.deleteButton.setOnClickListener { onDelete(product) }
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }
}
