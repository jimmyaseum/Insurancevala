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
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetStateListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.StateModel
import com.app.insurancevala.model.response.StateResponse
import com.app.insurancevala.model.response.CityByGUIDResponse
import com.app.insurancevala.model.response.CityModel
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
import kotlinx.android.synthetic.main.activity_add_city.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCityActivity : BaseActivity(), View.OnClickListener {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var CityGUID: String? = null

    var arrayListState: ArrayList<StateModel>? = ArrayList()
    var arrayListStateNew: ArrayList<StateModel>? = ArrayList()
    var mState: String = ""
    var mStateID: Int = 0

    var mCountry: String = ""
    var mCountryID: Int = 0

    var arrayListStatus: ArrayList<SingleSelectionModel>? = ArrayList()
    var mLeadStatus: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_city)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
        CityGUID = intent.getStringExtra("CityGUID")
        mStateID = intent.getIntExtra("StateID",0)
        mCountryID = intent.getIntExtra("CountryID",0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun initializeView() {
        if (state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Add City"
            setMasterData()
        } else if (state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update City"

            if (isOnline(this)) {
                callManageCityGUID()
            } else {
                internetErrordialog(this@AddCityActivity)
            }
        }

        callStateAllActive(0)

        SetInitListner()

        arrayListStatus?.add(SingleSelectionModel(0, "Active", true))
        arrayListStatus?.add(SingleSelectionModel(1, "InActive", false))
    }

    private fun setMasterData() {
        if (isOnline(this)) {
        } else {
            internetErrordialog(this@AddCityActivity)
        }
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        edtState.setOnClickListener(this)
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

            R.id.edtState -> {
                preventTwoClick(v)
                if (!arrayListState.isNullOrEmpty()) {
                    selectStateDialog()
                } else {
                    callStateAllActive(1)
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
                    ManageCityAPI()
                } else {
                    internetErrordialog(this@AddCityActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isvalidate = true

        if (edtState.text.toString().trim().isEmpty()) {
            edtState.setError(getString(R.string.error_empty_state), errortint(this))
            isvalidate = false
        }
        if (edtCountry.text.toString().trim().isEmpty()) {
            edtCountry.setError(getString(R.string.error_empty_country), errortint(this))
            isvalidate = false
        }
        if (edtCity.text.toString().trim().isEmpty()) {
            edtCity.setError(getString(R.string.error_empty_city), errortint(this))
            isvalidate = false
        }
        if (edtStatus.text.toString().trim().isEmpty()) {
            edtStatus.setError(getString(R.string.error_empty_type), errortint(this))
            isvalidate = false
        }

        return isvalidate
    }

    private fun callStateAllActive(mode: Int) {
        if (mode == 1) {
            showProgress()
        }
        val call = ApiUtils.apiInterface.getStateAllActive()
        call.enqueue(object : Callback<StateResponse> {
            override fun onResponse(
                call: Call<StateResponse>,
                response: Response<StateResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListState = response.body()?.Data!!

                        if (mode == 1) {
                            selectStateDialog()
                        }
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<StateResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun selectStateDialog() {
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
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectState.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectState.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectState.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectState.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectState.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectState.dismiss()
        }

        txtid.text = "Select State"

        val itemAdapter = BottomSheetStateListAdapter(this, arrayListState!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(view: View, position: Int, type: Int) {

                itemAdapter.updateItem(position)
                mStateID = arrayListState!![position].ID!!
                mState = arrayListState!![position].StateName!!

                mCountryID = arrayListState!![position].CountryID!!
                mCountry = arrayListState!![position].CountryName!!

                edtState.setText(arrayListState!![position].StateName)
                edtCountry.setText(arrayListState!![position].CountryName)

                edtState.setError(null)
                edtCountry.setError(null)

                dialogSelectState.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListState!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<StateModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListState!!) {
                        if (model.StateName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }
                    arrayListStateNew = arrItemsFinal1
                    val itemAdapter = BottomSheetStateListAdapter(this@AddCityActivity, arrayListStateNew!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(view: View, position: Int, type: Int) {

                            mStateID = arrayListStateNew!![position].ID!!
                            mState = arrayListStateNew!![position].StateName!!

                            mCountryID = arrayListState!![position].CountryID!!
                            mCountry = arrayListState!![position].CountryName!!

                            edtState.setText(arrayListStateNew!![position].StateName)
                            edtCountry.setText(arrayListState!![position].CountryName)

                            edtState.setError(null)
                            edtCountry.setError(null)

                            dialogSelectState!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetStateListAdapter(this@AddCityActivity, arrayListState!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(view: View, position: Int, type: Int) {

                            mStateID = arrayListState!![position].ID!!
                            mState = arrayListState!![position].StateName!!

                            mCountryID = arrayListState!![position].CountryID!!
                            mCountry = arrayListState!![position].CountryName!!

                            edtState.setText(arrayListState!![position].StateName)
                            edtCountry.setText(arrayListState!![position].CountryName)

                            edtState.setError(null)
                            edtCountry.setError(null)

                            dialogSelectState.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectState!!.show()
    }

    private fun callManageCityGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("CityGUID", CityGUID)

        val call = ApiUtils.apiInterface.ManageCityFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CityByGUIDResponse> {
            override fun onResponse(
                call: Call<CityByGUIDResponse>, response: Response<CityByGUIDResponse>
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

            override fun onFailure(call: Call<CityByGUIDResponse>, t: Throwable) {
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

    private fun ManageCityAPI() {

        showProgress()

        val call: Call<CommonResponse>

        val jsonObject = JSONObject()
        jsonObject.put("CityName", edtCity.text.toString().trim())
        jsonObject.put("CountryID", mCountryID)
        jsonObject.put("StateID", mStateID)

        if (edtStatus.text.toString().trim() == "Active") {
            jsonObject.put("IsActive", true)
        } else {
            jsonObject.put("IsActive", false)
        }

        if (state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("CityGUID", CityGUID)
        }

        if (state.equals(AppConstant.S_ADD)) {
            call = ApiUtils.apiInterface.ManageCityInsert(getRequestJSONBody(jsonObject.toString()))
        } else {
            call = ApiUtils.apiInterface.ManageCityUpdate(getRequestJSONBody(jsonObject.toString()))
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

    private fun setAPIData(model: CityModel) {

        if (model.CountryName != null && model.CountryName != "") {
            edtCountry.setText(model.CountryName)
        }

        if (model.StateName != null && model.StateName != "") {
            edtState.setText(model.StateName)
        }

        if (model.CityName != null && model.CityName != "") {
            edtCity.setText(model.CityName)
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