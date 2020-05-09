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

    fun addUpdateTaskList(activity: TestListActivity,board: Board)
    {
        val taskListHashMap = HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS).document(board.documentId).update(taskListHashMap).addOnSuccessListener {
            Log.e(activity.javaClass.simpleName,"Tasklist updated successfully")
            activity.addUpdateTaskListSuccess()
        }.addOnFailureListener {
            exception ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error while creating a board",exception)
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
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
            activity.boardDetails(board)
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
    fun getAssignedMembersListDetails(activity: MembersActivity, assignedTo: ArrayList<String>) {

        mFireStore.collection(Constants.USERS) // Collection Name
            .whereIn(Constants.ID, assignedTo) // Here the database field name and the id's of the members.
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val usersList: ArrayList<User> = ArrayList()

                for (i in document.documents) {
                    // Convert all the document snapshot to the object using the data model class.
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }

                activity.setupMembersList(usersList)
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
    fun getMemberDetails(activity: MembersActivity, email: String)
    {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                if (document.documents.size > 0)
                {
                    val user = document.documents[0].toObject(User::class.java)!!
                    // Here call a function of base activity for transferring the result to it.
                    activity.memberDetails(user)
                } else
                {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found.")
                }

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details",
                    e
                )
            }
    }

    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User) {

        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully.")
                activity.memberAssignSuccess(user)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }
    // E
}