package com.stylica.makeupclothing.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stylica.makeupclothing.adapter.ProductAdapter
import com.stylica.makeupclothing.R
import kotlinx.coroutines.launch
import com.stylica.makeupclothing.model.CartItem
import com.stylica.makeupclothing.model.Product
import com.stylica.makeupclothing.utils.Constants
import com.stylica.makeupclothing.utils.DatabaseProvider
import com.stylica.makeupclothing.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var editTextSearch: EditText
    private var allProducts = listOf<Product>()
    private var selectedCategory = "All"

    // Category chips
    private lateinit var chipAll: TextView
    private lateinit var chipMakeup: TextView
    private lateinit var chipClothing: TextView
    private lateinit var chipAccessories: TextView
    private lateinit var chipShoes: TextView
    private lateinit var chipSale: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        editTextSearch = view.findViewById(R.id.editTextSearch)

        chipAll = view.findViewById(R.id.chipAll)
        chipMakeup = view.findViewById(R.id.chipMakeup)
        chipClothing = view.findViewById(R.id.chipClothing)
        chipAccessories = view.findViewById(R.id.chipAccessories)
        chipShoes = view.findViewById(R.id.chipShoes)
        chipSale = view.findViewById(R.id.chipSale)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        productAdapter = ProductAdapter(emptyList()) { product ->
            onAddToCart(product)
        }
        recyclerView.adapter = productAdapter

        setupChips()
        setupSearch()
        loadProducts()

        return view
    }

    private fun setupChips() {
        val chips = listOf(chipAll, chipMakeup, chipClothing, chipAccessories, chipShoes, chipSale)
        val categories = listOf("All", Constants.CATEGORY_MAKEUP, Constants.CATEGORY_CLOTHING,
            Constants.CATEGORY_ACCESSORIES, Constants.CATEGORY_SHOES, Constants.CATEGORY_SALE)

        chips.forEachIndexed { index, chip ->
            chip.setOnClickListener {
                selectedCategory = categories[index]
                chips.forEach { c -> setChipUnselected(c) }
                setChipSelected(chip)
                filterProducts()
            }
        }
    }

    private fun setChipSelected(chip: TextView) {
        chip.setBackgroundResource(R.drawable.bg_chip_selected)
        chip.setTextColor(resources.getColor(android.R.color.white, null))
    }

    private fun setChipUnselected(chip: TextView) {
        chip.setBackgroundResource(R.drawable.bg_chip_unselected)
        chip.setTextColor(resources.getColor(android.R.color.darker_gray, null))
    }

    private fun setupSearch() {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { filterProducts() }
        })
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(requireContext())
                allProducts = database.productDao().getAllProducts().filter { it.approved }
                filterProducts()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load products", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterProducts() {
        val searchQuery = editTextSearch.text.toString().lowercase()

        val filteredProducts = allProducts.filter { product ->
            val matchesSearch = product.name.lowercase().contains(searchQuery) ||
                    product.description?.lowercase()?.contains(searchQuery) == true

            val matchesCategory = when (selectedCategory) {
                "All" -> true
                Constants.CATEGORY_SALE -> true // show all products as "on sale"
                else -> product.category == selectedCategory
            }

            matchesSearch && matchesCategory
        }

        // For Sale category show max 50% price visually (handled in adapter separately)
        productAdapter.updateProducts(filteredProducts, selectedCategory == Constants.CATEGORY_SALE)
    }

    private fun onAddToCart(product: Product) {
        val userId = SessionManager(requireContext()).getUserId()
        if (userId == -1) {
            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(requireContext())
                val existing = database.cartItemDao().getCartItemByUserAndProduct(userId, product.id)
                if (existing != null) {
                    database.cartItemDao().insertCartItem(existing.copy(quantity = existing.quantity + 1))
                } else {
                    val cartItem = CartItem(
                        userId = userId,
                        productId = product.id,
                        quantity = 1,
                        addedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    )
                    database.cartItemDao().insertCartItem(cartItem)
                }
                Toast.makeText(requireContext(), "${product.name} added to cart!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
