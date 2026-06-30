package com.example.hamrolaundryapp.repo

import com.example.hamrolaundryapp.model.LaundryService
import com.example.hamrolaundryapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class HomeRepo(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    private val ref = database.getReference("services")

    fun getUserName(): String {
        return auth.currentUser?.displayName ?: "User"
    }

    suspend fun getUserRole(): String {
        val uid = auth.currentUser?.uid ?: return "user"
        return try {
            val snapshot = database.getReference("users").child(uid).child("role").get().await()
            snapshot.getValue(String::class.java) ?: "user"
        } catch (e: Exception) {
            "user"
        }
    }

    private fun checkAndInjectMockData() {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mockServices = listOf(
                    LaundryService("1", "Wash Only", "Basic washing and drying", 100.0, "12 Hours", "", "Wash"),
                    LaundryService("2", "Wash & Iron", "Complete care for your daily wear", 150.0, "24 Hours", "", "Popular"),
                    LaundryService("3", "Dry Cleaning", "Specialized cleaning for delicate fabrics", 500.0, "48 Hours", "", "Dry Clean"),
                    LaundryService("4", "Premium Laundry", "Luxury treatment for your best clothes", 800.0, "36 Hours", "", "Popular"),
                    LaundryService("5", "Express Laundry", "Get your clothes clean in record time", 300.0, "4 Hours", "", "Express"),
                    LaundryService("6", "Iron Only", "Professional steam press", 50.0, "6 Hours", "", "Iron"),
                    LaundryService("7", "Curtain Cleaning", "Deep cleaning for all types of curtains", 400.0, "72 Hours", "", "Home"),
                    LaundryService("8", "Shoe Cleaning", "Revive your favorite pair of shoes", 250.0, "48 Hours", "", "Popular"),
                    LaundryService("9", "Bedding & Comforter", "Heavy duty cleaning for large items", 600.0, "48 Hours", "", "Home"),
                    LaundryService("10", "Leather Jacket Care", "Specialized leather cleaning and conditioning", 1200.0, "5 Days", "", "Special")
                )
                
                // Inject if the specific ID doesn't exist
                mockServices.forEach { service ->
                    if (!snapshot.hasChild(service.id)) {
                        ref.child(service.id).setValue(service)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getPopularServices(): Flow<Resource<List<LaundryService>>> = callbackFlow {
        trySend(Resource.Loading())
        checkAndInjectMockData()

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val services = mutableListOf<LaundryService>()
                for (child in snapshot.children) {
                    val service = child.getValue(LaundryService::class.java)
                    if (service != null && service.category == "Popular") {
                        services.add(service)
                    }
                }
                trySend(Resource.Success(services))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getAllServices(): Flow<Resource<List<LaundryService>>> = callbackFlow {
        trySend(Resource.Loading())

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val services = mutableListOf<LaundryService>()
                for (child in snapshot.children) {
                    child.getValue(LaundryService::class.java)?.let { services.add(it) }
                }
                trySend(Resource.Success(services))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
