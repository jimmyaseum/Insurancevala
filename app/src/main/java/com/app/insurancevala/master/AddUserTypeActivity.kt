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
import com.app.insurancevala.model.response.UserTypeModel
import com.app.insurancevala.model.response.UserTypeResponse
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
import kotlinx.android.synthetic.main.activity_add_user_type.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddUserTypeActivity : BaseActivity(), View.OnClickListener {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var UserTypeGUID: String? = null

    var arrayListStatus: ArrayList<SingleSelectionModel>? = ArrayList()

    var mUsertype: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_type)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
        UserTypeGUID = intent.getStringExtra("UserTypeGUID")

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun initializeView() {
        if (state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Add User Type"
            setMasterData()
        } else if (state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update User Type"

            if (isOnline(this)) {
                callManageUserTypesGUID()
            } else {
                internetErrordialog(this@AddUserTypeActivity)
            }
        }

        SetInitListner()

        arrayListStatus?.add(SingleSelectionModel(0, "Active", true))
        arrayListStatus?.add(SingleSelectionModel(1, "InActive", false))
    }

    private fun setMasterData() {
        if (isOnline(this)) {
        } else {
            internetErrordialog(this@AddUserTypeActivity)
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
                    selectUserTypeDialog()
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
                    ManageUserTypeAPI()
                } else {
                    internetErrordialog(this@AddUserTypeActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isvalidate = true

        if (edtUserType.text.toString().trim().isEmpty()) {
            edtUserType.setError(getString(R.string.error_empty_user_type), errortint(this))
            isvalidate = false
        }
        if (edtStatus.text.toString().trim().isEmpty()) {
            edtStatus.setError(getString(R.string.error_empty_type), errortint(this))
            isvalidate = false
        }

        return isvalidate
    }

    private fun ManageUserTypeAPI() {

        showProgress()

        val jsonObject = JSONObject()
        jsonObject.put("UserType", edtUserType.text.toString().trim())

        if (edtStatus.text.toString().trim() == "Active") {
            jsonObject.put("IsActive", true)
        } else {
            jsonObject.put("IsActive", false)
        }

        if (state.equals(AppConstant.S_ADD)) {
            jsonObject.put("OperationType", AppConstant.INSERT)
        } else if (state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("UserTypeGUID", UserTypeGUID)
            jsonObject.put("OperationType", AppConstant.EDIT)
        }

        val call =
            ApiUtils.apiInterface.ManageUserType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserTypeResponse> {
            override fun onResponse(
                call: Call<UserTypeResponse>,
                response: Response<UserTypeResponse>
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

            override fun onFailure(call: Call<UserTypeResponse>, t: Throwable) {
                hideProgress()
            }
        })
    }

    private fun callManageUserTypesGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETBYGUID)
        jsonObject.put("UserTypeGUID", UserTypeGUID)

        val call =
            ApiUtils.apiInterface.ManageUserType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserTypeResponse> {
            override fun onResponse(
                call: Call<UserTypeResponse>,
                response: Response<UserTypeResponse>
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

            override fun onFailure(call: Call<UserTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectUserTypeDialog() {
    var dialogSelectUserType = Dialog(this)
    dialogSelectUserType.requestWindowFeature(Window.FEATURE_NO_TITLE)

    val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
    dialogSelectUserType.setContentView(dialogView)

    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialogSelectUserType.window!!.attributes)

    dialogSelectUserType.window!!.attributes = lp
    dialogSelectUserType.setCancelable(true)
    dialogSelectUserType.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    dialogSelectUserType.window!!.setLayout(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    dialogSelectUserType.window!!.setGravity(Gravity.CENTER)

    val rvDialogCustomer =
        dialogSelectUserType.findViewById(R.id.rvDialogCustomer) as RecyclerView
    val edtSearchId = dialogSelectUserType.findViewById(R.id.edtSearchCustomer) as EditText
    val txtid = dialogSelectUserType.findViewById(R.id.txtid) as TextView
    val imgClear = dialogSelectUserType.findViewById(R.id.imgClear) as ImageView

    imgClear.setOnClickListener {
        dialogSelectUserType.dismiss()
    }

    txtid.text = "Select Status"

    edtSearchId.gone()

        val itemAdapter = BottomSheetListAdapter(this, arrayListStatus!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(view: View, position: Int, type: Int) {

                itemAdapter.updateItem(position)
                mUsertype = arrayListStatus!![position].Name.toString()
                edtStatus.setText(mUsertype)
                dialogSelectUserType.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        dialogSelectUserType.show()
    }

    private fun setAPIData(model: UserTypeModel) {

        if (model.UserType != null && model.UserType != "") {
            edtUserType.setText(model.UserType)
        }
        if (model.IsActive != null) {
            if (model.IsActive){
                edtStatus.setText("Active")
            } else {
                edtStatus.setText("InActive")
            }
        }
    }


}