package com.example.managed.activities

import android.app.Activity
import android.content.Intent
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.quicksettings.Tile
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.managed.R
import com.example.managed.adapters.TaskListItemAdapter
import com.example.managed.firebase.FirestoreClass
import com.example.managed.models.Board
import com.example.managed.models.Card
import com.example.managed.models.Task
import com.example.managed.models.User
import com.example.managed.utils.Constants
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_test_list.*
import java.text.FieldPosition

class TestListActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private lateinit var mBoardDocumentId: String
    lateinit var mAssignedMemberDetailList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_list)

        if (intent.hasExtra(Constants.DOCUMENT_ID))
        {
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this@TestListActivity, mBoardDocumentId)
    }


    private fun setupActionBar() {

        setSupportActionBar(toolbar_task_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
            actionBar.title = mBoardDetails.name
        }

        toolbar_task_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_members -> {

                val intent = Intent(this@TestListActivity, MembersActivity::class.java)
                intent.putExtra(Constants.BOARDS_DETAIL, mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && (requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE)
        ) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetails(this@TestListActivity, mBoardDocumentId)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }


    fun boardDetails(board: Board) {

        mBoardDetails = board

        hideProgressDialog()

        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(
            this@TestListActivity,
            mBoardDetails.assignedTo
        )
    }

    fun createTaskList(taskListName: String) {

        Log.e("Task List Name", taskListName)

        // Create and Assign the task details
        val task = Task(taskListName, FirestoreClass().getCurrentUserID())

        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TestListActivity, mBoardDetails)
    }

    fun updateTaskList(position: Int, listName: String, model: Task) {

        val task = Task(listName, model.createdBy)

        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TestListActivity, mBoardDetails)
    }

    fun deleteTaskList(position: Int) {

        mBoardDetails.taskList.removeAt(position)

        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TestListActivity, mBoardDetails)
    }

    fun addUpdateTaskListSuccess() {

        hideProgressDialog()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this@TestListActivity, mBoardDetails.documentId)
    }

    fun addCardToTaskList(position: Int, cardName: String) {

        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FirestoreClass().getCurrentUserID())

        val card = Card(cardName, FirestoreClass().getCurrentUserID(), cardAssignedUsersList)

        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )

        mBoardDetails.taskList[position] = task


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TestListActivity, mBoardDetails)
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int) {
        val intent = Intent(this@TestListActivity, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARDS_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST,mAssignedMemberDetailList)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    fun boardMembersDetailList(list: ArrayList<User>) {

        mAssignedMemberDetailList = list

        hideProgressDialog()

        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)

        rv_task_list.layoutManager =
            LinearLayoutManager(this@TestListActivity, LinearLayoutManager.HORIZONTAL, false)
        rv_task_list.setHasFixedSize(true)

        val adapter = TaskListItemAdapter(this@TestListActivity, mBoardDetails.taskList)
        rv_task_list.adapter = adapter
    }

    fun updateCardsInTaskList(taskListPosition: Int, cards: ArrayList<Card>) {

        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        mBoardDetails.taskList[taskListPosition].cards = cards

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TestListActivity, mBoardDetails)
    }

    companion object {

        const val MEMBERS_REQUEST_CODE: Int = 13

        const val CARD_DETAILS_REQUEST_CODE: Int = 14
    }
}