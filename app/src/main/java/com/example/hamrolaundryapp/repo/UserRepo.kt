package com.example.hamrolaundryapp.repo

import com.example.hamrolaundryapp.model.UserModel

interface UserRepo {
//    {
//        "success": true
    // "message": "login Success"
//    }

    // string? = have or not have
    // string = have

    fun login(email: String, password: String,
              callback: ( Boolean, String) -> Unit)
  // authentication
    fun register( email: String, password: String,callback: ( Boolean, String, String) -> Unit)// need two string

    //{
    // "success" : true
    // "message" : "register"
    // }

    // realtime
    fun addUser(id: String, model: UserModel,callback: ( Boolean, String) -> Unit )

    fun forgetPassword(email: String,
                       callback: ( Boolean, String) -> Unit)
    fun getUserById(id: String, callback: ( Boolean, String, UserModel?) -> Unit)

    fun editProfile(id:String,model: UserModel,
                    callback: (Boolean, String) -> Unit)

    fun getAllUser( callback: ( Boolean, String, List<UserModel>) -> Unit) // nothing to call one call multiple data
    fun logout( callback: ( Boolean, String) -> Unit)

    fun deleteUser(id:String, callback:( Boolean, String) -> Unit )
}