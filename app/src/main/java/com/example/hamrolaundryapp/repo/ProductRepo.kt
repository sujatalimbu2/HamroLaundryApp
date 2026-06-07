package com.example.hamrolaundryapp.repo

import com.example.hamrolaundryapp.model.ProductModel

interface ProductRepo {
    fun addProduct(product: ProductModel, callback: (Boolean, String) -> Unit)
    fun deleteProduct(id: String, callback: (Boolean, String) -> Unit)
    fun editProduct(id: String, product: ProductModel, callback: (Boolean, String) -> Unit)
    fun getAllProduct(callback: (Boolean, String, List<ProductModel>?) -> Unit)
    fun getProductById(id: String, callback: (Boolean, String, ProductModel?) -> Unit)
}
