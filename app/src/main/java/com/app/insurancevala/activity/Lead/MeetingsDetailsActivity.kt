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
import com.app.insurancevala.model.response.MeetingsByGUIDResponse
import com.app.insurancevala.model.response.MeetingsModel
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_meeting_details.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MeetingsDetailsActivity  : BaseActivity(), View.OnClickListener {

    var arrayListAttachment: ArrayList<DocumentsModel>? = null
    lateinit var adapter: AttachmentListAdapter

    var LeadID: Int? = 0
    var ID: Int? = 0
    var MeetingGUID: String? = ""
    var Type: String? = ""
    var sharedPreference: SharedPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_details)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()

    }

    private fun getIntentData() {
        if (intent.hasExtra("Type")) {
            Type = intent.getStringExtra("Type")
        }
        LeadID = intent.getIntExtra("LeadID",0)
        ID = intent.getIntExtra("ID",0)
        MeetingGUID = intent.getStringExtra("MeetingGUID")

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(applicationContext)
        }
        AppConstant.TOKEN =
            sharedPreference?.getPreferenceString(PrefConstants.PREF_TOKEN).toString()
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
            callManageMeetingGUID()
        } else {
            internetErrordialog(this@MeetingsDetailsActivity)
        }
    }
    private fun callManageMeetingGUID() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("NBInquiryTypeID", ID)
        jsonObject.put("MeetingGUID", MeetingGUID)

        val call = ApiUtils.apiInterface.ManageMeetingFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<MeetingsByGUIDResponse> {
            override fun onResponse(call: Call<MeetingsByGUIDResponse>, response: Response<MeetingsByGUIDResponse>) {
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

            override fun onFailure(call: Call<MeetingsByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun setAPIData(model: MeetingsModel) {

        if(model.MeetingDate != null && model.MeetingDate != "") {
            txtDate.setText(model.MeetingDate)
        }
        if(model.Purpose != null && model.Purpose != "") {
            txtPurpose.setText(model.Purpose)
        }
        if(model.MeetingType != null && model.MeetingType != "") {
            txtType.setText(model.MeetingType)
        }
        if(model.StartTime != null && model.Purpose != "" && model.EndTime != null && model.EndTime != "") {
            val mStime = convertDateStringToString(model.StartTime!! , AppConstant.HH_MM_SS_FORMAT, AppConstant.HH_MM_AA_FORMAT)
            val mEtime = convertDateStringToString(model.EndTime!! , AppConstant.HH_MM_SS_FORMAT, AppConstant.HH_MM_AA_FORMAT)
            txtTime.text = mStime + " to " + mEtime
        }
        if(model.MeetingStatus != null && model.MeetingStatus != "") {
            txtStatus.setText(model.MeetingStatus)
        }
        if(model.MeetingOutcome != null && model.MeetingOutcome != "") {
            txtOutcome.setText(model.MeetingOutcome)
        }
        if(model.Description != null && model.Description != "") {
            txtDescription.setText(model.Description)
        }
        if(model.Attendee != null && model.Attendee != "") {
            txtAttendee.setText(model.Attendee)
        }
        if(model.FollowupDate != null && model.FollowupDate != "") {
            txtFollowupDate.setText(model.FollowupDate)
        }
        if(model.FollowupNotes != null && model.FollowupNotes != "") {
            txtFollowupNotes.setText(model.FollowupNotes)
        }
        if(!model.MeetingAttachmentList.isNullOrEmpty() && model.MeetingAttachmentList.size > 0) {
            val arrayListAttachment = model.MeetingAttachmentList!!
            adapter = AttachmentListAdapter(this@MeetingsDetailsActivity, arrayListAttachment)
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
}