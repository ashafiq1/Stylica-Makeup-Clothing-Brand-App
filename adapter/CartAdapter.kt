package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import d.R
import model.CartItem
import model.Product

class CartAdapter(
    private var cartItems: List<Pair<CartItem, Product>>,
    private val onRemoveClick: (CartItem) -> Unit,
    private val onQuantityChange: (CartItem, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewCartProduct)
        val nameTextView: TextView = itemView.findViewById(R.id.textViewCartProductName)
        val priceTextView: TextView = itemView.findViewById(R.id.textViewCartProductPrice)
        val quantityTextView: TextView = itemView.findViewById(R.id.textViewQuantity)
        val buttonIncrease: Button = itemView.findViewById(R.id.buttonIncrease)
        val buttonDecrease: Button = itemView.findViewById(R.id.buttonDecrease)
        val buttonRemove: Button = itemView.findViewById(R.id.buttonRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val (cartItem, product) = cartItems[position]
        
        holder.nameTextView.text = product.name
        holder.priceTextView.text = "$${product.price * cartItem.quantity}"
        holder.quantityTextView.text = cartItem.quantity.toString()

        holder.buttonIncrease.setOnClickListener {
            onQuantityChange(cartItem, cartItem.quantity + 1)
        }

        holder.buttonDecrease.setOnClickListener {
            if (cartItem.quantity > 1) {
                onQuantityChange(cartItem, cartItem.quantity - 1)
            }
        }

        holder.buttonRemove.setOnClickListener {
            onRemoveClick(cartItem)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateCart(newCartItems: List<Pair<CartItem, Product>>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }
}
