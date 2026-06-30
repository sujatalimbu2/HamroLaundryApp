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

    private val _userRole = MutableStateFlow("user")
    val userRole: StateFlow<String> = _userRole

    private val _popularServices = MutableStateFlow<Resource<List<LaundryService>>>(Resource.Loading())
    val popularServices: StateFlow<Resource<List<LaundryService>>> = _popularServices

    private val _allServices = MutableStateFlow<Resource<List<LaundryService>>>(Resource.Loading())
    val allServices: StateFlow<Resource<List<LaundryService>>> = _allServices

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<LaundryService>>(emptyList())
    val searchResults: StateFlow<List<LaundryService>> = _searchResults

    init {
        getUserName()
        getUserRole()
        fetchPopularServices()
        fetchAllServices()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterServices(query)
    }

    private fun filterServices(query: String) {
        val currentServices = (_allServices.value as? Resource.Success)?.data ?: emptyList()
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
        } else {
            _searchResults.value = currentServices.filter {
                it.name.contains(query, ignoreCase = true) || 
                it.category.contains(query, ignoreCase = true)
            }
        }
    }

    private fun getUserName() {
        _userName.value = repository.getUserName()
    }

    private fun getUserRole() {
        viewModelScope.launch {
            _userRole.value = repository.getUserRole()
        }
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
