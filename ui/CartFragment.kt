package ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import adapter.CartAdapter
import d.R
import kotlinx.coroutines.launch
import model.CartItem
import model.Product
import utils.DatabaseProvider

class CartFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var textViewTotal: TextView
    private lateinit var buttonCheckout: Button
    private var userId: Int = 1 // TODO: Get from session
    private val cartItems = mutableListOf<Pair<CartItem, Product>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        
        recyclerView = view.findViewById(R.id.recyclerViewCart)
        textViewTotal = view.findViewById(R.id.textViewTotal)
        buttonCheckout = view.findViewById(R.id.buttonCheckout)
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        cartAdapter = CartAdapter(
            cartItems = emptyList(),
            onRemoveClick = { cartItem -> removeFromCart(cartItem) },
            onQuantityChange = { cartItem, newQuantity -> updateQuantity(cartItem, newQuantity) }
        )
        recyclerView.adapter = cartAdapter
        
        buttonCheckout.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                val intent = Intent(requireContext(), CheckoutActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
        
        loadCart()
        
        return view
    }

    override fun onResume() {
        super.onResume()
        loadCart()
    }

    private fun loadCart() {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(requireContext())
                val items = database.cartItemDao().getCartItemsByUser(userId)
                val products = database.productDao().getAllProducts()
                
                cartItems.clear()
                items.forEach { cartItem ->
                    val product = products.find { it.id == cartItem.productId }
                    if (product != null) {
                        cartItems.add(Pair(cartItem, product))
                    }
                }
                
                cartAdapter.updateCart(cartItems)
                updateTotal()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeFromCart(cartItem: CartItem) {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(requireContext())
                database.cartItemDao().deleteCartItem(cartItem)
                Toast.makeText(requireContext(), "Item removed from cart", Toast.LENGTH_SHORT).show()
                loadCart()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to remove item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        lifecycleScope.launch {
            try {
                val updatedItem = cartItem.copy(quantity = newQuantity)
                val database = DatabaseProvider.getDatabase(requireContext())
                database.cartItemDao().insertCartItem(updatedItem)
                loadCart()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to update quantity", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTotal() {
        var total = 0.0
        cartItems.forEach { (cartItem, product) ->
            total += product.price * cartItem.quantity
        }
        textViewTotal.text = "Total: $${"%.2f".format(total)}"
    }
}
