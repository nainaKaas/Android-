package com.example.managed.firebase

import android.app.Activity
import android.util.Log
import com.example.managed.activities.MainActivity
import com.example.managed.activities.SignInActivity
import com.example.managed.activities.SignUpActivity
import com.example.managed.models.User
import com.example.managed.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    fun signInUser(activity: Activity)
    {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener {document ->

                val loggedInUser = document.toObject(User::class.java)!!

                when(activity)
                {
                    is SignInActivity ->
                    {
                        activity.signInSuccess(loggedInUser)
                    }

                    is MainActivity ->
                    {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                }
            }.addOnFailureListener {

                    e ->
                when(activity)
                {
                    is SignInActivity ->
                    {
                        activity.hideProgressDialog()
                    }

                    is MainActivity ->
                    {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    "SignInUser",
                    "Error writing document",
                    e
                )
            }

    }
    fun getCurrentUserID(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID

    }
}