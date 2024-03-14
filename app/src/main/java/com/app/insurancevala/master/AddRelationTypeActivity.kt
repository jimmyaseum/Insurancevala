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
import com.app.insurancevala.model.response.RelationTypeByGUIDResponse
import com.app.insurancevala.model.response.RelationTypeModel
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
import kotlinx.android.synthetic.main.activity_add_relation_type.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddRelationTypeActivity : BaseActivity(), View.OnClickListener {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var RelationTypeGUID: String? = null

    var arrayListStatus: ArrayList<SingleSelectionModel>? = ArrayList()
    var mLeadStatus: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_relation_type)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
        RelationTypeGUID = intent.getStringExtra("RelationTypeGUID")

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun initializeView() {
        if (state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Add Relation Type"
            setMasterData()
        } else if (state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update Relation Type"

            if (isOnline(this)) {
                callManageRelationTypeGUID()
            } else {
                internetErrordialog(this@AddRelationTypeActivity)
            }
        }

        SetInitListner()

        arrayListStatus?.add(SingleSelectionModel(0, "Active", true))
        arrayListStatus?.add(SingleSelectionModel(1, "InActive", false))
    }

    private fun setMasterData() {
        if (isOnline(this)) {
        } else {
            internetErrordialog(this@AddRelationTypeActivity)
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
                    ManageRelationTypeAPI()
                } else {
                    internetErrordialog(this@AddRelationTypeActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isvalidate = true

        if (edtRelationType.text.toString().trim().isEmpty()) {
            edtRelationType.setError(getString(R.string.error_empty_lead_source), errortint(this))
            isvalidate = false
        }
        if (edtStatus.text.toString().trim().isEmpty()) {
            edtStatus.setError(getString(R.string.error_empty_type), errortint(this))
            isvalidate = false
        }

        return isvalidate
    }

    private fun ManageRelationTypeAPI() {

        showProgress()

        val call: Call<CommonResponse>

        val jsonObject = JSONObject()
        jsonObject.put("RelationName", edtRelationType.text.toString().trim())

        if (edtStatus.text.toString().trim() == "Active") {
            jsonObject.put("IsActive", true)
        } else {
            jsonObject.put("IsActive", false)
        }

        if (state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("RelationGUID", RelationTypeGUID)
        }

        if (state.equals(AppConstant.S_ADD)) {
            call = ApiUtils.apiInterface.ManageRelationInsert(getRequestJSONBody(jsonObject.toString()))
        } else {
            call = ApiUtils.apiInterface.ManageRelationUpdate(getRequestJSONBody(jsonObject.toString()))
        }
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>, response: Response<CommonResponse>
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

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                hideProgress()
            }
        })
    }

    private fun callManageRelationTypeGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("RelationGUID", RelationTypeGUID)

        val call = ApiUtils.apiInterface.ManageRelationFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<RelationTypeByGUIDResponse> {
            override fun onResponse(
                call: Call<RelationTypeByGUIDResponse>, response: Response<RelationTypeByGUIDResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListLead = response.body()?.Data!!
                        setAPIData(arrayListLead)
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<RelationTypeByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectLeadStatusDialog() {
        var dialogSelectRelationType = Dialog(this)
        dialogSelectRelationType.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectRelationType.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectRelationType.window!!.attributes)

        dialogSelectRelationType.window!!.attributes = lp
        dialogSelectRelationType.setCancelable(true)
        dialogSelectRelationType.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectRelationType.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectRelationType.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectRelationType.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchId = dialogSelectRelationType.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectRelationType.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectRelationType.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectRelationType.dismiss()
        }

        txtid.text = "Select Status"

        edtSearchId.gone()

        val itemAdapter = BottomSheetListAdapter(this, arrayListStatus!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(view: View, position: Int, type: Int) {

                itemAdapter.updateItem(position)
                mLeadStatus = arrayListStatus!![position].Name.toString()
                edtStatus.setText(mLeadStatus)
                dialogSelectRelationType.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        dialogSelectRelationType.show()
    }

    private fun setAPIData(model: RelationTypeModel) {

        if (model.RelationName != null && model.RelationName != "") {
            edtRelationType.setText(model.RelationName)
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