package com.app.insurancevala.activity.Lead

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.activity.DashBoard.HomeActivity
import com.app.insurancevala.adapter.AttachmentListAdapter
import com.app.insurancevala.model.pojo.DocumentsModel
import com.app.insurancevala.model.response.TasksByGUIDResponse
import com.app.insurancevala.model.response.TasksModel
import com.app.insurancevala.model.response.UserResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_task_details.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TasksDetailsActivity  : BaseActivity(), View.OnClickListener {

    var arrayListAttachment: ArrayList<DocumentsModel>? = null
    lateinit var adapter: AttachmentListAdapter

    var ID: Int? = 0
    var LeadID: Int? = 0
    var TaskGUID: String? = ""
    var Type: String? = ""
    var sharedPreference: SharedPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()

    }

    private fun getIntentData() {
        if (intent.hasExtra("Type")) {
            Type = intent.getStringExtra("Type")
        }
        ID = intent.getIntExtra("ID",0)
        LeadID = intent.getIntExtra("LeadID",0)
        TaskGUID = intent.getStringExtra("TaskGUID")

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(applicationContext)
        }
        AppConstant.TOKEN = sharedPreference?.getPreferenceString(PrefConstants.PREF_TOKEN).toString()
    }

    override fun initializeView() {
        SetInitListner()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(Type == ""){
            finish()
        } else {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun SetInitListner() {

        // attachment
        rvAttachment.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvAttachment.isNestedScrollingEnabled = false

        arrayListAttachment = ArrayList()
        adapter = AttachmentListAdapter(this, arrayListAttachment)
        rvAttachment.adapter = adapter

        imgBack.setOnClickListener(this)

        if (isOnline(this)) {
            callManageTaskGUID()
        } else {
            internetErrordialog(this@TasksDetailsActivity)
        }
    }

    private fun callManageTaskGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("NBInquiryTypeID", ID)
        jsonObject.put("TaskGUID", TaskGUID)

        val call = ApiUtils.apiInterface.ManageTasksFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<TasksByGUIDResponse> {
            override fun onResponse(call: Call<TasksByGUIDResponse>, response: Response<TasksByGUIDResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListLead = response.body()?.Data!!
                        setAPIData(arrayListLead)
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<TasksByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun setAPIData(model: TasksModel) {

        if(model.Subject != null && model.Subject != "") {
            txtSubject.setText(model.Subject)
        }
        if(model.DueDate != null && model.DueDate != "") {
            txtDueDate.setText(model.DueDate)
        }
        if(model.TaskStatus != null && model.TaskStatus != "") {
            txtStatusStatus.setText(model.TaskStatus)
        }
        if(model.Priority != null && model.Priority != "") {
            txtPriority.setText(model.Priority)
        }
        if(model.Description != null && model.Description != "") {
            txtDescription.setText(model.Description)
        }

        if(model.IsReminder != null) {
            if(model.IsReminder) {
                LLReminderVia.visible()
                LLReminderDate.visible()
                LLTaskRepeat.visible()
            } else {
                LLReminderVia.gone()
                LLReminderDate.gone()
                LLTaskRepeat.gone()
            }
        }

        if(model.Repeat != null && model.Repeat != 0) {
            var mTaskrepeat = ""
            if(model.Repeat == 1) {
                mTaskrepeat = "None"
            } else if(model.Repeat == 2) {
                mTaskrepeat = "Daily"
            } else if(model.Repeat == 3) {
                mTaskrepeat = "Weekly"
            } else if(model.Repeat == 4) {
                mTaskrepeat = "Monthly"
            } else if(model.Repeat == 5) {
                mTaskrepeat = "Yearly"
            }
            txtTaskRepeat.setText(mTaskrepeat)
        }
        if(model.NotifyVia != null && model.NotifyVia != 0) {
            var mTasknotifyvia = ""
            if(model.NotifyVia == 1) {
                mTasknotifyvia = "Email"
            } else if(model.NotifyVia == 2) {
                mTasknotifyvia = "Pop Up"
            } else if(model.NotifyVia == 3) {
                mTasknotifyvia = "Both"
            }
            txtNotifyvia.setText(mTasknotifyvia)
        }
        if(model.ReminderDate != null && model.ReminderDate != "") {
            txtReminderDate.setText(model.ReminderDate)
        }
        if(model.TaskOwnerID != null && model.TaskOwnerID != 0) {
            callManageUsers(model.TaskOwnerID)
        }

        if(!model.TasksAttachmentList.isNullOrEmpty() && model.TasksAttachmentList.size > 0) {
            val arrayListAttachment = model.TasksAttachmentList!!
            adapter = AttachmentListAdapter(this@TasksDetailsActivity, arrayListAttachment)
            rvAttachment.adapter = adapter

            LLAttachments.visible()
        } else {
            LLAttachments.gone()
        }

    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                onBackPressed()
            }
        }
    }

    private fun callManageUsers(taskOwnerID: Int) {

        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageUsers(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListUsers = response.body()?.Data!!

                        if(arrayListUsers!!.size > 0) {
                            for(i in 0 until arrayListUsers!!.size) {
                                if(arrayListUsers!![i].ID == taskOwnerID) {
                                    arrayListUsers!![i].IsSelected = true

                                    val mUsersName = arrayListUsers!![i].FirstName!! + " "+ arrayListUsers!![i].LastName!!
                                    txtTaskOwner.setText(mUsersName)
                                }
                            }
                        }
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {

                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }

}