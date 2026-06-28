package com.example.hamrolaundryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamrolaundryapp.model.Booking
import com.example.hamrolaundryapp.repo.HistoryRepo
import com.example.hamrolaundryapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: HistoryRepo = HistoryRepo()) : ViewModel() {

    private val _history = MutableStateFlow<Resource<List<Booking>>>(Resource.Loading())
    val history: StateFlow<Resource<List<Booking>>> = _history

    init {
        fetchHistory()
    }

    fun fetchHistory() {
        viewModelScope.launch {
            repository.getBookingHistory().collectLatest {
                _history.value = it
            }
        }
    }
}
