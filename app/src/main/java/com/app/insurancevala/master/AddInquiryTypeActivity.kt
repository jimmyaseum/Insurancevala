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
import com.app.insurancevala.model.response.InquiryTypeModel
import com.app.insurancevala.model.response.InquiryTypeResponse
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
import kotlinx.android.synthetic.main.activity_add_inquiry_type.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddInquiryTypeActivity : BaseActivity(), View.OnClickListener {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var InquiryTypeGUID: String? = null

    var arrayListStatus: ArrayList<SingleSelectionModel>? = ArrayList()

    var mInquirytype: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_inquiry_type)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
        InquiryTypeGUID = intent.getStringExtra("InquiryTypeGUID")

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun initializeView() {
        if (state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Add Inquiry Type"
            setMasterData()
        } else if (state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update Inquiry Type"

            if (isOnline(this)) {
                callManageInquiryTypesGUID()
            } else {
                internetErrordialog(this@AddInquiryTypeActivity)
            }
        }

        SetInitListner()

        arrayListStatus?.add(SingleSelectionModel(0, "Active", true))
        arrayListStatus?.add(SingleSelectionModel(1, "InActive", false))
    }

    private fun setMasterData() {
        if (isOnline(this)) {
        } else {
            internetErrordialog(this@AddInquiryTypeActivity)
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
                    selectInquiryTypeDialog()
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
                    ManageInquiryTypeAPI()
                } else {
                    internetErrordialog(this@AddInquiryTypeActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isvalidate = true

        if (edtInquiryType.text.toString().trim().isEmpty()) {
            edtInquiryType.setError(getString(R.string.error_empty_inquiry_type), errortint(this))
            isvalidate = false
        }
        if (edtStatus.text.toString().trim().isEmpty()) {
            edtStatus.setError(getString(R.string.error_empty_type), errortint(this))
            isvalidate = false
        }

        return isvalidate
    }

    private fun ManageInquiryTypeAPI() {

        showProgress()

        val jsonObject = JSONObject()
        jsonObject.put("InquiryType", edtInquiryType.text.toString().trim())

        if (edtStatus.text.toString().trim() == "Active") {
            jsonObject.put("IsActive", true)
        } else {
            jsonObject.put("IsActive", false)
        }

        if (state.equals(AppConstant.S_ADD)) {
            jsonObject.put("OperationType", AppConstant.INSERT)
        } else if (state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("InquiryTypeGUID", InquiryTypeGUID)
            jsonObject.put("OperationType", AppConstant.EDIT)
        }

        val call =
            ApiUtils.apiInterface.ManageInquiryType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<InquiryTypeResponse> {
            override fun onResponse(
                call: Call<InquiryTypeResponse>,
                response: Response<InquiryTypeResponse>
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

            override fun onFailure(call: Call<InquiryTypeResponse>, t: Throwable) {
                hideProgress()
            }
        })
    }

    private fun callManageInquiryTypesGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETBYGUID)
        jsonObject.put("InquiryTypeGUID", InquiryTypeGUID)

        val call =
            ApiUtils.apiInterface.ManageInquiryType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<InquiryTypeResponse> {
            override fun onResponse(
                call: Call<InquiryTypeResponse>,
                response: Response<InquiryTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListLead = response.body()?.Data!!
                        setAPIData(arrayListLead[0])
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<InquiryTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectInquiryTypeDialog() {
    var dialogSelectInquiryType = Dialog(this)
    dialogSelectInquiryType.requestWindowFeature(Window.FEATURE_NO_TITLE)

    val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
    dialogSelectInquiryType.setContentView(dialogView)

    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialogSelectInquiryType.window!!.attributes)

    dialogSelectInquiryType.window!!.attributes = lp
    dialogSelectInquiryType.setCancelable(true)
    dialogSelectInquiryType.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    dialogSelectInquiryType.window!!.setLayout(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    dialogSelectInquiryType.window!!.setGravity(Gravity.CENTER)

    val rvDialogCustomer =
        dialogSelectInquiryType.findViewById(R.id.rvDialogCustomer) as RecyclerView
    val edtSearchId = dialogSelectInquiryType.findViewById(R.id.edtSearchCustomer) as EditText
    val txtid = dialogSelectInquiryType.findViewById(R.id.txtid) as TextView
    val imgClear = dialogSelectInquiryType.findViewById(R.id.imgClear) as ImageView

    imgClear.setOnClickListener {
        dialogSelectInquiryType.dismiss()
    }

    txtid.text = "Select Status"

    edtSearchId.gone()

        val itemAdapter = BottomSheetListAdapter(this, arrayListStatus!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(view: View, position: Int, type: Int) {

                itemAdapter.updateItem(position)
                mInquirytype = arrayListStatus!![position].Name.toString()
                edtStatus.setText(mInquirytype)
                dialogSelectInquiryType.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        dialogSelectInquiryType.show()
    }

    private fun setAPIData(model: InquiryTypeModel) {

        if (model.InquiryType != null && model.InquiryType != "") {
            edtInquiryType.setText(model.InquiryType)
        }
        if (model.IsActive != null) {
            if (model.IsActive == true) {
                edtStatus.setText("Active")
            } else {
                edtStatus.setText("InActive")
            }
        }
    }
}