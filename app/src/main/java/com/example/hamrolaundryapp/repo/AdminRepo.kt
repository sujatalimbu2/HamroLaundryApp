package com.example.hamrolaundryapp.repo

import com.example.hamrolaundryapp.model.Booking
import com.example.hamrolaundryapp.model.LaundryService
import com.example.hamrolaundryapp.utils.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AdminRepo(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun isAdmin(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            val snapshot = database.getReference("users").child(uid).child("role").get().await()
            snapshot.getValue(String::class.java) == "admin"
        } catch (e: Exception) {
            false
        }
    }

    fun getAllBookings(): Flow<Resource<List<Booking>>> = callbackFlow {
        trySend(Resource.Loading())
        val ref = database.getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookings = mutableListOf<Booking>()
                for (child in snapshot.children) {
                    child.getValue(Booking::class.java)?.let { bookings.add(it) }
                }
                trySend(Resource.Success(bookings.reversed()))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun updateBookingStatus(bookingId: String, newStatus: String): Resource<Unit> {
        return try {
            database.getReference("bookings").child(bookingId)
                .child("status").setValue(newStatus).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update status")
        }
    }

    suspend fun deleteBooking(bookingId: String): Resource<Unit> {
        return try {
            database.getReference("bookings").child(bookingId).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete booking")
        }
    }

    suspend fun addOrUpdateService(service: LaundryService): Resource<Unit> {
        return try {
            val ref = database.getReference("services")
            val id = if (service.id.isEmpty()) ref.push().key ?: "" else service.id
            val finalService = service.copy(id = id)
            ref.child(id).setValue(finalService).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save service")
        }
    }

    suspend fun deleteService(serviceId: String): Resource<Unit> {
        return try {
            database.getReference("services").child(serviceId).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete service")
        }
    }
}
