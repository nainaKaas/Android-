package com.example.managed.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.managed.R
import com.example.managed.models.Board
import com.example.managed.utils.Constants
import kotlinx.android.synthetic.main.activity_card_details.*

class CardDetailsActivity : BaseActivity() {
    private lateinit var mBoardDetail :Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        setupActionBar()
    }


    private fun setupActionBar() {

        setSupportActionBar(toolbar_card_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
            actionBar.title = mBoardDetail.taskList[mTaskListPosition].cards[mCardPosition].name
        }

        toolbar_card_details_activity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun getIntentData()
    {
        if(intent.hasExtra(Constants.BOARDS_DETAIL))
        {
            mBoardDetail = intent.getParcelableExtra(Constants.BOARDS_DETAIL)
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION))
        {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION))
        {
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }
    }
}
