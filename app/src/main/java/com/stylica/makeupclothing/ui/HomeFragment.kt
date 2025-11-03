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
import com.stylica.makeupclothing.model.Product
import com.stylica.makeupclothing.utils.Constants
import com.stylica.makeupclothing.utils.DatabaseProvider

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var editTextSearch: EditText
    private lateinit var spinnerCategory: Spinner
    private var allProducts = listOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        
        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        editTextSearch = view.findViewById(R.id.editTextSearch)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        
        productAdapter = ProductAdapter(emptyList()) { product ->
            onAddToCart(product)
        }
        recyclerView.adapter = productAdapter
        
        setupCategoryFilter()
        setupSearch()
        loadProducts()
        
        return view
    }

    private fun setupCategoryFilter() {
        val categories = arrayOf("All", Constants.CATEGORY_MAKEUP, Constants.CATEGORY_CLOTHING, Constants.CATEGORY_ACCESSORIES)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterProducts()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSearch() {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterProducts()
            }
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
        val selectedCategory = spinnerCategory.selectedItem.toString()

        val filteredProducts = allProducts.filter { product ->
            val matchesSearch = product.name.lowercase().contains(searchQuery) ||
                    product.description?.lowercase()?.contains(searchQuery) == true
            val matchesCategory = selectedCategory == "All" || product.category == selectedCategory

            matchesSearch && matchesCategory
        }

        productAdapter.updateProducts(filteredProducts)
    }
    
    private fun onAddToCart(product: Product) {
        Toast.makeText(requireContext(), "${product.name} added to cart", Toast.LENGTH_SHORT).show()
        // TODO: Implement cart functionality
    }
}
