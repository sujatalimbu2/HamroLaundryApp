package com.example.hamrolaundryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamrolaundryapp.repo.ProfileRepo
import com.example.hamrolaundryapp.model.UserModel
import com.example.hamrolaundryapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepo = ProfileRepo()) : ViewModel() {

    private val _userProfile = MutableStateFlow<Resource<UserModel>>(Resource.Loading())
    val userProfile: StateFlow<Resource<UserModel>> = _userProfile

    private val _updateResult = MutableStateFlow<Resource<Unit>?>(null)
    val updateResult: StateFlow<Resource<Unit>?> = _updateResult

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            _userProfile.value = Resource.Loading()
            val result = repository.getUserProfile()
            _userProfile.value = result
        }
    }

    fun updateProfile(name: String, address: String, contact: String) {
        val currentUser = (_userProfile.value as? Resource.Success)?.data ?: return
        val updatedUser = currentUser.copy(
            name = name,
            address = address,
            contact = contact
        )

        viewModelScope.launch {
            _updateResult.value = Resource.Loading()
            val result = repository.updateUserProfile(updatedUser)
            _updateResult.value = result
            if (result is Resource.Success) {
                _userProfile.value = Resource.Success(updatedUser)
            }
        }
    }

    fun logout() {
        repository.logout()
    }
}
