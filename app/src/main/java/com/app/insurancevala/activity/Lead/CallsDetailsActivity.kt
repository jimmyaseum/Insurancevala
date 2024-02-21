package com.app.insurancevala.activity.Lead

import android.os.Bundle
import android.view.View
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.model.response.*
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_call_details.*
import kotlinx.android.synthetic.main.activity_call_details.imgBack
import kotlinx.android.synthetic.main.activity_call_details.layout
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallsDetailsActivity  : BaseActivity(), View.OnClickListener {

    var LeadID: Int? = null
    var CallGUID: String? = null
    var sharedPreference: SharedPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_details)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()

    }

    private fun getIntentData() {
        LeadID = intent.getIntExtra("LeadID",0)
        CallGUID = intent.getStringExtra("CallGUID")
    }

    override fun initializeView() {
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)

        if (isOnline(this)) {
            callManageCallGUID()
        } else {
            internetErrordialog(this@CallsDetailsActivity)
        }
    }

    private fun callManageCallGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETBYGUID)
        jsonObject.put("CallGUID", CallGUID)

        val call = ApiUtils.apiInterface.ManageCalls(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CallsResponse> {
            override fun onResponse(call: Call<CallsResponse>, response: Response<CallsResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListLead = response.body()?.Data!!
                        setAPIData(arrayListLead[0])
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<CallsResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun setAPIData(model: CallsModel) {

        if(model.CallType != null && model.CallType != "") {
            txtCallType.setText(model.CallType)
        }
        if(model.CallStatus != null && model.CallStatus != "") {
            txtStatus.setText(model.CallStatus)
            if(model.CallStatus.equals("Scheduled")) {
                LLResult.gone()
                LLFollowupDate.gone()
                LLFollowupNotes.gone()
            } else {
                LLResult.visible()
                LLFollowupDate.visible()
                LLFollowupNotes.visible()
            }
        }
        if(model.CallDate != null && model.CallDate != "") {
            txtDate.setText(model.CallDate)
        }
        if(model.CallPurpose != null && model.CallPurpose != "") {
            txtPurpose.setText(model.CallPurpose)
        }
        if(model.CallResult != null && model.CallResult != "") {
            txtResult.setText(model.CallResult)
        }
        if(model.Subject != null && model.Subject != "") {
            txtSubject.setText(model.Subject)
        }
        if(model.Agenda != null && model.Agenda != "") {
            txtAgenda.setText(model.Agenda)
        }
        if(model.Description != null && model.Description != "") {
            txtDescription.setText(model.Description)
        }
        if(model.FollowupDate != null && model.FollowupDate != "") {
            txtFollowupDate.setText(model.FollowupDate)
        }
        if(model.FollowupNotes != null && model.FollowupNotes != "") {
            txtFollowupNotes.setText(model.FollowupNotes)
        }

    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }
        }
    }

}