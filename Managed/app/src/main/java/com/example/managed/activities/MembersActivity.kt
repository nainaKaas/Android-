package com.example.managed.activities

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.managed.R
import com.example.managed.adapters.MemberListItemsAdapter
import com.example.managed.firebase.FirestoreClass
import com.example.managed.models.Board
import com.example.managed.models.User
import com.example.managed.utils.Constants
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.dialog_search_member.*

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private var anyChangesDone: Boolean = false
    private lateinit var mAssignedMembersList:ArrayList<User>


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if (intent.hasExtra(Constants.BOARDS_DETAIL))
        {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARDS_DETAIL)!!
        }

        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(
            this@MembersActivity,
            mBoardDetails.assignedTo
        )
    }

    private fun setupActionBar()
    {


        setSupportActionBar(toolbar_members_activity)

        val actionBar = supportActionBar
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        }

        toolbar_members_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {

        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_add_member -> {

                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setupMembersList(list: ArrayList<User>) {

        mAssignedMembersList = list

        hideProgressDialog()

        rv_members_list.layoutManager = LinearLayoutManager(this@MembersActivity)
        rv_members_list.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this@MembersActivity, list)
        rv_members_list.adapter = adapter
    }


    private fun dialogSearchMember() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_search_member)
        dialog.tv_add.setOnClickListener(View.OnClickListener {

            val email = dialog.et_email_search_member.text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()

                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this@MembersActivity, email)

            } else {
                showErrorSnackBar("Please enter members email address.")
                /*Toast.makeText(
                    this@MembersActivity,
                    "Please enter members email address.",
                    Toast.LENGTH_SHORT
                ).show()*/
            }
        })
        dialog.tv_cancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        dialog.show()
    }

    fun memberDetails(user: User) {

        mBoardDetails.assignedTo.add(user.id)


        FirestoreClass().assignMemberToBoard(this@MembersActivity, mBoardDetails, user)

    }

    override fun onBackPressed() {
        if (anyChangesDone) {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    fun memberAssignSuccess(user: User) {

        hideProgressDialog()

        mAssignedMembersList.add(user)
        anyChangesDone = true
        setupMembersList(mAssignedMembersList)
    }

}