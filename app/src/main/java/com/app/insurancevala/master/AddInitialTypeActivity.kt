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
import com.app.insurancevala.model.response.InitialModel
import com.app.insurancevala.model.response.InitialResponse
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
import kotlinx.android.synthetic.main.activity_add_initial_type.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddInitialTypeActivity : BaseActivity(), View.OnClickListener {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var InitialGUID: String? = null

    var arrayListStatus: ArrayList<SingleSelectionModel>? = ArrayList()

    var mInitial: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_initial_type)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
        InitialGUID = intent.getStringExtra("InitialGUID")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun initializeView() {
        if (state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Add Initial Type"
            setMasterData()
        } else if (state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update Initial Type"

            if (isOnline(this)) {
                callManageInitialGUID()
            } else {
                internetErrordialog(this@AddInitialTypeActivity)
            }
        }

        SetInitListner()

        arrayListStatus?.add(SingleSelectionModel(0, "true", true))
        arrayListStatus?.add(SingleSelectionModel(1, "false", false))
    }

    private fun setMasterData() {
        if (isOnline(this)) {
        } else {
            internetErrordialog(this@AddInitialTypeActivity)
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
                    selectInitialDialog()
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
                    ManageInitialAPI()
                } else {
                    internetErrordialog(this@AddInitialTypeActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isvalidate = true

        if (edtInitial.text.toString().trim().isEmpty()) {
            edtInitial.setError(getString(R.string.error_empty_initial_type), errortint(this))
            isvalidate = false
        }
        if (edtIsActive.text.toString().trim().isEmpty()) {
            edtIsActive.setError(getString(R.string.error_empty_type), errortint(this))
            isvalidate = false
        }

        return isvalidate
    }

    private fun ManageInitialAPI() {

        showProgress()

        val jsonObject = JSONObject()
        jsonObject.put("Initial", edtInitial.text.toString().trim())

        if (edtIsActive.text.toString().trim() == "true") {
            jsonObject.put("IsActive", true)
        } else {
            jsonObject.put("IsActive", false)
        }

        if (state.equals(AppConstant.S_ADD)) {
            jsonObject.put("OperationType", AppConstant.INSERT)
        } else if (state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("InitialGUID", InitialGUID)
            jsonObject.put("OperationType", AppConstant.EDIT)
        }

        val call =
            ApiUtils.apiInterface.ManageInitial(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<InitialResponse> {
            override fun onResponse(
                call: Call<InitialResponse>,
                response: Response<InitialResponse>
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

            override fun onFailure(call: Call<InitialResponse>, t: Throwable) {
                hideProgress()
            }
        })
    }

    private fun callManageInitialGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("InitialGUID", InitialGUID)
        jsonObject.put("OperationType", AppConstant.GETBYGUID)

        val call =
            ApiUtils.apiInterface.ManageInitial(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<InitialResponse> {
            override fun onResponse(
                call: Call<InitialResponse>,
                response: Response<InitialResponse>
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

            override fun onFailure(call: Call<InitialResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectInitialDialog() {
    var dialogSelectInitial = Dialog(this)
    dialogSelectInitial.requestWindowFeature(Window.FEATURE_NO_TITLE)

    val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
    dialogSelectInitial.setContentView(dialogView)

    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialogSelectInitial.window!!.attributes)

    dialogSelectInitial.window!!.attributes = lp
    dialogSelectInitial.setCancelable(true)
    dialogSelectInitial.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    dialogSelectInitial.window!!.setLayout(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    dialogSelectInitial.window!!.setGravity(Gravity.CENTER)

    val rvDialogCustomer =
        dialogSelectInitial.findViewById(R.id.rvDialogCustomer) as RecyclerView
    val edtSearchId = dialogSelectInitial.findViewById(R.id.edtSearchCustomer) as EditText
    val txtid = dialogSelectInitial.findViewById(R.id.txtid) as TextView
    val imgClear = dialogSelectInitial.findViewById(R.id.imgClear) as ImageView

    imgClear.setOnClickListener {
        dialogSelectInitial.dismiss()
    }

    txtid.text = "Select Status"

    edtSearchId.gone()

        val itemAdapter = BottomSheetListAdapter(this, arrayListStatus!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(view: View, position: Int, type: Int) {

                itemAdapter.updateItem(position)
                mInitial = arrayListStatus!![position].Name.toString()
                edtIsActive.setText(mInitial)
                dialogSelectInitial.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        dialogSelectInitial.show()
    }

    private fun setAPIData(model: InitialModel) {

        if (model.Initial != null && model.Initial != "") {
            edtInitial.setText(model.Initial)
        }
        if (model.IsActive != null) {
            edtIsActive.setText(model.IsActive.toString())
        }
    }


}