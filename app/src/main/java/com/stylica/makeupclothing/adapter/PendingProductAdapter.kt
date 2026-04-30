package com.stylica.makeupclothing.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stylica.makeupclothing.R
import com.stylica.makeupclothing.model.Product

class PendingProductAdapter(
    private val products: MutableList<Product>,
    private val onApprove: (Product) -> Unit,
    private val onReject: (Product) -> Unit
) : RecyclerView.Adapter<PendingProductAdapter.PendingViewHolder>() {

    class PendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewPendingProductName)
        val categoryTextView: TextView = itemView.findViewById(R.id.textViewPendingProductCategory)
        val priceTextView: TextView = itemView.findViewById(R.id.textViewPendingProductPrice)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textViewPendingProductDescription)
        val approveButton: Button = itemView.findViewById(R.id.buttonApprove)
        val rejectButton: Button = itemView.findViewById(R.id.buttonReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_product, parent, false)
        return PendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: PendingViewHolder, position: Int) {
        val product = products[position]
        holder.nameTextView.text = product.name
        holder.categoryTextView.text = product.category.replaceFirstChar { it.uppercase() }
        holder.priceTextView.text = "Rs ${product.price.toLong()}"
        holder.descriptionTextView.text = product.description ?: "No description available"
        holder.approveButton.setOnClickListener { onApprove(product) }
        holder.rejectButton.setOnClickListener { onReject(product) }
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }
}
