package com.example.hamrolaundryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamrolaundryapp.model.LaundryService
import com.example.hamrolaundryapp.repo.HomeRepo
import com.example.hamrolaundryapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ServiceViewModel(private val repository: HomeRepo = HomeRepo()) : ViewModel() {

    private val _services = MutableStateFlow<Resource<List<LaundryService>>>(Resource.Loading())
    val services: StateFlow<Resource<List<LaundryService>>> = _services

    init {
        fetchServices()
    }

    private fun fetchServices() {
        viewModelScope.launch {
            repository.getAllServices().collectLatest {
                _services.value = it
            }
        }
    }
}
