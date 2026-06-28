package com.example.hamrolaundryapp.repo

import com.example.hamrolaundryapp.model.Booking
import com.example.hamrolaundryapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class HistoryRepo(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    fun getBookingHistory(): Flow<Resource<List<Booking>>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(Resource.Error("Login required to view history"))
            awaitClose()
            return@callbackFlow
        }

        trySend(Resource.Loading())
        val ref = database.getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val history = mutableListOf<Booking>()
                for (child in snapshot.children) {
                    val booking = child.getValue(Booking::class.java)
                    if (booking != null && booking.userId == uid) {
                        history.add(booking)
                    }
                }
                trySend(Resource.Success(history.reversed()))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
