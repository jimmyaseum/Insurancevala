package com.app.insurancevala.master

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.AddMorePlanNameAdapter
import com.app.insurancevala.interFase.RecyclerItemClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.pojo.PlanNameInfoModel
import com.app.insurancevala.model.response.CompanyByGUIDResponse
import com.app.insurancevala.model.response.CompanyModel
import com.app.insurancevala.model.response.SingleSelectionModel
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.errortint
import com.app.insurancevala.utils.getRequestJSONBody
import com.app.insurancevala.utils.hideKeyboard
import com.app.insurancevala.utils.internetErrordialog
import com.app.insurancevala.utils.isOnline
import com.app.insurancevala.utils.preventTwoClick
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.icon
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.position
import com.example.awesomedialog.title
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_company_type.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCompanyActivity : BaseActivity(), View.OnClickListener, RecyclerItemClickListener {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var CompanyID: Int? = null

    var arrayListPlanInfo: ArrayList<PlanNameInfoModel>? = ArrayList()
    lateinit var adapter: AddMorePlanNameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_company_type)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
        setDefaultData()
    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
        CompanyID = intent.getIntExtra("CompanyID",0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun initializeView() {
        if (state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Add Company"
            setMasterData()
        } else if (state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update Company"

            if (isOnline(this)) {
                callManageCompanyID()
            } else {
                internetErrordialog(this@AddCompanyActivity)
            }
        }

        SetInitListner()
    }

    private fun setMasterData() {
        if (isOnline(this)) {
        } else {
            internetErrordialog(this@AddCompanyActivity)
        }
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        tvAddMore.setOnClickListener(this)
        txtButtonCancel.setOnClickListener(this)
        txtButtonSubmit.setOnClickListener(this)

        rvPlanName.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        rvPlanName.isNestedScrollingEnabled = false
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                onBackPressed()
            }

            R.id.tvAddMore -> {
                preventTwoClick(v)
                if (::adapter.isInitialized) {
                    if (adapter.getAdapterArrayList()!!.size > 0) {
                        adapter.addItem(PlanNameInfoModel(), 1)
                    } else {
                        setDefaultData()
                    }
                } else {
                    setDefaultData()
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
                    manageCompanyInsertUpdateAPI()
                } else {
                    internetErrordialog(this@AddCompanyActivity)
                }
            }
        }
    }

    private fun setDefaultData() {
        arrayListPlanInfo = ArrayList()
        arrayListPlanInfo?.add(PlanNameInfoModel())
        setAdapterData(arrayListPlanInfo)
    }

    private fun setAdapterData(arrayList: ArrayList<PlanNameInfoModel>?) {
        adapter = AddMorePlanNameAdapter(arrayList, this@AddCompanyActivity)
        rvPlanName.adapter = adapter
    }

    private fun isValidate(): Boolean {

        var isvalidate = true

        if (edtCompanyType.text.toString().trim().isEmpty()) {
            edtCompanyType.setError(getString(R.string.error_empty_lead_source), errortint(this))
            isvalidate = false
        }

        if (::adapter.isInitialized) {
            if (!adapter.isValidateItem()) {
                isvalidate = false
            }
        }

        return isvalidate
    }

    private fun manageCompanyInsertUpdateAPI() {

        showProgress()

        val call: Call<CommonResponse>

        val jsonArrayPlanName = JSONArray()
        if (::adapter.isInitialized) {
            val arrayList = adapter.getAdapterArrayList()
            if (!arrayList.isNullOrEmpty()) {
                for (i in 0 until arrayList!!.size) {
                    val jsonObjectPlanName = JSONObject()
                    jsonObjectPlanName.put("PlanName", arrayList[i].PlanName)
                    jsonArrayPlanName.put(jsonObjectPlanName)
                }
            }
        }

        val jsonObject = JSONObject()

        if (state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("ID", CompanyID)
        }

        jsonObject.put("CompanyName", edtCompanyType.text.toString().trim())
        jsonObject.put("Plan", jsonArrayPlanName)

        if (state.equals(AppConstant.S_ADD)) {
            call = ApiUtils.apiInterface.ManageCompanyInsert(getRequestJSONBody(jsonObject.toString()))
        } else {
            call = ApiUtils.apiInterface.ManageCompanyUpdate(getRequestJSONBody(jsonObject.toString()))
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

    private fun callManageCompanyID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("ID", CompanyID)

        val call = ApiUtils.apiInterface.ManageCompanyFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CompanyByGUIDResponse> {
            override fun onResponse(
                call: Call<CompanyByGUIDResponse>, response: Response<CompanyByGUIDResponse>
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

            override fun onFailure(call: Call<CompanyByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setAPIData(model: CompanyModel) {

        if (model.CompanyName != null && model.CompanyName != "") {
            edtCompanyType.setText(model.CompanyName)
        }

        if(!model.PlanList!!.isNullOrEmpty()) {
            arrayListPlanInfo = ArrayList()

            for(i in 0 until model.PlanList.size) {

                arrayListPlanInfo?.add(
                    PlanNameInfoModel(
                        ID = model.PlanList[i].ID!!,
                        PlanName = model.PlanList[i].PlanName!!
                    )
                )
            }
            setAdapterData(arrayListPlanInfo)
        }
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int, name: String) {
        hideKeyboard(applicationContext, view)
        when (view.id) {
            R.id.imgDelete -> {
                if (::adapter.isInitialized) {
                    AwesomeDialog.build(this).title("Warning !!!")
                        .body("Are you sure want to delete this Plan Name?")
                        .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                        .onNegative("No") {

                        }.onPositive("Yes") {
                            adapter.remove(position)
                        }
                }
            }
        }
    }


}