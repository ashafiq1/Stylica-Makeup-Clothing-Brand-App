package com.stylica.makeupclothing.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stylica.makeupclothing.R
import com.stylica.makeupclothing.model.Product
import com.stylica.makeupclothing.ui.ProductDetailActivity

class ProductAdapter(
    private var products: List<Product>,
    private val onAddToCartClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var isSaleMode = false

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewProduct)
        val nameTextView: TextView = itemView.findViewById(R.id.textViewProductName)
        val categoryTextView: TextView = itemView.findViewById(R.id.textViewProductCategory)
        val priceTextView: TextView = itemView.findViewById(R.id.textViewProductPrice)
        val saleBadge: TextView = itemView.findViewById(R.id.textViewSaleBadge)
        val addToCartButton: Button = itemView.findViewById(R.id.buttonAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.nameTextView.text = product.name
        holder.categoryTextView.text = product.category.replaceFirstChar { it.uppercase() }

        if (isSaleMode) {
            val salePrice = (product.price * 0.5).toLong()
            holder.priceTextView.text = "Rs $salePrice"
            holder.saleBadge.visibility = View.VISIBLE
        } else {
            holder.priceTextView.text = "Rs ${product.price.toLong()}"
            holder.saleBadge.visibility = View.GONE
        }

        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .centerCrop()
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.addToCartButton.setOnClickListener {
            onAddToCartClick(product)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>, saleMode: Boolean = false) {
        products = newProducts
        isSaleMode = saleMode
        notifyDataSetChanged()
    }
}
