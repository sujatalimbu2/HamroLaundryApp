package com.example.hamrolaundryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamrolaundryapp.model.Booking
import com.example.hamrolaundryapp.repo.BookingRepo
import com.example.hamrolaundryapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingConfirmationViewModel(private val repository: BookingRepo = BookingRepo()) : ViewModel() {

    private val _booking = MutableStateFlow<Resource<Booking>>(Resource.Loading())
    val booking: StateFlow<Resource<Booking>> = _booking

    fun fetchBooking(bookingId: String) {
        viewModelScope.launch {
            _booking.value = Resource.Loading()
            val result = repository.getBookingById(bookingId)
            _booking.value = result
        }
    }
}
