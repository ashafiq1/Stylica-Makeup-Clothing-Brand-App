package com.stylica.makeupclothing.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.stylica.makeupclothing.model.Product
import com.stylica.makeupclothing.repository.ProductRepository

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {
    
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    fun loadProducts() {
        viewModelScope.launch {
            try {
                val productList = repository.getAllProducts()
                _products.postValue(productList)
            } catch (e: Exception) {
                _error.postValue("Failed to load products: ${e.message}")
            }
        }
    }
    
    fun searchProducts(query: String) {
        viewModelScope.launch {
            try {
                val productList = repository.searchByName(query)
                _products.postValue(productList)
            } catch (e: Exception) {
                _error.postValue("Failed to search products: ${e.message}")
            }
        }
    }
    
    fun addProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.insertProduct(product)
                loadProducts()
            } catch (e: Exception) {
                _error.postValue("Failed to add product: ${e.message}")
            }
        }
    }
    
    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.updateProduct(product)
                loadProducts()
            } catch (e: Exception) {
                _error.postValue("Failed to update product: ${e.message}")
            }
        }
    }
    
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(product)
                loadProducts()
            } catch (e: Exception) {
                _error.postValue("Failed to delete product: ${e.message}")
            }
        }
    }
}
