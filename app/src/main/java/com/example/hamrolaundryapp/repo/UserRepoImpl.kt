package com.example.hamrolaundryapp.repo

import com.example.hamrolaundryapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// class

class UserRepoImpl : UserRepo{

    // service we which
    val auth by lazy { FirebaseAuth.getInstance() }
    // database inter = repo  -fetch model
    // calling one string = user id return
    val database by lazy { FirebaseDatabase.getInstance() }
    val ref by lazy { database.getReference("users") }
    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                // kotlin ma return call back
                    callback(true,"Login successful")

                }else{
                    callback(false,"${it.exception?.message}")
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true,"Login successful","${auth.currentUser?.uid}")

                }else{
                    callback(false,"${it.exception?.message}","")
                }

            }

    }
    //Create RUD
    // to auto generate id
    //val id = ref.push().key.toString()
    override fun addUser(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
       ref.child(id).setValue(model).addOnCompleteListener {
           if (it.isSuccessful) {
               callback(true,"User registered")
           }else{
               callback(false,"${it.exception?.message}")
           }

       }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true,"Login Resent link sent to $email")

            }else{
                callback(false,"${it.exception?.message}")
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
//                        if(user !=null){
//                            callback(true,"user fetched", user)
//                        }
                      user.let {
                          callback(true,"user fetched", it)
                      }

              }
          }
                override fun onCancelled(error: DatabaseError) {
                    callback(false,error.message,null)
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
                callback(true,"Login successful")

            }else{
                callback(false,"${it.exception?.message}")
            }
        }

    }

    override fun getAllUser(callback: (Boolean, String, List<UserModel>) -> Unit) {

    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logout successful")
        }catch (e: Exception){
            callback(false, e.toString())
        }
    }

    override fun deleteUser(
        id: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true,"Account delete Successfully")

            }else{
                callback(false,"${it.exception?.message}")
            }
        }

    }

}