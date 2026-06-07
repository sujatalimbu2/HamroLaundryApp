package com.example.hamrolaundryapp.repo

import com.example.hamrolaundryapp.model.ProductModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductRepoImpl : ProductRepo {
    private val database by lazy { FirebaseDatabase.getInstance() }
    private val ref by lazy { database.getReference("products") }

    override fun addProduct(product: ProductModel, callback: (Boolean, String) -> Unit) {
        val id = ref.push().key.toString()
        product.id = id
        ref.child(id).setValue(product).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Product added successfully")
            } else {
                callback(false, it.exception?.message ?: "Error adding product")
            }
        }
    }

    override fun deleteProduct(id: String, callback: (Boolean, String) -> Unit) {
        ref.child(id).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Product deleted successfully")
            } else {
                callback(false, it.exception?.message ?: "Error deleting product")
            }
        }
    }

    override fun editProduct(id: String, product: ProductModel, callback: (Boolean, String) -> Unit) {
        ref.child(id).setValue(product).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Product updated successfully")
            } else {
                callback(false, it.exception?.message ?: "Error updating product")
            }
        }
    }

    override fun getAllProduct(callback: (Boolean, String, List<ProductModel>?) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<ProductModel>()
                if (snapshot.exists()) {
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(ProductModel::class.java)
                        if (product != null) {
                            productList.add(product)
                        }
                    }
                    callback(true, "Products fetched successfully", productList)
                } else {
                    callback(false, "No products found", null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun getProductById(id: String, callback: (Boolean, String, ProductModel?) -> Unit) {
        ref.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val product = snapshot.getValue(ProductModel::class.java)
                    callback(true, "Product fetched successfully", product)
                } else {
                    callback(false, "Product not found", null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }
}
