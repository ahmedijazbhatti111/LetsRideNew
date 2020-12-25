package com.example.letsridenew.data

import com.example.letsridenew.utils.interfaces.OnUserChangeCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser

class AuthenticationManager {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    fun createUserWithEmailAndPassword(
        email: String?,
        password: String?,
        callback: OnUserChangeCallback
    ) {
        mAuth.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback.onSuccessCallback(
                        mAuth.currentUser!!.uid,
                        "You are know registered"
                    )
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        callback.onSuccessCallback(null, "You are already registered")
                    } else {
                        callback.onSuccessCallback(null, task.exception!!.message)
                    }
                }
            }
    }

    fun signInWithEmailAndPassword(
        email: String?,
        password: String?,
        callback: OnUserChangeCallback
    ) {
        mAuth.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback.onSuccessCallback(
                        mAuth.currentUser!!.uid,
                        "You are know login"
                    )
                } else {
                    callback.onSuccessCallback(null, task.exception!!.message)
                }
            }
    }

    fun signOut() {
        mAuth.signOut()
    }

    val user: FirebaseUser? get() = mAuth.currentUser

}