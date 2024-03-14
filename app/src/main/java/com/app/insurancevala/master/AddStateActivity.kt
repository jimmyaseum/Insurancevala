package com.app.insurancevala.master

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetCountryListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.CountryModel
import com.app.insurancevala.model.response.CountryResponse
import com.app.insurancevala.model.response.StateByGUIDResponse
import com.app.insurancevala.model.response.StateModel
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
import com.app.insurancevala.utils.visible
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_state.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStateActivity : BaseActivity(), View.OnClickListener {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var StateGUID: String? = null

    var arrayListCountry: ArrayList<CountryModel>? = ArrayList()
    var arrayListCountryNew: ArrayList<CountryModel>? = ArrayList()
    var mCountry: String = ""
    var mCountryID: Int = 0

    var arrayListStatus: ArrayList<SingleSelectionModel>? = ArrayList()
    var mLeadStatus: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_state)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
        StateGUID = intent.getStringExtra("StateGUID")
        mCountryID = intent.getIntExtra("CountryID",0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun initializeView() {
        if (state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Add State"
            setMasterData()
        } else if (state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update State"

            if (isOnline(this)) {
                callManageStateGUID()
            } else {
                internetErrordialog(this@AddStateActivity)
            }
        }

        callCountryAllActive(0)

        SetInitListner()

        arrayListStatus?.add(SingleSelectionModel(0, "Active", true))
        arrayListStatus?.add(SingleSelectionModel(1, "InActive", false))
    }

    private fun setMasterData() {
        if (isOnline(this)) {
        } else {
            internetErrordialog(this@AddStateActivity)
        }
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        edtCountry.setOnClickListener(this)
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

            R.id.edtCountry -> {
                preventTwoClick(v)
                if (!arrayListCountry.isNullOrEmpty()) {
                    selectCountryDialog()
                } else {
                    callCountryAllActive(1)
                }
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
                    ManageStateAPI()
                } else {
                    internetErrordialog(this@AddStateActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isvalidate = true

        if (edtCountry.text.toString().trim().isEmpty()) {
            edtCountry.setError(getString(R.string.error_empty_country), errortint(this))
            isvalidate = false
        }
        if (edtState.text.toString().trim().isEmpty()) {
            edtState.setError(getString(R.string.error_empty_state), errortint(this))
            isvalidate = false
        }
        if (edtStatus.text.toString().trim().isEmpty()) {
            edtStatus.setError(getString(R.string.error_empty_type), errortint(this))
            isvalidate = false
        }

        return isvalidate
    }

    private fun callCountryAllActive(mode: Int) {
        if (mode == 1) {
            showProgress()
        }
        val call = ApiUtils.apiInterface.getCountryAllActive()
        call.enqueue(object : Callback<CountryResponse> {
            override fun onResponse(
                call: Call<CountryResponse>,
                response: Response<CountryResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListCountry = response.body()?.Data!!

                        if (mode == 1) {
                            selectCountryDialog()
                        }
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<CountryResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun selectCountryDialog() {
        var dialogSelectCountry = Dialog(this)
        dialogSelectCountry.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectCountry.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectCountry.window!!.attributes)

        dialogSelectCountry.window!!.attributes = lp
        dialogSelectCountry.setCancelable(true)
        dialogSelectCountry.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectCountry.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectCountry.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectCountry.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectCountry.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectCountry.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectCountry.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectCountry.dismiss()
        }

        txtid.text = "Select Country"

        val itemAdapter = BottomSheetCountryListAdapter(this, arrayListCountry!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(view: View, position: Int, type: Int) {

                itemAdapter.updateItem(position)
                mCountryID = arrayListCountry!![position].ID!!
                mCountry = arrayListCountry!![position].CountryName!!

                edtCountry.setText(arrayListCountry!![position].CountryName)

                dialogSelectCountry.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListCountry!!.size > 6) {
            edtSearchCustomer.visible()
        } else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<CountryModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListCountry!!) {
                        if (model.CountryName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }
                    arrayListCountryNew = arrItemsFinal1
                    val itemAdapter = BottomSheetCountryListAdapter(this@AddStateActivity, arrayListCountryNew!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(view: View, position: Int, type: Int) {

                            mCountryID = arrayListCountryNew!![position].ID!!
                            mCountry = arrayListCountryNew!![position].CountryName!!

                            edtCountry.setText(arrayListCountryNew!![position].CountryName)

                            dialogSelectCountry!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetCountryListAdapter(this@AddStateActivity, arrayListCountry!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(view: View, position: Int, type: Int) {

                            mCountryID = arrayListCountry!![position].ID!!
                            mCountry = arrayListCountry!![position].CountryName!!

                            edtCountry.setText(arrayListCountry!![position].CountryName)

                            dialogSelectCountry!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectCountry!!.show()
    }

    private fun callManageStateGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("StateGUID", StateGUID)

        val call = ApiUtils.apiInterface.ManageStateFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<StateByGUIDResponse> {
            override fun onResponse(
                call: Call<StateByGUIDResponse>, response: Response<StateByGUIDResponse>
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

            override fun onFailure(call: Call<StateByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectLeadStatusDialog() {
        var dialogSelectState = Dialog(this)
        dialogSelectState.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectState.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectState.window!!.attributes)

        dialogSelectState.window!!.attributes = lp
        dialogSelectState.setCancelable(true)
        dialogSelectState.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectState.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectState.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectState.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchId = dialogSelectState.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectState.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectState.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectState.dismiss()
        }

        txtid.text = "Select Status"

        edtSearchId.gone()

        val itemAdapter = BottomSheetListAdapter(this, arrayListStatus!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(view: View, position: Int, type: Int) {

                itemAdapter.updateItem(position)
                mLeadStatus = arrayListStatus!![position].Name.toString()
                edtStatus.setText(mLeadStatus)
                dialogSelectState.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        dialogSelectState.show()
    }

    private fun ManageStateAPI() {

        showProgress()

        val call: Call<CommonResponse>

        val jsonObject = JSONObject()
        jsonObject.put("StateName", edtState.text.toString().trim())
        jsonObject.put("CountryID", mCountryID)

        if (edtStatus.text.toString().trim() == "Active") {
            jsonObject.put("IsActive", true)
        } else {
            jsonObject.put("IsActive", false)
        }

        if (state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("StateGUID", StateGUID)
        }

        if (state.equals(AppConstant.S_ADD)) {
            call = ApiUtils.apiInterface.ManageStateInsert(getRequestJSONBody(jsonObject.toString()))
        } else {
            call = ApiUtils.apiInterface.ManageStateUpdate(getRequestJSONBody(jsonObject.toString()))
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

    private fun setAPIData(model: StateModel) {

        if (model.CountryName != null && model.CountryName != "") {
            edtCountry.setText(model.CountryName)
        }

        if (model.StateName != null && model.StateName != "") {
            edtState.setText(model.StateName)
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