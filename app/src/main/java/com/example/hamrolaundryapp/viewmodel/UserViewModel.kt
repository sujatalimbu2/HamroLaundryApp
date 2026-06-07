package com.example.hamrolaundryapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hamrolaundryapp.repo.UserRepo
import com.example.hamrolaundryapp.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel(val repo : UserRepo): ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _users = MutableStateFlow<UserModel?>(null)
    val users: StateFlow<UserModel?> = _users.asStateFlow()

    private val _allUsers = MutableStateFlow<List<UserModel>?>(null)
    val allUsers: StateFlow<List<UserModel>?> = _allUsers.asStateFlow()

    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        _loading.value = true
        repo.login(email, password) { success, msg ->
            _loading.value = false
            callback(success, msg)
        }
    }

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        _loading.value = true
        repo.register(email, password) { success, msg, userId ->
            _loading.value = false
            callback(success, msg, userId)
        }
    }

    fun addUser(id: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        _loading.value = true
        repo.addUser(id, model) { success, msg ->
            _loading.value = false
            callback(success, msg)
        }
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        _loading.value = true
        repo.forgetPassword(email) { success, msg ->
            _loading.value = false
            callback(success, msg)
        }
    }

    fun getUserById(id: String) {
        _loading.value = true
        repo.getUserById(id) { success, msg, data ->
            _loading.value = false
            if (success) {
                _users.value = data
            } else {
                _users.value = null
            }
        }
    }

    fun editProfile(id: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        _loading.value = true
        repo.editProfile(id, model) { success, msg ->
            _loading.value = false
            callback(success, msg)
        }
    }

    fun getAllUser() {
        _loading.value = true
        repo.getAllUser { success, message, data ->
            _loading.value = false
            if (success) {
                _allUsers.value = data
            } else {
                _allUsers.value = emptyList()
            }
        }
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        repo.logout(callback)
    }

    fun deleteUser(id: String, callback: (Boolean, String) -> Unit) {
        _loading.value = true
        repo.deleteUser(id) { success, msg ->
            _loading.value = false
            callback(success, msg)
        }
    }
}
