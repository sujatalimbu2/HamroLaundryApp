package com.example.hamrolaundryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamrolaundryapp.model.Booking
import com.example.hamrolaundryapp.model.LaundryService
import com.example.hamrolaundryapp.repo.AdminRepo
import com.example.hamrolaundryapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: AdminRepo = AdminRepo()) : ViewModel() {

    private val _allBookings = MutableStateFlow<Resource<List<Booking>>>(Resource.Loading())
    val allBookings: StateFlow<Resource<List<Booking>>> = _allBookings

    private val _updateResult = MutableStateFlow<Resource<Unit>?>(null)
    val updateResult: StateFlow<Resource<Unit>?> = _updateResult

    init {
        fetchAllBookings()
    }

    private fun fetchAllBookings() {
        viewModelScope.launch {
            repository.getAllBookings().collectLatest {
                _allBookings.value = it
            }
        }
    }

    fun updateStatus(bookingId: String, newStatus: String) {
        viewModelScope.launch {
            if (!repository.isAdmin()) {
                _updateResult.value = Resource.Error("Access Denied: Only admins can update status")
                return@launch
            }
            _updateResult.value = Resource.Loading()
            val result = repository.updateBookingStatus(bookingId, newStatus)
            _updateResult.value = result
        }
    }

    fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            if (!repository.isAdmin()) return@launch
            repository.deleteBooking(bookingId)
        }
    }

    fun saveService(service: LaundryService) {
        viewModelScope.launch {
            if (!repository.isAdmin()) {
                _updateResult.value = Resource.Error("Access Denied: Only admins can manage services")
                return@launch
            }
            _updateResult.value = Resource.Loading()
            val result = repository.addOrUpdateService(service)
            _updateResult.value = result
        }
    }

    fun deleteService(serviceId: String) {
        viewModelScope.launch {
            if (!repository.isAdmin()) return@launch
            repository.deleteService(serviceId)
        }
    }
}
