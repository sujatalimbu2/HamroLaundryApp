package com.example.hamrolaundryapp.repo

import com.example.hamrolaundryapp.model.UserModel
import com.example.hamrolaundryapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class ProfileRepo(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun getUserProfile(): Resource<UserModel> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Guest")
            val snapshot = database.getReference("users").child(uid).get().await()
            val user = snapshot.getValue(UserModel::class.java)
            if (user != null) {
                Resource.Success(user)
            } else {
                Resource.Error("Profile not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch profile")
        }
    }

    suspend fun updateUserProfile(user: UserModel): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("User not logged in")
            database.getReference("users").child(uid).setValue(user).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update profile")
        }
    }

    fun logout() {
        auth.signOut()
    }
}
