package com.example.hamrolaundryapp.repo

import com.example.hamrolaundryapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepoImpl : UserRepo {

    val auth by lazy { FirebaseAuth.getInstance() }
    val database by lazy { FirebaseDatabase.getInstance() }
    val ref by lazy { database.getReference("users") }

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Login successful")
                } else {
                    callback(false, it.exception?.message ?: "Login failed")
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Registration successful", auth.currentUser?.uid ?: "")
                } else {
                    callback(false, it.exception?.message ?: "Registration failed", "")
                }
            }
    }

    override fun addUser(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "User registered")
            } else {
                callback(false, it.exception?.message ?: "Failed to save user")
            }
        }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Reset link sent to $email")
            } else {
                callback(false, it.exception?.message ?: "Failed to send reset link")
            }
        }
    }

    override fun getUserById(
        id: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        ref.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    callback(true, "User fetched", user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun editProfile(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Profile updated successfully")
            } else {
                callback(false, it.exception?.message ?: "Update failed")
            }
        }
    }

    override fun getAllUser(callback: (Boolean, String, List<UserModel>) -> Unit) {
        // Implementation if needed
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logout successful")
        } catch (e: Exception) {
            callback(false, e.localizedMessage ?: "Logout failed")
        }
    }

    override fun deleteUser(
        id: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Account deleted successfully")
            } else {
                callback(false, it.exception?.message ?: "Delete failed")
            }
        }
    }
}
