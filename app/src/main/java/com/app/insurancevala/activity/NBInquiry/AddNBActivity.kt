package com.app.insurancevala.activity.NBInquiry

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.bottomsheetadapter.*
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.interFase.RecyclerItemClickListener
import com.app.insurancevala.model.api.LeadCountResponse
import com.app.insurancevala.model.pojo.InquiryInformationModel
import com.app.insurancevala.model.response.*
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_lead.layout
import kotlinx.android.synthetic.main.activity_add_nbinquiry.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddNBActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener,
    RecyclerItemClickListener {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var AddMore: Boolean? = null
    var View: Boolean? = false
    var NBInquiryGUID: String? = null

    var arrayListAllotmentTo: ArrayList<UserModel>? = ArrayList()
    var mAllotmentTo: String = ""
    var mAllotmentToID: Int = 0

    var arrayListClient: ArrayList<LeadModel>? = ArrayList()
    var mClient: String = ""
    var mClientID: Int = 0
    var mClientGUID: String = ""
    var mClientStageID: Int = 0

    var arrayListFamilyMember: ArrayList<FamilyModel>? = ArrayList()
    var mFamilyMember: String = ""
    var mFamilyMemberID: Int = 0
    var mFamilyMemberItemPostion: Int = 0
    var mFamilyMemberGUID: String = ""

    var arrayListleadtype: ArrayList<LeadTypeModel>? = ArrayList()
    var mLeadtype: String = ""
    var mLeadtypeID: Int = 0
    var mLeadtypeItemPostion: Int = 0

    var arrayListleadstatus: ArrayList<LeadStatusModel>? = ArrayList()
    var mLeadstatus: String = ""
    var mLeadstatusID: Int = 0
    var mLeadstatusItemPostion: Int = 0

    var arrayListInquiryType: ArrayList<InquiryTypeModel>? = ArrayList()
    var mInquiryType: String = ""
    var mInquiryTypeID: Int = 0
    var mInquiryTypeItemPostion: Int = 0

    var arrayListFrequency: ArrayList<SingleSelectionModel> = ArrayList()
    var mFrequency: String = ""
    var mFrequencyID: Int = 0
    var mFrequencyItemPostion: Int = 0

    var arrayListInquirySubType: ArrayList<InquirySubTypeModel>? = ArrayList()
    var mInquirySubType: String = ""
    var mInquirySubTypeID: Int = 0
    var mInquirySubTypeItemPostion: Int = 0

    var arrayListInquiryAllotmentTo: ArrayList<UserModel>? = ArrayList()
    var mInquiryAllotmentTo: String = ""
    var mInquiryAllotmentToID: Int = 0
    var mAllotmentToItemPostion: Int = 0

    var calendarNow: Calendar? = Calendar.getInstance()
    var mInquiryDateItemPostion: Int = 0

    var arrayListInquiryInfo: ArrayList<InquiryInformationModel>? = ArrayList()
    lateinit var adapter: AddMoreInquiryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_nbinquiry)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
        AddMore = intent.getBooleanExtra("AddMore", true)
        edtClient.setText(intent.getStringExtra("LeadName"))

        mClientID = intent.getIntExtra("LeadID", 0)

        mAllotmentToID = intent.getIntExtra("LeadOwnerID", 0)
        mAllotmentTo = intent.getStringExtra("LeadOwnerName").toString()

        if (!AddMore!!) {
            edtAllotmentTo.setText(mAllotmentTo)
        }

        callManageAllotmentTo(0, "AllotedName")

        if (mClientID != 0) {
            callManageFamilyDetails(mClientID)
        }

        mClientGUID = intent.getStringExtra("LeadGUID").toString()

        mClientStageID = intent.getIntExtra("LeadType", 0)
        if (mClientStageID.equals(1)) {
            edtClientType.setText("Existing Client")
        } else if (mClientStageID.equals(2)) {
            edtClientType.setText("Prospect")
        }
    }

    override fun initializeView() {
        SetAnimationControl1()
        setMasterData()
        SetInitListner()
    }

    private fun SetAnimationControl1() {
        val topanim = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        val bottomanim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim)
    }

    private fun setMasterData() {
        if (isOnline(this)) {
            callManageAllotmentTo(0, "")
            callManageInquiryType(0)
            callManageLeadType(0)
            callManageLeadStatus(0)
        } else {
            internetErrordialog(this@AddNBActivity)
        }
        if (state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Create NB Inquiry"
            if (!AddMore!!) {
                callManageLeadGUID(mClientGUID, null)
            }
            txtSave.visible()
        } else if (state.equals(AppConstant.S_EDIT)) {
            NBInquiryGUID = intent.getStringExtra("NBInquiryGUID")
            txtHearderText.text = "Update NB Inquiry"
            if (isOnline(this)) {
                callManageNBInquiryGUID()
            } else {
                internetErrordialog(this@AddNBActivity)
            }
        }

        arrayListFrequency.add(SingleSelectionModel(1, "Single", true))
        arrayListFrequency.add(SingleSelectionModel(2, "Yearly", false))
        arrayListFrequency.add(SingleSelectionModel(3, "Half Yearly", false))
        arrayListFrequency.add(SingleSelectionModel(4, "Quaterly", false))
        arrayListFrequency.add(SingleSelectionModel(5, "Monthly", false))
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        txtSave.setOnClickListener(this)

        edtClient.setOnClickListener(this)
        edtAllotmentTo.setOnClickListener(this)

        rvInquiry.layoutManager = LinearLayoutManager(
            applicationContext, RecyclerView.VERTICAL, false
        )
        rvInquiry.isNestedScrollingEnabled = false
    }

    private fun setDefaultData() {
        arrayListInquiryInfo = ArrayList()
        arrayListInquiryInfo?.add(
            InquiryInformationModel(
                AllotmentTo = mAllotmentTo, AllotmentToId = mAllotmentToID
            )
        )
        setAdapterData(arrayListInquiryInfo)
    }

    private fun setAdapterData(arrayList: ArrayList<InquiryInformationModel>?) {
        adapter = AddMoreInquiryAdapter(arrayList, AddMore!!, View!!,this@AddNBActivity)
        rvInquiry.adapter = adapter

        Handler().postDelayed({
            smoothScrollToLastItem()
        }, 100)
    }

    private fun smoothScrollToLastItem() {
        val smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(this) {
            override fun calculateSpeedPerPixel(displayMetrics: android.util.DisplayMetrics): Float {
                // Gradually slow down the speed when near the last item
                return 100f / displayMetrics.densityDpi
            }
        }
        smoothScroller.targetPosition = adapter.itemCount - 1
        rvInquiry.layoutManager?.startSmoothScroll(smoothScroller)
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }

            R.id.edtClient -> {
                if (AddMore != false && state == AppConstant.S_ADD) {
                    preventTwoClick(v)
                    selectClientDialog()
                }
            }

            R.id.txtSave -> {
                preventTwoClick(v)
                validation()
            }

            R.id.edtAllotmentTo -> {
                preventTwoClick(v)
                if (arrayListAllotmentTo.isNullOrEmpty()) {
                    callManageAllotmentTo(1, "")
                } else {
                    selectAllotmentToDialog()
                }
            }
        }
    }

    fun validation() {
        val flag = isValidate()
        if (flag) {
            if (isOnline(this)) {
                callManageNBInquiry()
            } else {
                internetErrordialog(this@AddNBActivity)
            }
        }
    }

    fun isValidate(): Boolean {
        var isValidate = true
        if (edtClient.text.isEmpty()) {
            edtClient.setError("Select Client", errortint(this))
            isValidate = false
        }
        if (edtAllotmentTo.text.isEmpty()) {
            edtAllotmentTo.setError("Select Allotment To", errortint(this))
            isValidate = false
        }
        if (::adapter.isInitialized) {
            if (!adapter.isValidateItem()) {
                isValidate = false
            }
        }

        return isValidate
    }

    fun callManageNBInquiry() {

        showProgress()

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(applicationContext)
        }

        val jsonArrayEducation = JSONArray()
        if (::adapter.isInitialized) {
            val arrayList = adapter.getAdapterArrayList()
            if (!arrayList.isNullOrEmpty()) {
                for (i in 0 until arrayList!!.size) {
                    val jsonObjectEducation = JSONObject()
                    jsonObjectEducation.put("NBInquiryBy", arrayList!![i].FamilyMemberId)
                    jsonObjectEducation.put("InquiryTypeID", arrayList!![i].InquirytypeId)
                    jsonObjectEducation.put("InquirySubTypeID", arrayList!![i].InquirysubtypeId)
                    jsonObjectEducation.put("LeadTypeID", arrayList!![i].LeadtypeId)
                    jsonObjectEducation.put("LeadStatusID", arrayList!![i].LeadstatusId)
                    jsonObjectEducation.put("ProposedAmount", arrayList!![i].Proposed.toDouble())
                    jsonObjectEducation.put("Frequency", arrayList!![i].Frequency)
                    jsonObjectEducation.put("InquiryAllotmentID", arrayList!![i].AllotmentToId)
                    jsonObjectEducation.put("InquiryDate", arrayList!![i].mInquiryDate)
                    jsonArrayEducation.put(jsonObjectEducation)
                }
            }
        }

        val jsonObject = JSONObject()
        jsonObject.put("LeadID", mClientID)
        jsonObject.put("LeadStageID", mClientStageID)
        jsonObject.put("IsActive", true)
        jsonObject.put("AllotmentID", mAllotmentToID)
        jsonObject.put("NBInquiryList", jsonArrayEducation)
        jsonObject.put("FamilyID", mFamilyMemberID)

        var call: Call<NBInquiryTypeAddUpdateResponse>? = null

        if (state.equals(AppConstant.S_ADD)) {
            call =
                ApiUtils.apiInterface.ManageNBInquiryInsert(getRequestJSONBody(jsonObject.toString()))
        } else if (state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("NBInquiryGUID", NBInquiryGUID)
            call =
                ApiUtils.apiInterface.ManageNBInquiryUpdate(getRequestJSONBody(jsonObject.toString()))
        }

        if (call != null) {
            call.enqueue(object : Callback<NBInquiryTypeAddUpdateResponse> {
                override fun onResponse(
                    call: Call<NBInquiryTypeAddUpdateResponse>,
                    response: Response<NBInquiryTypeAddUpdateResponse>
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

                override fun onFailure(call: Call<NBInquiryTypeAddUpdateResponse>, t: Throwable) {
                    hideProgress()
                    Snackbar.make(
                        layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                    ).show()
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                }
            })
        }
    }

    private fun callManageAllotmentTo(mode: Int, name: String) {
        if (mode == 1) {
            showProgress()
        }
        if (sharedPreference == null) {
            sharedPreference = SharedPreference(this)
        }
        val Userid = sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_ID)!!

        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageUsers(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListAllotmentTo = response.body()?.Data!!
                        if (name == "") {
                            if (arrayListAllotmentTo!!.size > 0) {
                                for (i in 0 until arrayListAllotmentTo!!.size) {
                                    if (state.equals(AppConstant.S_ADD) && AddMore == true) {
                                        if (arrayListAllotmentTo!![i].ID == Userid.toInt()) {
                                            mAllotmentToID = arrayListAllotmentTo!![i].ID!!
                                            mAllotmentTo =
                                                arrayListAllotmentTo!![i].FirstName!! + " " + arrayListAllotmentTo!![i].LastName!!
                                            edtAllotmentTo.setText(mAllotmentTo)
                                            edtAllotmentTo.setError(null)
                                        }
                                    } else if (state.equals(AppConstant.S_EDIT) || AddMore == false) {
                                        if (arrayListAllotmentTo!![i].ID == mAllotmentToID) {
                                            mAllotmentToID = arrayListAllotmentTo!![i].ID!!
                                            mAllotmentTo =
                                                arrayListAllotmentTo!![i].FirstName!! + " " + arrayListAllotmentTo!![i].LastName!!
                                            edtAllotmentTo.setText(mAllotmentTo)
                                            edtAllotmentTo.setError(null)
                                        }
                                    }
                                }
                            }
                        } else {
                            for (i in 0 until arrayListAllotmentTo!!.size) {
                                if (arrayListAllotmentTo!![i].ID == mAllotmentToID) {
                                    mAllotmentToID = arrayListAllotmentTo!![i].ID!!
                                    mAllotmentTo =
                                        arrayListAllotmentTo!![i].FirstName!! + " " + arrayListAllotmentTo!![i].LastName!!
                                    edtAllotmentTo.setText(mAllotmentTo)
                                    edtAllotmentTo.setError(null)
                                }
                            }
                        }
                        if (state.equals(AppConstant.S_ADD)) {
                            setDefaultData()
                        }
                        if (mode == 1) {
                            selectAllotmentToDialog()
                        }
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectAllotmentToDialog() {
        var dialogSelectAllotmentTo = Dialog(this)
        dialogSelectAllotmentTo.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectAllotmentTo.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectAllotmentTo.window!!.attributes)

        dialogSelectAllotmentTo.window!!.attributes = lp
        dialogSelectAllotmentTo.setCancelable(true)
        dialogSelectAllotmentTo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectAllotmentTo.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectAllotmentTo.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectAllotmentTo.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectAllotmentTo.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectAllotmentTo.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectAllotmentTo.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectAllotmentTo.dismiss()
        }

        txtid.text = "Select Allotment To"

        val itemAdapter = BottomSheetUsersListAdapter(this, arrayListAllotmentTo!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                itemAdapter.updateItem(pos)
                mAllotmentToID = arrayListAllotmentTo!![pos].ID!!
                mAllotmentTo =
                    arrayListAllotmentTo!![pos].FirstName!! + " " + arrayListAllotmentTo!![pos].LastName!!
                edtAllotmentTo.setText(mAllotmentTo)
                edtAllotmentTo.setError(null)
                dialogSelectAllotmentTo!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListAllotmentTo!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<UserModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListAllotmentTo!!) {
                        if (model.FirstName!!.toLowerCase()
                                .contains(strSearch.toLowerCase()) || model.LastName!!.toLowerCase()
                                .contains(strSearch.toLowerCase()) || model.MobileNo!!.toLowerCase()
                                .contains(strSearch.toLowerCase())
                        ) {
                            arrItemsFinal1.add(model)
                        }
                    }
                    val itemAdapter =
                        BottomSheetUsersListAdapter(this@AddNBActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            mAllotmentToID = arrItemsFinal1!![pos].ID!!
                            mAllotmentTo =
                                arrItemsFinal1!![pos].FirstName!! + " " + arrItemsFinal1!![pos].LastName!!
                            edtAllotmentTo.setText(mAllotmentTo)
                            edtAllotmentTo.setError(null)
                            dialogSelectAllotmentTo!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetUsersListAdapter(this@AddNBActivity, arrayListAllotmentTo!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mAllotmentToID = arrayListAllotmentTo!![pos].ID!!
                            mAllotmentTo =
                                arrayListAllotmentTo!![pos].FirstName!! + " " + arrayListAllotmentTo!![pos].LastName!!
                            edtAllotmentTo.setText(mAllotmentTo)
                            edtAllotmentTo.setError(null)

                            dialogSelectAllotmentTo!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectAllotmentTo!!.show()
    }

    private fun selectClientDialog() {
        var dialogSelectClient = Dialog(this)
        dialogSelectClient.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectClient.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectClient.window!!.attributes)

        dialogSelectClient.window!!.attributes = lp
        dialogSelectClient.setCancelable(true)
        dialogSelectClient.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectClient.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectClient.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectClient.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectClient.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectClient.findViewById(R.id.txtid) as TextView
        val txtChar = dialogSelectClient.findViewById(R.id.txtChar) as TextView
        val imgClear = dialogSelectClient.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectClient.dismiss()
        }
        txtid.text = "Select Clients"

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<LeadModel> = ArrayList()
                if (char.toString().trim().isNotEmpty() && edtSearchCustomer.text.toString()
                        .trim().length > 2
                ) {
                    val strSearch = char.toString()
                    txtChar.gone()
                    rvDialogCustomer.visible()
                    callManageClient(0, strSearch)
                } else {
                    txtChar.visible()
                    rvDialogCustomer.gone()
                }
            }

            private fun callManageClient(mode: Int, searchdata: String) {
                if (mode == 1) {
                    showProgress()
                }
                var jsonObject = JSONObject()
                jsonObject.put("GroupCode", searchdata)
                jsonObject.put("FirstName", searchdata)
                val call =
                    ApiUtils.apiInterface.ManageLeadsFindAll(getRequestJSONBody(jsonObject.toString()))
                call.enqueue(object : Callback<LeadResponse> {
                    override fun onResponse(
                        call: Call<LeadResponse>, response: Response<LeadResponse>
                    ) {
                        hideProgress()
                        if (response.code() == 200) {
                            if (response.body()?.Status == 200) {
                                arrayListClient = response.body()?.Data!!

                                if (arrayListClient!!.size > 0) {
                                    val itemAdapter = BottomSheetClientListAdapter(
                                        this@AddNBActivity, arrayListClient!!
                                    )
                                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                                        override fun onItemClickEvent(
                                            v: View, pos: Int, flag: Int
                                        ) {

                                            itemAdapter.updateItem(pos)

                                            if (state.equals(AppConstant.S_ADD)) {
                                                callManageLeadGUID(
                                                    arrayListClient!![pos].LeadGUID!!,
                                                    dialogSelectClient
                                                )
                                                callManageFamilyDetails(arrayListClient!![pos].ID!!)
                                            }
                                        }
                                    })
                                    rvDialogCustomer.adapter = itemAdapter
                                }
                            } else {
                                Snackbar.make(
                                    layout,
                                    response.body()?.Details.toString(),
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<LeadResponse>, t: Throwable) {
                        hideProgress()
                        Snackbar.make(
                            layout,
                            getString(R.string.error_failed_to_connect),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                })

            }
        })
        dialogSelectClient!!.show()
    }

    private fun selectFamilyMemberDialog() {
        var dialogSelectFamilyMember = Dialog(this)
        dialogSelectFamilyMember.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectFamilyMember.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectFamilyMember.window!!.attributes)

        dialogSelectFamilyMember.window!!.attributes = lp
        dialogSelectFamilyMember.setCancelable(true)
        dialogSelectFamilyMember.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectFamilyMember.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectFamilyMember.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectFamilyMember.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectFamilyMember.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectFamilyMember.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectFamilyMember.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectFamilyMember.dismiss()
        }

        txtid.text = "Select Family Member"

        val itemAdapter = BottomSheetFamilyMemberListAdapter(this, arrayListFamilyMember!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                itemAdapter.updateItem(pos)
                mFamilyMember =
                    arrayListFamilyMember!![pos].FirstName!! + " " + arrayListFamilyMember!![pos].LastName!!
                mFamilyMemberID = arrayListFamilyMember!![pos].ID!!
                adapter.updateFamilyMemberItem(
                    mFamilyMemberItemPostion,
                    mFamilyMember,
                    mFamilyMemberID
                )
                dialogSelectFamilyMember!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListFamilyMember!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<FamilyModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListFamilyMember!!) {
                        if (model.FirstName!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                            model.LastName!!.toLowerCase().contains(strSearch.toLowerCase())
                        ) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetFamilyMemberListAdapter(this@AddNBActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mFamilyMemberID = arrItemsFinal1[pos].ID!!
                            mFamilyMember =
                                arrItemsFinal1[pos].FirstName + " " + arrItemsFinal1[pos].LastName
                            adapter.updateFamilyMemberItem(
                                mFamilyMemberItemPostion,
                                mFamilyMember,
                                mFamilyMemberID
                            )
                            dialogSelectFamilyMember.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetFamilyMemberListAdapter(
                            this@AddNBActivity,
                            arrayListFamilyMember!!
                        )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mFamilyMemberID = arrayListFamilyMember!![pos].ID!!
                            mFamilyMember =
                                arrayListFamilyMember!![pos].FirstName + " " + arrayListFamilyMember!![pos].LastName
                            adapter.updateFamilyMemberItem(
                                mFamilyMemberItemPostion,
                                mFamilyMember,
                                mFamilyMemberID
                            )
                            dialogSelectFamilyMember!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectFamilyMember!!.show()
    }

    private fun callManageInquiryType(mode: Int) {
        if (mode == 1) {
            showProgress()
        }
        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call =
            ApiUtils.apiInterface.ManageInquiryType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<InquiryTypeResponse> {
            override fun onResponse(
                call: Call<InquiryTypeResponse>, response: Response<InquiryTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListInquiryType = response.body()?.Data!!

                        if (mode == 1) {
                            selectInquiryTypeDialog()
                        }
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<InquiryTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
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
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectInquiryType.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectInquiryType.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectInquiryType.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectInquiryType.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectInquiryType.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectInquiryType.dismiss()
        }

        txtid.text = "Select Inquiry Type"

        val itemAdapter = BottomSheetInquiryTypeListAdapter(this, arrayListInquiryType!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mInquiryTypeID = arrayListInquiryType!![pos].ID!!
                mInquiryType = arrayListInquiryType!![pos].InquiryType!!

                adapter.updateInquiryTypeItem(mInquiryTypeItemPostion, mInquiryType, mInquiryTypeID)

                arrayListInquirySubType = ArrayList()
                adapter.updateInquirySubTypeItem(mInquiryTypeItemPostion, "", 0)

                callManageInquirySubType(1, mInquiryTypeID)
                dialogSelectInquiryType!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListInquiryType!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<InquiryTypeModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListInquiryType!!) {
                        if (model.InquiryType!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetInquiryTypeListAdapter(this@AddNBActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquiryTypeID = arrItemsFinal1!![pos].ID!!
                            mInquiryType = arrItemsFinal1!![pos].InquiryType!!

                            adapter.updateInquiryTypeItem(
                                mInquiryTypeItemPostion, mInquiryType, mInquiryTypeID
                            )

                            arrayListInquirySubType = ArrayList()
                            adapter.updateInquirySubTypeItem(mInquiryTypeItemPostion, "", 0)

                            callManageInquirySubType(1, mInquiryTypeID)
                            dialogSelectInquiryType!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetInquiryTypeListAdapter(
                        this@AddNBActivity, arrayListInquiryType!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquiryTypeID = arrayListInquiryType!![pos].ID!!
                            mInquiryType = arrayListInquiryType!![pos].InquiryType!!

                            adapter.updateInquiryTypeItem(
                                mInquiryTypeItemPostion, mInquiryType, mInquiryTypeID
                            )

                            arrayListInquirySubType = ArrayList()
                            adapter.updateInquirySubTypeItem(mInquiryTypeItemPostion, "", 0)

                            callManageInquirySubType(1, mInquiryTypeID)
                            dialogSelectInquiryType!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectInquiryType!!.show()
    }

    private fun callManageInquirySubType(mode: Int, mInquiryTypeID: Int) {

        if (mode == 1) {
            showProgress()
        }

        var jsonObject = JSONObject()
        jsonObject.put("InquiryTypeID", mInquiryTypeID)
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call =
            ApiUtils.apiInterface.ManageInquirySubType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<InquirySubTypeResponse> {
            override fun onResponse(
                call: Call<InquirySubTypeResponse>, response: Response<InquirySubTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListInquirySubType = response.body()?.Data!!

                        if (mode == 1) {
                            selectInquirySubTypeDialog()
                        }
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<InquirySubTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectInquirySubTypeDialog() {
        var dialogSelectInquirySubType = Dialog(this)
        dialogSelectInquirySubType.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectInquirySubType.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectInquirySubType.window!!.attributes)

        dialogSelectInquirySubType.window!!.attributes = lp
        dialogSelectInquirySubType.setCancelable(true)
        dialogSelectInquirySubType.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectInquirySubType.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectInquirySubType.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectInquirySubType.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectInquirySubType.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectInquirySubType.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectInquirySubType.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectInquirySubType.dismiss()
        }

        txtid.text = "Select Inquiry Sub Type"

        val itemAdapter = BottomSheetInquirySubTypeListAdapter(this, arrayListInquirySubType!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mInquirySubTypeID = arrayListInquirySubType!![pos].ID!!
                mInquirySubType = arrayListInquirySubType!![pos].InquirySubType!!
                adapter.updateInquirySubTypeItem(
                    mInquirySubTypeItemPostion, mInquirySubType, mInquirySubTypeID
                )
                dialogSelectInquirySubType!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListInquirySubType!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<InquirySubTypeModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListInquirySubType!!) {
                        if (model.InquirySubType!!.toLowerCase()
                                .contains(strSearch.toLowerCase())
                        ) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetInquirySubTypeListAdapter(this@AddNBActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquirySubTypeID = arrItemsFinal1!![pos].ID!!
                            mInquirySubType = arrItemsFinal1!![pos].InquirySubType!!
                            adapter.updateInquirySubTypeItem(
                                mInquirySubTypeItemPostion, mInquirySubType, mInquirySubTypeID
                            )
                            dialogSelectInquirySubType!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetInquirySubTypeListAdapter(
                        this@AddNBActivity, arrayListInquirySubType!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquirySubTypeID = arrayListInquirySubType!![pos].ID!!
                            mInquirySubType = arrayListInquirySubType!![pos].InquirySubType!!
                            adapter.updateInquirySubTypeItem(
                                mInquirySubTypeItemPostion, mInquirySubType, mInquirySubTypeID
                            )
                            dialogSelectInquirySubType!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectInquirySubType!!.show()
    }

    private fun callManageLeadType(mode: Int) {
        if (mode == 1) {
            showProgress()
        }
        val call = ApiUtils.apiInterface.getLeadTypeAllActive()
        call.enqueue(object : Callback<LeadTypeResponse> {
            override fun onResponse(
                call: Call<LeadTypeResponse>, response: Response<LeadTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListleadtype = response.body()?.Data!!

                        if (mode == 1) {
                            selectLeadTypeDialog()
                        }
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<LeadTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
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
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectLeadType.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectLeadType.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectLeadType.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectLeadType.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectLeadType.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectLeadType.dismiss()
        }

        txtid.text = "Select Lead Type"

        val itemAdapter = BottomSheetLeadTypeListAdapter(this, arrayListleadtype!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mLeadtype = arrayListleadtype!![pos].LeadType!!
                mLeadtypeID = arrayListleadtype!![pos].ID!!
                adapter.updateLeadTypeItem(mLeadtypeItemPostion, mLeadtype, mLeadtypeID)
                dialogSelectLeadType!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListleadtype!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<LeadTypeModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListleadtype!!) {
                        if (model.LeadType!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetLeadTypeListAdapter(this@AddNBActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mLeadtype = arrItemsFinal1!![pos].LeadType!!
                            mLeadtypeID = arrItemsFinal1!![pos].ID!!
                            adapter.updateLeadTypeItem(mLeadtypeItemPostion, mLeadtype, mLeadtypeID)
                            dialogSelectLeadType!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetLeadTypeListAdapter(this@AddNBActivity, arrayListleadtype!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mLeadtype = arrayListleadtype!![pos].LeadType!!
                            mLeadtypeID = arrayListleadtype!![pos].ID!!
                            adapter.updateLeadTypeItem(mLeadtypeItemPostion, mLeadtype, mLeadtypeID)
                            dialogSelectLeadType!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectLeadType!!.show()
    }

    private fun callManageLeadStatus(mode: Int) {
        if (mode == 1) {
            showProgress()
        }
        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageLeadStatus(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<LeadStatusResponse> {
            override fun onResponse(
                call: Call<LeadStatusResponse>, response: Response<LeadStatusResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListleadstatus = response.body()?.Data!!

                        if (mode == 1) {
                            selectLeadStatusDialog()
                        }
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<LeadStatusResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectLeadStatusDialog() {
        var dialogSelectLeadStatus = Dialog(this)
        dialogSelectLeadStatus.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectLeadStatus.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectLeadStatus.window!!.attributes)

        dialogSelectLeadStatus.window!!.attributes = lp
        dialogSelectLeadStatus.setCancelable(true)
        dialogSelectLeadStatus.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectLeadStatus.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectLeadStatus.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectLeadStatus.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectLeadStatus.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectLeadStatus.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectLeadStatus.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectLeadStatus.dismiss()
        }

        txtid.text = "Select Lead Status"

        val itemAdapter = BottomSheetLeadStatusListAdapter(this, arrayListleadstatus!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mLeadstatus = arrayListleadstatus!![pos].LeadStatus!!
                mLeadstatusID = arrayListleadstatus!![pos].ID!!
                adapter.updateLeadStatusItem(mLeadstatusItemPostion, mLeadstatus, mLeadstatusID)
                dialogSelectLeadStatus!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListleadstatus!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<LeadStatusModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListleadstatus!!) {
                        if (model.LeadStatus!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetLeadStatusListAdapter(this@AddNBActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mLeadstatus = arrItemsFinal1!![pos].LeadStatus!!
                            mLeadstatusID = arrItemsFinal1!![pos].ID!!
                            adapter.updateLeadStatusItem(
                                mLeadstatusItemPostion, mLeadstatus, mLeadstatusID
                            )

                            dialogSelectLeadStatus!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetLeadStatusListAdapter(this@AddNBActivity, arrayListleadstatus!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mLeadstatus = arrayListleadstatus!![pos].LeadStatus!!
                            mLeadstatusID = arrayListleadstatus!![pos].ID!!
                            adapter.updateLeadStatusItem(
                                mLeadstatusItemPostion, mLeadstatus, mLeadstatusID
                            )

                            dialogSelectLeadStatus!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectLeadStatus!!.show()
    }

    private fun callManageInquiryAllotmentTo(mode: Int) {
        if (mode == 1) {
            showProgress()
        }

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(this)
        }
        val Userid = sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_ID)!!

        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageUsers(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>, response: Response<UserResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListInquiryAllotmentTo = response.body()?.Data!!
                        if (mode == 1) {
                            selectInquiryAllotmentToDialog()
                        }
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectInquiryAllotmentToDialog() {
        var dialogSelectInquiryAllotmentTo = Dialog(this)
        dialogSelectInquiryAllotmentTo.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectInquiryAllotmentTo.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectInquiryAllotmentTo.window!!.attributes)

        dialogSelectInquiryAllotmentTo.window!!.attributes = lp
        dialogSelectInquiryAllotmentTo.setCancelable(true)
        dialogSelectInquiryAllotmentTo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectInquiryAllotmentTo.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectInquiryAllotmentTo.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectInquiryAllotmentTo.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectInquiryAllotmentTo.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectInquiryAllotmentTo.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectInquiryAllotmentTo.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectInquiryAllotmentTo.dismiss()
        }

        txtid.text = "Select Allotment To"

        val itemAdapter = BottomSheetUsersListAdapter(this, arrayListInquiryAllotmentTo!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                itemAdapter.updateItem(pos)
                mInquiryAllotmentToID = arrayListInquiryAllotmentTo!![pos].ID!!
                mInquiryAllotmentTo =
                    arrayListInquiryAllotmentTo!![pos].FirstName!! + " " + arrayListInquiryAllotmentTo!![pos].LastName!!
                adapter.updateAllotmentToItem(
                    mAllotmentToItemPostion, mInquiryAllotmentTo, mInquiryAllotmentToID
                )
                dialogSelectInquiryAllotmentTo!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListInquiryAllotmentTo!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<UserModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListInquiryAllotmentTo!!) {
                        if (model.FirstName!!.toLowerCase()
                                .contains(strSearch.toLowerCase()) || model.LastName!!.toLowerCase()
                                .contains(strSearch.toLowerCase())
                        ) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetUsersListAdapter(this@AddNBActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquiryAllotmentToID = arrItemsFinal1!![pos].ID!!
                            mInquiryAllotmentTo =
                                arrItemsFinal1!![pos].FirstName!! + " " + arrItemsFinal1!![pos].LastName!!
                            adapter.updateAllotmentToItem(
                                mAllotmentToItemPostion, mInquiryAllotmentTo, mInquiryAllotmentToID
                            )

                            dialogSelectInquiryAllotmentTo!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetUsersListAdapter(
                        this@AddNBActivity, arrayListInquiryAllotmentTo!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquiryAllotmentToID = arrayListInquiryAllotmentTo!![pos].ID!!
                            mInquiryAllotmentTo =
                                arrayListInquiryAllotmentTo!![pos].FirstName!! + " " + arrayListInquiryAllotmentTo!![pos].LastName!!
                            adapter.updateAllotmentToItem(
                                mAllotmentToItemPostion, mInquiryAllotmentTo, mInquiryAllotmentToID
                            )

                            dialogSelectInquiryAllotmentTo!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectInquiryAllotmentTo!!.show()
    }

    private fun selectFrequencyDialog() {
        var dialogSelectFrequency = Dialog(this)
        dialogSelectFrequency.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectFrequency.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectFrequency.window!!.attributes)

        dialogSelectFrequency.window!!.attributes = lp
        dialogSelectFrequency.setCancelable(true)
        dialogSelectFrequency.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectFrequency.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectFrequency.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectFrequency.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectFrequency.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectFrequency.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectFrequency.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectFrequency.dismiss()
        }

        txtid.text = "Select Frequency"


        val itemAdapter = BottomSheetListAdapter(this, arrayListFrequency!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                itemAdapter.updateItem(pos)
                mFrequency = arrayListFrequency!![pos].Name!!
                mFrequencyID = arrayListFrequency!![pos].ID!!
                adapter.updateFrequencyItem(mFrequencyItemPostion, mFrequency, mFrequencyID)
                dialogSelectFrequency!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListFrequency!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<SingleSelectionModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListFrequency!!) {
                        if (model.Name!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetListAdapter(this@AddNBActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mFrequency = arrItemsFinal1!![pos].Name!!
                            mFrequencyID = arrItemsFinal1!![pos].ID!!
                            adapter.updateFrequencyItem(
                                mFrequencyItemPostion, mFrequency, mFrequencyID
                            )
                            dialogSelectFrequency!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetListAdapter(this@AddNBActivity, arrayListFrequency!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mFrequency = arrayListFrequency!![pos].Name!!
                            mFrequencyID = arrayListFrequency!![pos].ID!!
                            adapter.updateFrequencyItem(
                                mFrequencyItemPostion, mFrequency, mFrequencyID
                            )
                            dialogSelectFrequency!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectFrequency!!.show()
    }

    private fun showDatePickerDialog() {

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calendarNow!!.set(Calendar.YEAR, year)
                calendarNow!!.set(Calendar.MONTH, monthOfYear)
                calendarNow!!.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val mdate = SimpleDateFormat(
                    AppConstant.yyyy_MM_dd_Dash, Locale.US
                ).format(calendarNow!!.time)

                val selecteddate = SimpleDateFormat(
                    AppConstant.dd_MM_yyyy_HH_mm_ss, Locale.US
                ).format(calendarNow!!.time)
                val date = convertDateStringToString(
                    selecteddate, AppConstant.dd_MM_yyyy_HH_mm_ss, AppConstant.dd_LLL_yyyy
                )

                adapter.updateInquiryDateItem(mInquiryDateItemPostion, date!!, mdate)
            },
            calendarNow!!.get(Calendar.YEAR),
            calendarNow!!.get(Calendar.MONTH),
            calendarNow!!.get(Calendar.DAY_OF_MONTH)
        )
//                dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.datePicker.maxDate = System.currentTimeMillis()
        dpd.show()
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int, name: String) {
        hideKeyboard(applicationContext, view)
        when (view.id) {
            R.id.edtFamilyMember -> {
                preventTwoClick(view)
                mFamilyMemberItemPostion = position
                if (!arrayListFamilyMember.isNullOrEmpty()) {
                    selectFamilyMemberDialog()
                } else {
                    toast("Select Client", Toast.LENGTH_SHORT)
                }
            }

            R.id.edtInquiryType -> {
                preventTwoClick(view)
                mInquiryTypeItemPostion = position
                if (arrayListInquiryType.isNullOrEmpty()) {
                    callManageInquiryType(1)
                } else {
                    selectInquiryTypeDialog()
                }
            }

            R.id.edtInquirySub -> {
                preventTwoClick(view)
                mInquirySubTypeItemPostion = position
                selectInquirySubTypeDialog()

            }

            R.id.edtFrequency -> {
                preventTwoClick(view)
                mFrequencyItemPostion = position
                selectFrequencyDialog()
            }

            R.id.edtAllotmentTo -> {
                preventTwoClick(view)
                mAllotmentToItemPostion = position
                if (arrayListInquiryAllotmentTo.isNullOrEmpty()) {
                    callManageInquiryAllotmentTo(1)
                } else {
                    selectInquiryAllotmentToDialog()
                }
            }

            R.id.edtLeadType -> {
                preventTwoClick(view)
                mLeadtypeItemPostion = position
                if (arrayListleadtype.isNullOrEmpty()) {
                    callManageLeadType(1)
                } else {
                    selectLeadTypeDialog()
                }
            }

            R.id.edtLeadStatus -> {
                preventTwoClick(view)
                mLeadstatusItemPostion = position
                if (arrayListleadstatus.isNullOrEmpty()) {
                    callManageLeadStatus(1)
                } else {
                    selectLeadStatusDialog()
                }
            }

            R.id.edtInquiryDate -> {
                preventTwoClick(view)
                mInquiryDateItemPostion = position
                showDatePickerDialog()
            }

            R.id.imgDelete -> {
                if (::adapter.isInitialized) {
                    adapter.remove(position)
                }
            }

            R.id.tvAddMore -> {
                preventTwoClick(view)
                if (::adapter.isInitialized) {
                    adapter.addItem(
                        InquiryInformationModel(
                            AllotmentToId = mAllotmentToID, AllotmentTo = mAllotmentTo
                        ), 1
                    )
                }
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {

    }

    private fun callManageNBInquiryGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("NBInquiryGUID", NBInquiryGUID)
//        jsonObject.put("OperationType", 10)

        val call =
            ApiUtils.apiInterface.ManageNBInquiryByGUID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<NBInquiryByGUIDResponse> {
            override fun onResponse(
                call: Call<NBInquiryByGUIDResponse>, response: Response<NBInquiryByGUIDResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arraylist = response.body()?.Data!!
                        SetAPIData(arraylist)
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<NBInquiryByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun SetAPIData(model: NBModel) {


        if (model.AllotmentID == sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_ID)!!.toInt() ||
            sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_TYPE_ID)!!.toInt() == 1) {
            AddMore = true
            txtSave.visible()
            enableDisableViewGroup(llContent,true)
        } else {
            AddMore = false
            View = true
            txtSave.gone()
            txtHearderText.text = "View NB Inquiry"
            enableDisableViewGroup(llContent,false)
        }


        if (model.LeadID != 0 && model.LeadID != null) {
            callManageFamilyDetails(model.LeadID)
            mClientID = model.LeadID
            mClient = model.LeadName!!
            mClientGUID = model.LeadGUID!!
            mClientStageID = model.LeadStageID!!
            edtClient.setText(mClient)
            edtClient.setError(null)

            callManageLeadGUID(mClientGUID, null)

            if (mClientStageID.equals(1)) {
                edtClientType.setText("Existing Client")
            } else if (mClientStageID.equals(2)) {
                edtClientType.setText("Prospect")
            }
        }

        if (model.AllotmentID != 0 && model.AllotmentID != null) {
            mAllotmentToID = model.AllotmentID
            mAllotmentTo = model.AllotmentName!!
            edtAllotmentTo.setText(mAllotmentTo)
            edtAllotmentTo.setError(null)
        }

        if (model.FamilyID != 0 && model.FamilyID != null) {
            mFamilyMemberID = model.FamilyID
            mFamilyMember = model.FamilyMemberName!!
        }

        if (model.NBInquiryList!!.size > 0) {
            arrayListInquiryInfo = ArrayList()

            for (i in 0 until model.NBInquiryList.size) {

                val mDate = convertDateStringToString(
                    model.NBInquiryList[i].InquiryDate!!,
                    AppConstant.DATE_INPUT_FORMAT,
                    AppConstant.DEFAULT_DATE_FORMAT
                )

                arrayListInquiryInfo?.add(
                    InquiryInformationModel(
                        FamilyMemberId = model.NBInquiryList[i].NBInquiryBy!!,
                        FamilyMember = model.NBInquiryList[i].NBInquiryByName!!,
                        InquirytypeId = model.NBInquiryList[i].InquiryTypeID!!,
                        Inquirytype = model.NBInquiryList[i].InquiryType!!,
                        InquirysubtypeId = model.NBInquiryList[i].InquirySubTypeID!!,
                        Inquirysubtype = model.NBInquiryList[i].InquirySubType!!,
                        Proposed = model.NBInquiryList[i].ProposedAmount!!.toString(),
                        Frequency = model.NBInquiryList[i].Frequency!!,
                        LeadtypeId = model.NBInquiryList[i].LeadTypeID!!,
                        Leadtype = model.NBInquiryList[i].LeadType!!,
                        LeadstatusId = model.NBInquiryList[i].LeadStatusID!!,
                        Leadstatus = model.NBInquiryList[i].LeadStatus!!,
                        AllotmentToId = model.NBInquiryList[i].InquiryAllotmentID!!,
                        AllotmentTo = model.NBInquiryList[i].InquiryAllotmentName!!,
                        InquiryDate = model.NBInquiryList[i].InquiryDate!!,
                        mInquiryDate = mDate!!
                    )
                )
            }
            setAdapterData(arrayListInquiryInfo)
        }
        hideProgress()
    }

    private fun callManageLeadGUID(mClientGUID: String, dialogSelectClient: Dialog?) {

        if (dialogSelectClient != null) {
            showProgress()
        }

        var jsonObject = JSONObject()
        jsonObject.put("LeadGUID", mClientGUID)

        val call =
            ApiUtils.apiInterface.ManageLeadsFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<LeadByGUIDResponse> {
            override fun onResponse(
                call: Call<LeadByGUIDResponse>,
                response: Response<LeadByGUIDResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListLead = response.body()?.Data!!
//                        Log.e("arrayList","===>"+arrayListLead.toString())
                        if (state.equals(AppConstant.S_ADD)) {
                            if (dialogSelectClient != null) {
                                setAPIData(arrayListLead, dialogSelectClient!!)
                            }
                        }
                        if (dialogSelectClient != null) {
                            hideProgress()
                        }
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<LeadByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun callManageFamilyDetails(ID: Int) {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("ID", ID)

        val call = ApiUtils.apiInterface.ManageFamilyList(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<FamilyResponse> {
            override fun onResponse(
                call: Call<FamilyResponse>,
                response: Response<FamilyResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListFamily = response.body()?.Data!!
                        if (state.equals(AppConstant.S_ADD)) {
                            setAPIData(arrayListFamily)
                        } else {
                            setAPIDataWhenEdit(arrayListFamily)
                        }
                        hideProgress()
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<FamilyResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setAPIDataWhenEdit(familyModel: ArrayList<FamilyModel>) {
        arrayListFamilyMember = familyModel

    }

    private fun setAPIData(leadModel: LeadModel, dialogSelectClient: Dialog) {
        mClient = leadModel.FirstName!! + " " + leadModel.LastName
        mClientID = leadModel.ID!!
        mClientGUID = leadModel.LeadGUID!!
        mClientStageID = leadModel.LeadStage!!
        edtClient.setText(mClient)
        edtClient.setError(null)

        adapter.clearAllFamilyMembers()

        if (mClientStageID.equals(1)) {
            edtClientType.setText("Existing Client")
        } else if (mClientStageID.equals(2)) {
            edtClientType.setText("Prospect")
        }

        dialogSelectClient!!.dismiss()
    }

    private fun setAPIData(familyModel: ArrayList<FamilyModel>) {
        arrayListFamilyMember = familyModel
    }

}