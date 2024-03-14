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
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.LeadTypeByGUIDResponse
import com.app.insurancevala.model.response.LeadTypeModel
import com.app.insurancevala.model.response.LeadTypeResponse
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
import kotlinx.android.synthetic.main.activity_add_lead_type.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLeadTypeActivity : BaseActivity(), View.OnClickListener {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var LeadTypeGUID: String? = null

    var arrayListStatus: ArrayList<SingleSelectionModel>? = ArrayList()

    var mLeadtype: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lead_type)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
        LeadTypeGUID = intent.getStringExtra("LeadTypeGUID")

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun initializeView() {
        if (state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Add Lead Type"
            setMasterData()
        } else if (state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update Lead Type"

            if (isOnline(this)) {
                callManageLeadTypesGUID()
            } else {
                internetErrordialog(this@AddLeadTypeActivity)
            }
        }

        SetInitListner()

        arrayListStatus?.add(SingleSelectionModel(0, "Active", true))
        arrayListStatus?.add(SingleSelectionModel(1, "InActive", false))
    }

    private fun setMasterData() {
        if (isOnline(this)) {
        } else {
            internetErrordialog(this@AddLeadTypeActivity)
        }
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        edtStatus.setOnClickListener(this)
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

            R.id.edtStatus -> {
                preventTwoClick(v)
                if (!arrayListStatus.isNullOrEmpty()) {
                    selectLeadTypeDialog()
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
                    ManageLeadTypeAPI()
                } else {
                    internetErrordialog(this@AddLeadTypeActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isvalidate = true

        if (edtLeadType.text.toString().trim().isEmpty()) {
            edtLeadType.setError(getString(R.string.error_empty_lead_type), errortint(this))
            isvalidate = false
        }
        if (edtStatus.text.toString().trim().isEmpty()) {
            edtStatus.setError(getString(R.string.error_empty_type), errortint(this))
            isvalidate = false
        }

        return isvalidate
    }

    private fun ManageLeadTypeAPI() {

        showProgress()

        val call: Call<CommonResponse>

        val jsonObject = JSONObject()
        jsonObject.put("LeadType", edtLeadType.text.toString().trim())

        if (edtStatus.text.toString().trim() == "Active") {
            jsonObject.put("IsActive", true)
        } else {
            jsonObject.put("IsActive", false)
        }

        if (state.equals(AppConstant.S_EDIT)){
            jsonObject.put("LeadTypeGUID", LeadTypeGUID)
        }

        if (state.equals(AppConstant.S_ADD)) {
            call = ApiUtils.apiInterface.ManageLeadTypeInsert(getRequestJSONBody(jsonObject.toString()))
        } else {
            call = ApiUtils.apiInterface.ManageLeadTypeUpdate(getRequestJSONBody(jsonObject.toString()))
        }

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 201) {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    } else if (response.body()?.Status == 200) {
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                hideProgress()
            }
        })
    }

    private fun callManageLeadTypesGUID() {

        showProgress()

        val jsonObject = JSONObject()
        jsonObject.put("LeadTypeGUID", LeadTypeGUID)

        val call = ApiUtils.apiInterface.ManageLeadTypeFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<LeadTypeByGUIDResponse> {
            override fun onResponse(
                call: Call<LeadTypeByGUIDResponse>,
                response: Response<LeadTypeByGUIDResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListLead = response.body()?.Data!!
                        setAPIData(arrayListLead)
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<LeadTypeByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectLeadTypeDialog() {
    var dialogSelectLeadType = Dialog(this)
    dialogSelectLeadType.requestWindowFeature(Window.FEATURE_NO_TITLE)

    val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
    dialogSelectLeadType.setContentView(dialogView)

    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialogSelectLeadType.window!!.attributes)

    dialogSelectLeadType.window!!.attributes = lp
    dialogSelectLeadType.setCancelable(true)
    dialogSelectLeadType.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    dialogSelectLeadType.window!!.setLayout(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    dialogSelectLeadType.window!!.setGravity(Gravity.CENTER)

    val rvDialogCustomer =
        dialogSelectLeadType.findViewById(R.id.rvDialogCustomer) as RecyclerView
    val edtSearchId = dialogSelectLeadType.findViewById(R.id.edtSearchCustomer) as EditText
    val txtid = dialogSelectLeadType.findViewById(R.id.txtid) as TextView
    val imgClear = dialogSelectLeadType.findViewById(R.id.imgClear) as ImageView

    imgClear.setOnClickListener {
        dialogSelectLeadType.dismiss()
    }

    txtid.text = "Select Status"

    edtSearchId.gone()

        val itemAdapter = BottomSheetListAdapter(this, arrayListStatus!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(view: View, position: Int, type: Int) {

                itemAdapter.updateItem(position)
                mLeadtype = arrayListStatus!![position].Name.toString()
                edtStatus.setText(mLeadtype)
                dialogSelectLeadType.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        dialogSelectLeadType.show()
    }

    private fun setAPIData(model: LeadTypeModel) {

        if (model.LeadType != null && model.LeadType != "") {
            edtLeadType.setText(model.LeadType)
        }
        if (model.IsActive != null) {
            if (model.IsActive) {
                edtStatus.setText("Active")
            } else {
                edtStatus.setText("InActive")
            }
        }
    }


}