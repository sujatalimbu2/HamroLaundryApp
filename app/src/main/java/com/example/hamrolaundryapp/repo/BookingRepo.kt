package com.example.hamrolaundryapp.repo

import com.example.hamrolaundryapp.model.Booking
import com.example.hamrolaundryapp.model.LaundryService
import com.example.hamrolaundryapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class BookingRepo(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    suspend fun getServiceById(serviceId: String): Resource<LaundryService> {
        return try {
            val snapshot = database.getReference("services").child(serviceId).get().await()
            val service = snapshot.getValue(LaundryService::class.java)
            if (service != null) {
                Resource.Success(service)
            } else {
                Resource.Error("Service not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun createBooking(booking: Booking): Resource<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("Login Required")
            val ref = database.getReference("bookings").push()
            val bookingId = ref.key ?: return Resource.Error("Failed to generate ID")
            val finalBooking = booking.copy(id = bookingId, userId = userId)
            ref.setValue(finalBooking).await()
            Resource.Success(bookingId)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to book service")
        }
    }

    suspend fun getBookingById(bookingId: String): Resource<Booking> {
        return try {
            val snapshot = database.getReference("bookings").child(bookingId).get().await()
            val booking = snapshot.getValue(Booking::class.java)
            if (booking != null) {
                Resource.Success(booking)
            } else {
                Resource.Error("Booking not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch booking")
        }
    }
}
