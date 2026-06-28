package com.example.hamrolaundryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamrolaundryapp.model.Booking
import com.example.hamrolaundryapp.model.LaundryService
import com.example.hamrolaundryapp.repo.BookingRepo
import com.example.hamrolaundryapp.utils.Resource
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class BookingViewModel(private val repository: BookingRepo = BookingRepo()) : ViewModel() {

    private val _service = MutableStateFlow<Resource<LaundryService>>(Resource.Loading())
    val service: StateFlow<Resource<LaundryService>> = _service

    private val _bookingResult = MutableStateFlow<Resource<String>?>(null)
    val bookingResult: StateFlow<Resource<String>?> = _bookingResult

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice

    fun getService(serviceId: String) {
        viewModelScope.launch {
            val result = repository.getServiceById(serviceId)
            _service.value = result
            if (result is Resource.Success) {
                calculatePrice(result.data?.price ?: 0.0, _quantity.value)
            }
        }
    }

    fun updateQuantity(newQuantity: Int) {
        _quantity.value = newQuantity
        val serviceData = (_service.value as? Resource.Success)?.data
        calculatePrice(serviceData?.price ?: 0.0, newQuantity)
    }

    private fun calculatePrice(pricePerItem: Double, qty: Int) {
        _totalPrice.value = pricePerItem * qty
    }

    fun bookLaundry(
        clothingType: String,
        pickupDate: Date,
        deliveryDate: Date,
        address: String,
        instructions: String
    ) {
        val serviceData = (_service.value as? Resource.Success)?.data ?: return
        
        val booking = Booking(
            serviceId = serviceData.id,
            serviceName = serviceData.name,
            quantity = _quantity.value,
            clothingType = clothingType,
            pickupDate = Timestamp(pickupDate),
            deliveryDate = Timestamp(deliveryDate),
            pickupAddress = address,
            specialInstructions = instructions,
            totalPrice = _totalPrice.value
        )

        viewModelScope.launch {
            _bookingResult.value = Resource.Loading()
            val result = repository.createBooking(booking)
            _bookingResult.value = result
        }
    }
}
