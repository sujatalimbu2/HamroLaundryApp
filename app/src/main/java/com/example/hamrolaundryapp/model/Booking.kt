package com.example.hamrolaundryapp.model

import com.google.firebase.Timestamp

data class Booking(
    val id: String = "",
    val userId: String = "",
    val serviceId: String = "",
    val serviceName: String = "",
    val quantity: Int = 1,
    val clothingType: String = "",
    val pickupDate: Timestamp? = null,
    val deliveryDate: Timestamp? = null,
    val pickupAddress: String = "",
    val specialInstructions: String = "",
    val totalPrice: Double = 0.0,
    val status: String = "Pending", // Pending, Pickup Scheduled, Collected, Washing, Ironing, Out for Delivery, Completed
    val createdAt: Timestamp = Timestamp.now()
)