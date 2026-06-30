package com.example.hamrolaundryapp.model

data class UserModel(
    val id: String="",
    val email: String="",
    val name: String="",
    val password: String="",
    val address: String="",
    val contact: String="",
    val role: String = "user" // "admin" or "user"
){
    fun toMap(): Map<String,Any?>{
        return mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "address" to address,
            "contact" to contact,
            "role" to role
        )
    }
    // function "calculate" return type


}