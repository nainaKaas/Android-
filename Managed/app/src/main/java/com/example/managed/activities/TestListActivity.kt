package com.example.managed.activities

import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.quicksettings.Tile
import com.example.managed.R
import com.example.managed.firebase.FirestoreClass
import com.example.managed.models.Board
import com.example.managed.utils.Constants
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_test_list.*

class TestListActivity : BaseActivity() {

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
            actionBar.title = title
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        }

        toolbar_task_list_activity.setNavigationOnClickListener { onBackPressed() }
    }
    fun boardDetails(board : Board)
    {
        hideProgressDialog()
        setupActionBar(board.name)
    }
}
