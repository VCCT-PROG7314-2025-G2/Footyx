package com.example.footyxapp.data.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.footyxapp.data.classes.User
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel(application: Application): AndroidViewModel(application) {
    private val firestore = FirebaseFirestore.getInstance()
    private val userCollection = firestore.collection("users")


    // Insert user into Firestore
    fun insertUser(user : User): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        userCollection.document(user.uid ?: "")
            .set(user)
            .addOnSuccessListener { result.value = true }
            .addOnFailureListener { result.value = false }
        return result
    }

    fun validateLogin(email: String, password: String): LiveData<User?>{
        val result = MutableLiveData<User?>()

        userCollection
            .whereEqualTo("email",email)
            .whereEqualTo("password",password)
            .get()
            .addOnSuccessListener { documents ->
                if(!documents.isEmpty){
                    val user = documents.first().toObject(User::class.java)
                    result.value = user
                }else{
                    result.value = null
                }
            }
            .addOnFailureListener {
                result.value = null
            }
        return result
    }

    fun updateUser(uid: String, name: String?, email: String?, password: String?): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val updates = mutableMapOf<String, Any>()

        if (!name.isNullOrBlank()) {
            updates["name"] = name
        }
        if (!email.isNullOrBlank()) {
            updates["email"] = email.lowercase()
        }
        if (!password.isNullOrBlank()) {
            updates["password"] = password
        }

        if (updates.isEmpty()) {
            result.value = true
            return result
        }

        userCollection.document(uid)
            .update(updates)
            .addOnSuccessListener { result.value = true }
            .addOnFailureListener { result.value = false }
        return result
    }
}