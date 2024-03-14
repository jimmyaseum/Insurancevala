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
import com.app.insurancevala.model.response.CategoryTypeByGUIDResponse
import com.app.insurancevala.model.response.CategoryTypeModel
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
import kotlinx.android.synthetic.main.activity_add_category_type.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCategoryTypeActivity : BaseActivity(), View.OnClickListener {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var CategoryTypeGUID: String? = null

    var arrayListStatus: ArrayList<SingleSelectionModel>? = ArrayList()
    var mLeadStatus: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category_type)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
        CategoryTypeGUID = intent.getStringExtra("CategoryTypeGUID")

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun initializeView() {
        if (state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Add Category Type"
            setMasterData()
        } else if (state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update Category Type"

            if (isOnline(this)) {
                callManageCategoryTypeGUID()
            } else {
                internetErrordialog(this@AddCategoryTypeActivity)
            }
        }

        SetInitListner()

        arrayListStatus?.add(SingleSelectionModel(0, "Active", true))
        arrayListStatus?.add(SingleSelectionModel(1, "InActive", false))
    }

    private fun setMasterData() {
        if (isOnline(this)) {
        } else {
            internetErrordialog(this@AddCategoryTypeActivity)
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
                    ManageCategoryTypeAPI()
                } else {
                    internetErrordialog(this@AddCategoryTypeActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isvalidate = true

        if (edtCategoryType.text.toString().trim().isEmpty()) {
            edtCategoryType.setError(getString(R.string.error_empty_lead_source), errortint(this))
            isvalidate = false
        }
        if (edtStatus.text.toString().trim().isEmpty()) {
            edtStatus.setError(getString(R.string.error_empty_type), errortint(this))
            isvalidate = false
        }

        return isvalidate
    }

    private fun ManageCategoryTypeAPI() {

        showProgress()

        val call: Call<CommonResponse>

        val jsonObject = JSONObject()
        jsonObject.put("CategoryName", edtCategoryType.text.toString().trim())

        if (edtStatus.text.toString().trim() == "Active") {
            jsonObject.put("IsActive", true)
        } else {
            jsonObject.put("IsActive", false)
        }

        if (state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("CategoryGUID", CategoryTypeGUID)
        }

        if (state.equals(AppConstant.S_ADD)) {
            call = ApiUtils.apiInterface.ManageCategoryInsert(getRequestJSONBody(jsonObject.toString()))
        } else {
            call = ApiUtils.apiInterface.ManageCategoryUpdate(getRequestJSONBody(jsonObject.toString()))
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

    private fun callManageCategoryTypeGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("CategoryGUID", CategoryTypeGUID)

        val call = ApiUtils.apiInterface.ManageCategoryFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CategoryTypeByGUIDResponse> {
            override fun onResponse(
                call: Call<CategoryTypeByGUIDResponse>, response: Response<CategoryTypeByGUIDResponse>
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

            override fun onFailure(call: Call<CategoryTypeByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectLeadStatusDialog() {
        var dialogSelectCategoryType = Dialog(this)
        dialogSelectCategoryType.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectCategoryType.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectCategoryType.window!!.attributes)

        dialogSelectCategoryType.window!!.attributes = lp
        dialogSelectCategoryType.setCancelable(true)
        dialogSelectCategoryType.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectCategoryType.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectCategoryType.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectCategoryType.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchId = dialogSelectCategoryType.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectCategoryType.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectCategoryType.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectCategoryType.dismiss()
        }

        txtid.text = "Select Status"

        edtSearchId.gone()

        val itemAdapter = BottomSheetListAdapter(this, arrayListStatus!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(view: View, position: Int, type: Int) {

                itemAdapter.updateItem(position)
                mLeadStatus = arrayListStatus!![position].Name.toString()
                edtStatus.setText(mLeadStatus)
                dialogSelectCategoryType.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        dialogSelectCategoryType.show()
    }

    private fun setAPIData(model: CategoryTypeModel) {

        if (model.CategoryName != null && model.CategoryName != "") {
            edtCategoryType.setText(model.CategoryName)
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