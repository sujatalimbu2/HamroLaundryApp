package com.example.hamrolaundryapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.c38.repo.UserRepo
import com.example.hamrolaundryapp.model.UserModel

// inter with repo and not with database
class UserViewModel(val repo : UserRepo): ViewModel() {
    // making loading whil
    private val _loading = MutableLiveData<UserModel?>()
    val loading : MutableLiveData<UserModel?> get() = _loading

    private val _users = MutableLiveData<UserModel?>()
    val users : MutableLiveData<UserModel?> get() = _users
    fun login(
        email: String, password: String,
        callback: ( Boolean, String) -> Unit
    ){
        repo.login(email,password)
    }

    // authentication
    fun register(
        email: String, password: String,
        callback: ( Boolean, String, String) -> Unit
    ) {
        repo.register(email, password, callback)
    }

    // realtime
    fun addUser(
        id: String, model: UserModel,
        callback: ( Boolean, String) -> Unit
    ){
        repo.addUser(id, model, callback)
    }

    fun forgetPassword(
        email: String,
        callback: ( Boolean, String) -> Unit
    ){
        repo.forgetPassword(email, callback)
    }

    fun getUserById(
        id: String
    ){
        _loading.value = true
        repo.getUserById(id){
            success,msg,data->
            if(success){
                _users.value = data
                _loading.value = false
            }else {
                _users.value = null
                _loading.value =false
            }
        }
    }

    fun editProfile(
        id:String,model: UserModel,
        callback: (Boolean, String) -> Unit
    ){
        repo.editProfile(id, model, callback)
    }

    private val _allUsers = MutableLiveData<List<UserModel>?>()
    val allUsers : MutableLiveData<List<UserModel>?> get() = _allUsers

    fun getAllUser() {
        repo.getAllUser { success, message, data ->
            if(success){
                _loading.value = false
                _allUsers.value = data
            }else{
                _loading.value = false
                _allUsers.value = emptyList()
            }
        }
    }

    fun logout(
        callback: ( Boolean, String) -> Unit
    ){
        repo.logout(callback);
    }

    fun deleteUser(
        id:String, callback:( Boolean, String) -> Unit
    ){
        repo.deleteUser(id,callback)
    }

}