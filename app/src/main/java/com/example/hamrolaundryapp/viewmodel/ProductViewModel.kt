package com.example.hamrolaundryapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hamrolaundryapp.model.ProductModel
import com.example.hamrolaundryapp.repo.ProductRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductViewModel(val repo: ProductRepo) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _products = MutableStateFlow<ProductModel?>(null)
    val product: StateFlow<ProductModel?> = _products.asStateFlow()

    private val _allProducts = MutableStateFlow<List<ProductModel>?>(null)
    val allProducts: StateFlow<List<ProductModel>?> = _allProducts.asStateFlow()

    fun addProduct(product: ProductModel, callback: (Boolean, String) -> Unit) {
        _loading.value = true
        repo.addProduct(product) { success, message ->
            _loading.value = false
            callback(success, message)
        }
    }

    fun deleteProduct(id: String, callback: (Boolean, String) -> Unit) {
        _loading.value = true
        repo.deleteProduct(id) { success, message ->
            _loading.value = false
            callback(success, message)
        }
    }

    fun editProduct(id: String, product: ProductModel, callback: (Boolean, String) -> Unit) {
        _loading.value = true
        repo.editProduct(id, product) { success, message ->
            _loading.value = false
            callback(success, message)
        }
    }

    fun getAllProduct() {
        _loading.value = true
        repo.getAllProduct { success, message, data ->
            _loading.value = false
            if (success) {
                _allProducts.value = data
            } else {
                _allProducts.value = emptyList()
            }
        }
    }

    fun getProductById(id: String) {
        _loading.value = true
        repo.getProductById(id) { success, message, data ->
            _loading.value = false
            if (success) {
                _products.value = data
            } else {
                _products.value = null
            }
        }
    }
}
