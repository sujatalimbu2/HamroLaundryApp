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

class HomeViewModel(private val repository: HomeRepo = HomeRepo()) : ViewModel() {

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _popularServices = MutableStateFlow<Resource<List<LaundryService>>>(Resource.Loading())
    val popularServices: StateFlow<Resource<List<LaundryService>>> = _popularServices

    private val _allServices = MutableStateFlow<Resource<List<LaundryService>>>(Resource.Loading())
    val allServices: StateFlow<Resource<List<LaundryService>>> = _allServices

    init {
        getUserName()
        fetchPopularServices()
        fetchAllServices()
    }

    private fun getUserName() {
        _userName.value = repository.getUserName()
    }

    private fun fetchPopularServices() {
        viewModelScope.launch {
            repository.getPopularServices().collectLatest {
                _popularServices.value = it
            }
        }
    }

    private fun fetchAllServices() {
        viewModelScope.launch {
            repository.getAllServices().collectLatest {
                _allServices.value = it
            }
        }
    }
}
