package com.example.hamrolaundryapp.model

data class UserModel(
    val id: String="",
    val email: String="",
    val name: String="",
    val password: String="",
    val address: String="",
    val contact: String="", // contractor
){
    fun toMap(): Map<String,Any?>{
        return mapOf(
            "name" to name,
            "email" to email,
            "address" to address,
            "contact" to contact,

        )
    }
    // function "calculate" return type


}