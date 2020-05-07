package com.example.managed.activities

import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.quicksettings.Tile
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.managed.R
import com.example.managed.adapters.TaskListItemAdapter
import com.example.managed.firebase.FirestoreClass
import com.example.managed.models.Board
import com.example.managed.models.Card
import com.example.managed.models.Task
import com.example.managed.utils.Constants
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_test_list.*
import java.text.FieldPosition

class TestListActivity : BaseActivity() {

    private  lateinit var mBoardDetails: Board
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_list)
        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID))
        {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,boardDocumentId)
    }

    private fun setupActionBar(title: String)
    {

        setSupportActionBar(toolbar_task_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = mBoardDetails.name
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        }

        toolbar_task_list_activity.setNavigationOnClickListener { onBackPressed() }
    }
    fun boardDetails(board : Board)
    {
        mBoardDetails = board

        hideProgressDialog()
        setupActionBar(board.name)

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        rv_task_list.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv_task_list.setHasFixedSize(true)
        val adapter = TaskListItemAdapter(this,board.taskList)
        rv_task_list.adapter = adapter
    }

    fun addUpdateTaskListSuccesss()
    {
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,mBoardDetails.documentId)
    }

    fun createTaskList(taskListName : String)
    {
        val task = Task(taskListName,FirestoreClass().getCurrentUserID())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun updateTaskList(position: Int,listName : String,model : Task)
    {
        val task = Task(listName,model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1 )
    }

    fun deleteTaskList(position: Int)
    {
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this,mBoardDetails)

    }
    fun addCardToTaskList(position: Int,cardName: String)
    {
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val cardAssignedUserList:ArrayList<String> = ArrayList()
        cardAssignedUserList.add(FirestoreClass().getCurrentUserID())

        val card = Card(cardName,FirestoreClass().getCurrentUserID(),cardAssignedUserList)

        val cardsList = mBoardDetails.taskList[position].cards

        cardsList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )

        mBoardDetails.taskList[position] = task
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }
}
