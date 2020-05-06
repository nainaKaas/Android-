package com.example.managed.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.managed.activities.*
import com.example.managed.models.Board
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
                    "Error writing document", e)
            }
    }
    fun createBoard(activity: CreateBoardActivity,board: Board)
    {
        mFireStore.collection(Constants.BOARDS).document().set(board, SetOptions.merge()).addOnSuccessListener {
            Log.e(activity.javaClass.simpleName,"Noard created successfully.")

            Toast.makeText(activity,"Board created successfully.",Toast.LENGTH_SHORT).show()
            activity.boardCreatedSuccessfully()
        }.addOnFailureListener{
            exception ->
            activity.hideProgressDialog()
            Log.e(
                activity.javaClass.simpleName,
                "Error while creating a board.",exception
            )
        }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")

                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }
    fun loadUserData(activity: Activity,readsBoardList: Boolean = false)
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
                        activity.updateNavigationUserDetails(loggedInUser,readsBoardList)
                    }
                    is MyProfileActivity ->
                    {

                        activity.setUserDataInUI(loggedInUser)
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

    fun getBoardDetails(activity: TestListActivity,documentId : String)
    {
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                document ->
            Log.i(activity.javaClass.simpleName,document.toString())

            activity.boardDetails(document.toObject(Board::class.java)!!)
        }.addOnFailureListener {
                e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error while creating a board",e)
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

    fun getTheBoardList(activity: MainActivity)
    {
        mFireStore.collection(Constants.BOARDS).whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserID()).get().addOnSuccessListener {
            document ->
            Log.i(activity.javaClass.simpleName,document.documents.toString())

            val boardList : ArrayList<Board> = ArrayList()
            for(i in document.documents)
            {
                val board = i.toObject(Board::class.java)!!
                board.documentId = i.id
                boardList.add(board)
            }
            activity.populateBoardListToUI(boardList)
        }.addOnFailureListener {
            e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error while creating a board",e)
        }

    }
}