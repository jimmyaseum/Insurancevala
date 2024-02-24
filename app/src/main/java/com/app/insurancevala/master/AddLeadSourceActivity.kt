package com.app.insurancevala.master

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.LeadSourceModel
import com.app.insurancevala.model.response.LeadSourceResponse
import com.app.insurancevala.model.response.SingleSelectionModel
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.errortint
import com.app.insurancevala.utils.getRequestJSONBody
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.hideKeyboard
import com.app.insurancevala.utils.internetErrordialog
import com.app.insurancevala.utils.isOnline
import com.app.insurancevala.utils.preventTwoClick
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_lead_source.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLeadSourceActivity : BaseActivity(), View.OnClickListener {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var LeadSourceGUID: String? = null

    var arrayListStatus: ArrayList<SingleSelectionModel>? = ArrayList()
    var mLeadStatus: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lead_source)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
        LeadSourceGUID = intent.getStringExtra("LeadSourceGUID")

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun initializeView() {
        if (state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Add Lead Source"
            setMasterData()
        } else if (state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update Lead Source"

            if (isOnline(this)) {
                callManageLeadSourceGUID()
            } else {
                internetErrordialog(this@AddLeadSourceActivity)
            }
        }

        SetInitListner()

        arrayListStatus?.add(SingleSelectionModel(0, "true", true))
        arrayListStatus?.add(SingleSelectionModel(1, "false", false))
    }

    private fun setMasterData() {
        if (isOnline(this)) {
        } else {
            internetErrordialog(this@AddLeadSourceActivity)
        }
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        edtIsActive.setOnClickListener(this)
        txtButtonCancel.setOnClickListener(this)
        txtButtonSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                onBackPressed()
            }

            R.id.edtIsActive -> {
                preventTwoClick(v)
                if (!arrayListStatus.isNullOrEmpty()) {
                    selectLeadStatusDialog()
                }
            }

            R.id.imgProfilePic -> {
                preventTwoClick(v)
            }

            R.id.txtButtonSubmit -> {
                preventTwoClick(v)
                validation()
            }

            R.id.txtButtonCancel -> {
                preventTwoClick(v)
                onBackPressed()
            }
        }
    }

    private fun validation() {
        when (isValidate()) {
            true -> {
                if (isOnline(this)) {
                    ManageLeadSourceAPI()
                } else {
                    internetErrordialog(this@AddLeadSourceActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isvalidate = true

        if (edtLeadSource.text.toString().trim().isEmpty()) {
            edtLeadSource.setError(getString(R.string.error_empty_lead_source), errortint(this))
            isvalidate = false
        }
        if (edtIsActive.text.toString().trim().isEmpty()) {
            edtIsActive.setError(getString(R.string.error_empty_type), errortint(this))
            isvalidate = false
        }

        return isvalidate
    }

    private fun ManageLeadSourceAPI() {

        showProgress()

        val jsonObject = JSONObject()
        jsonObject.put("LeadSource", edtLeadSource.text.toString().trim())

        if (edtIsActive.text.toString().trim() == "true") {
            jsonObject.put("IsActive", true)
        } else {
            jsonObject.put("IsActive", false)
        }

        if (state.equals(AppConstant.S_ADD)) {
            jsonObject.put("OperationType", AppConstant.INSERT)
        } else if (state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("LeadSourceGUID", LeadSourceGUID)
            jsonObject.put("OperationType", AppConstant.EDIT)
        }

        val call = ApiUtils.apiInterface.ManageLeadSource(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<LeadSourceResponse> {
            override fun onResponse(
                call: Call<LeadSourceResponse>, response: Response<LeadSourceResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 201) {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    } else if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<LeadSourceResponse>, t: Throwable) {
                hideProgress()
            }
        })
    }

    private fun callManageLeadSourceGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETBYGUID)
        jsonObject.put("LeadSourceGUID", LeadSourceGUID)

        val call = ApiUtils.apiInterface.ManageLeadSource(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<LeadSourceResponse> {
            override fun onResponse(
                call: Call<LeadSourceResponse>, response: Response<LeadSourceResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListLead = response.body()?.Data!!
                        setAPIData(arrayListLead[0])
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<LeadSourceResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectLeadStatusDialog() {
        var dialogSelectLeadSource = Dialog(this)
        dialogSelectLeadSource.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectLeadSource.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectLeadSource.window!!.attributes)

        dialogSelectLeadSource.window!!.attributes = lp
        dialogSelectLeadSource.setCancelable(true)
        dialogSelectLeadSource.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectLeadSource.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectLeadSource.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectLeadSource.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchId = dialogSelectLeadSource.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectLeadSource.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectLeadSource.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectLeadSource.dismiss()
        }

        txtid.text = "Select Source"

        edtSearchId.gone()

        val itemAdapter = BottomSheetListAdapter(this, arrayListStatus!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(view: View, position: Int, Source: Int) {

                itemAdapter.updateItem(position)
                mLeadStatus = arrayListStatus!![position].Name.toString()
                edtIsActive.setText(mLeadStatus)
                dialogSelectLeadSource.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        dialogSelectLeadSource.show()
    }

    private fun setAPIData(model: LeadSourceModel) {

        if (model.LeadSource != null && model.LeadSource != "") {
            edtLeadSource.setText(model.LeadSource)
        }
        if (model.IsActive != null) {
            edtIsActive.setText(model.IsActive.toString())
        }
    }


}