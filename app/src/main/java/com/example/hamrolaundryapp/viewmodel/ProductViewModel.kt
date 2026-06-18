package com.example.hamrolaundryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hamrolaundryapp.model.ProductModel
import com.example.hamrolaundryapp.repo.ProductRepo

class ProductViewModel(val repo: ProductRepo) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _products = MutableLiveData<ProductModel?>(null)
    val product: LiveData<ProductModel?> = _products

    private val _allProducts = MutableLiveData<List<ProductModel>?>(null)
    val allProducts: LiveData<List<ProductModel>?> = _allProducts

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
        repo.getAllProduct { success, message, product ->
            if (success) {
                _allProducts.value = product
                _loading.value = false
            } else {
                _loading.value = false
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
