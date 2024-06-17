package com.app.insurancevala.activity.LeadList

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
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
import com.app.insurancevala.model.pojo.InquiryInformationModel
import com.app.insurancevala.model.response.*
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_leadlist.layout
import kotlinx.android.synthetic.main.activity_add_leadlist.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddLeadListActivity : BaseActivity(), View.OnClickListener{

    var sharedPreference: SharedPreference? = null

    var mClientID: Int = 0

    var arrayListFamilyMember: ArrayList<FamilyModel>? = ArrayList()
    var mFamilyMember: String = ""
    var mFamilyMemberID: Int = 0

    var arrayListleadtype: ArrayList<LeadTypeModel>? = ArrayList()
    var mLeadtype: String = ""
    var mLeadtypeID: Int = 0

    var arrayListleadstatus: ArrayList<LeadStatusModel>? = ArrayList()
    var mLeadstatus: String = ""
    var mLeadstatusID: Int = 0

    var arrayListInquiryType: ArrayList<InquiryTypeModel>? = ArrayList()
    var mInquiryType: String = ""
    var mInquiryTypeID: Int = 0

    var arrayListFrequency: ArrayList<SingleSelectionModel> = ArrayList()
    var mFrequency: String = ""
    var mFrequencyID: Int = 0

    var arrayListInquirySubType: ArrayList<InquirySubTypeModel>? = ArrayList()
    var mInquirySubType: String = ""
    var mInquirySubTypeID: Int = 0

    var arrayListInquiryAllotmentTo: ArrayList<UserModel>? = ArrayList()
    var mInquiryAllotmentTo: String = ""
    var mInquiryAllotmentToID: Int = 0

    var mLeadDate: String = ""
    var calendarNow: Calendar? = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_leadlist)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {

        mClientID = intent.getIntExtra("LeadID", 0)

        mInquiryAllotmentToID = intent.getIntExtra("LeadOwnerID", 0)
        mInquiryAllotmentTo = intent.getStringExtra("LeadOwnerName").toString()

        if (mClientID != 0) {
            callManageFamilyDetails(mClientID)
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
            callManageInquiryAllotmentTo(0)
            callManageInquiryType(0)
            callManageLeadType(0)
            callManageLeadStatus(0)
        } else {
            internetErrordialog(this@AddLeadListActivity)
        }
        txtHearderText.text = "Create Lead"
        txtSave.visible()

        arrayListFrequency.add(SingleSelectionModel(1, "Single", true))
        arrayListFrequency.add(SingleSelectionModel(2, "Yearly", false))
        arrayListFrequency.add(SingleSelectionModel(3, "Half Yearly", false))
        arrayListFrequency.add(SingleSelectionModel(4, "Quaterly", false))
        arrayListFrequency.add(SingleSelectionModel(5, "Monthly", false))
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        txtSave.setOnClickListener(this)
        edtFamilyMember.setOnClickListener(this)
        edtInquiryType.setOnClickListener(this)
        edtInquirySub.setOnClickListener(this)
        edtFrequency.setOnClickListener(this)
        edtAllotmentTo.setOnClickListener(this)
        edtLeadType.setOnClickListener(this)
        edtLeadStatus.setOnClickListener(this)
        edtLeadDate.setOnClickListener(this)

        rvInquiry.layoutManager = LinearLayoutManager(
            applicationContext, RecyclerView.VERTICAL, false
        )
        rvInquiry.isNestedScrollingEnabled = false
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }

            R.id.edtFamilyMember -> {
                preventTwoClick(v)
                if (!arrayListFamilyMember.isNullOrEmpty()) {
                    selectFamilyMemberDialog()
                } else {
                    toast("Select Client", Toast.LENGTH_SHORT)
                }
            }

            R.id.edtInquiryType -> {
                preventTwoClick(v)
                if (arrayListInquiryType.isNullOrEmpty()) {
                    callManageInquiryType(1)
                } else {
                    selectInquiryTypeDialog()
                }
            }

            R.id.edtInquirySub -> {
                preventTwoClick(v)
                selectInquirySubTypeDialog()

            }

            R.id.edtFrequency -> {
                preventTwoClick(v)
                selectFrequencyDialog()
            }

            R.id.edtAllotmentTo -> {
                preventTwoClick(v)
                if (arrayListInquiryAllotmentTo.isNullOrEmpty()) {
                    callManageInquiryAllotmentTo(1)
                } else {
                    selectInquiryAllotmentToDialog()
                }
            }

            R.id.edtLeadType -> {
                preventTwoClick(v)
                if (arrayListleadtype.isNullOrEmpty()) {
                    callManageLeadType(1)
                } else {
                    selectLeadTypeDialog()
                }
            }

            R.id.edtLeadStatus -> {
                preventTwoClick(v)
                if (arrayListleadstatus.isNullOrEmpty()) {
                    callManageLeadStatus(1)
                } else {
                    selectLeadStatusDialog()
                }
            }

            R.id.edtLeadDate -> {
                preventTwoClick(v)
                showDatePickerDialog()
            }

            R.id.txtSave -> {
                preventTwoClick(v)
                validation()
            }
        }
    }

    fun validation() {
        val flag = isValidate()
        if (flag) {
            if (isOnline(this)) {
                callManageNBInquiry()
            } else {
                internetErrordialog(this@AddLeadListActivity)
            }
        }
    }

    fun isValidate(): Boolean {
        var isValidate = true
        if (edtFamilyMember.text.isEmpty()) {
            edtFamilyMember.setError("Select Family Member", errortint(this))
            isValidate = false
        }
        if (edtLeadDate.text.isEmpty()) {
            edtLeadDate.setError("Select Lead Date", errortint(this))
            isValidate = false
        }
        if (edtInquiryType.text.isEmpty()) {
            edtInquiryType.setError("Select Inquiry Type", errortint(this))
            isValidate = false
        }
        if (edtInquirySub.text.isEmpty()) {
            edtInquirySub.setError("Select Inquiry Sub Type", errortint(this))
            isValidate = false
        }
        if (edtLeadType.text.isEmpty()) {
            edtLeadType.setError("Select Lead Type", errortint(this))
            isValidate = false
        }
        if (edtLeadStatus.text.isEmpty()) {
            edtLeadStatus.setError("Select Lead Status", errortint(this))
            isValidate = false
        }
        if (edtAllotmentTo.text.isEmpty()) {
            edtAllotmentTo.setError("Select Allotment To", errortint(this))
            isValidate = false
        }
        if (edtProposedAmount.text.isEmpty()) {
            edtProposedAmount.setError("Enter Proposed Amount", errortint(this))
            isValidate = false
        }
        if (edtFrequency.text.isEmpty()) {
            edtFrequency.setError("Select Frequency", errortint(this))
            isValidate = false
        }

        return isValidate
    }

    fun callManageNBInquiry() {

        showProgress()

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(applicationContext)
        }

        val jsonObject = JSONObject()
        jsonObject.put("LeadID", mClientID)
        jsonObject.put("NBLeadBy", mFamilyMemberID)
        jsonObject.put("LeadAllotmentID", mInquiryAllotmentToID)
        jsonObject.put("InquiryTypeID", mInquiryTypeID)
        jsonObject.put("InquirySubTypeID", mInquirySubTypeID)
        jsonObject.put("InquirySubTypeID", mInquirySubTypeID)
        jsonObject.put("LeadTypeID", mLeadtypeID)
        jsonObject.put("LeadTypeID", mLeadtypeID)
        jsonObject.put("LeadStatusID", mLeadstatusID)
        jsonObject.put("ProposedAmount", edtProposedAmount.text.toString().toDouble())
        jsonObject.put("Frequency", mFrequency)
        jsonObject.put("LeadDate", mLeadDate)
        jsonObject.put("IsActive", true)

        val call = ApiUtils.apiInterface.ManageNBLeadInsert(getRequestJSONBody(jsonObject.toString()))

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
                mFamilyMember = arrayListFamilyMember!![pos].FirstName!! + " " + arrayListFamilyMember!![pos].LastName!!
                mFamilyMemberID = arrayListFamilyMember!![pos].ID!!
                edtFamilyMember.setText(mFamilyMember)
                edtFamilyMember.setError(null)
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
                        BottomSheetFamilyMemberListAdapter(this@AddLeadListActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mFamilyMemberID = arrItemsFinal1[pos].ID!!
                            mFamilyMember = arrItemsFinal1[pos].FirstName + " " + arrItemsFinal1[pos].LastName
                            edtFamilyMember.setText(mFamilyMember)
                            edtFamilyMember.setError(null)
                            dialogSelectFamilyMember.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetFamilyMemberListAdapter(
                            this@AddLeadListActivity,
                            arrayListFamilyMember!!
                        )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mFamilyMemberID = arrayListFamilyMember!![pos].ID!!
                            mFamilyMember = arrayListFamilyMember!![pos].FirstName + " " + arrayListFamilyMember!![pos].LastName
                            edtFamilyMember.setText(mFamilyMember)
                            edtFamilyMember.setError(null)
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

                edtInquiryType.setText(mInquiryType)
                edtInquiryType.setError(null)

                arrayListInquirySubType = ArrayList()
                mInquirySubType = ""
                mInquirySubTypeID = 0
                edtInquirySub.setText("")
                edtInquirySub.setError(null)

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
                        BottomSheetInquiryTypeListAdapter(this@AddLeadListActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquiryTypeID = arrItemsFinal1!![pos].ID!!
                            mInquiryType = arrItemsFinal1!![pos].InquiryType!!

                            edtInquiryType.setText(mInquiryType)
                            edtInquiryType.setError(null)

                            arrayListInquirySubType = ArrayList()
                            mInquirySubType = ""
                            mInquirySubTypeID = 0
                            edtInquirySub.setText("")
                            edtInquirySub.setError(null)

                            callManageInquirySubType(1, mInquiryTypeID)
                            dialogSelectInquiryType!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetInquiryTypeListAdapter(
                        this@AddLeadListActivity, arrayListInquiryType!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquiryTypeID = arrayListInquiryType!![pos].ID!!
                            mInquiryType = arrayListInquiryType!![pos].InquiryType!!

                            edtInquiryType.setText(mInquiryType)
                            edtInquiryType.setError(null)

                            arrayListInquirySubType = ArrayList()
                            mInquirySubType = ""
                            mInquirySubTypeID = 0
                            edtInquirySub.setText("")
                            edtInquirySub.setError(null)

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
                edtInquirySub.setText(mInquirySubType)
                edtInquirySub.setError(null)
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
                        BottomSheetInquirySubTypeListAdapter(this@AddLeadListActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquirySubTypeID = arrItemsFinal1!![pos].ID!!
                            mInquirySubType = arrItemsFinal1!![pos].InquirySubType!!
                            edtInquirySub.setText(mInquirySubType)
                            edtInquirySub.setError(null)
                            dialogSelectInquirySubType!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetInquirySubTypeListAdapter(
                        this@AddLeadListActivity, arrayListInquirySubType!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquirySubTypeID = arrayListInquirySubType!![pos].ID!!
                            mInquirySubType = arrayListInquirySubType!![pos].InquirySubType!!
                            edtInquirySub.setText(mInquirySubType)
                            edtInquirySub.setError(null)
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
                edtLeadType.setText(mLeadtype)
                edtLeadType.setError(null)
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
                        BottomSheetLeadTypeListAdapter(this@AddLeadListActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mLeadtype = arrItemsFinal1!![pos].LeadType!!
                            mLeadtypeID = arrItemsFinal1!![pos].ID!!
                            edtLeadType.setText(mLeadtype)
                            edtLeadType.setError(null)
                            dialogSelectLeadType!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetLeadTypeListAdapter(this@AddLeadListActivity, arrayListleadtype!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mLeadtype = arrayListleadtype!![pos].LeadType!!
                            mLeadtypeID = arrayListleadtype!![pos].ID!!
                            edtLeadType.setText(mLeadtype)
                            edtLeadType.setError(null)
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
                edtLeadStatus.setText(mLeadstatus)
                edtLeadStatus.setError(null)
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
                        BottomSheetLeadStatusListAdapter(this@AddLeadListActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mLeadstatus = arrItemsFinal1!![pos].LeadStatus!!
                            mLeadstatusID = arrItemsFinal1!![pos].ID!!
                            edtLeadStatus.setText(mLeadstatus)
                            edtLeadStatus.setError(null)

                            dialogSelectLeadStatus!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetLeadStatusListAdapter(this@AddLeadListActivity, arrayListleadstatus!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mLeadstatus = arrayListleadstatus!![pos].LeadStatus!!
                            mLeadstatusID = arrayListleadstatus!![pos].ID!!
                            edtLeadStatus.setText(mLeadstatus)
                            edtLeadStatus.setError(null)

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
                mInquiryAllotmentTo = arrayListInquiryAllotmentTo!![pos].FirstName!! + " " + arrayListInquiryAllotmentTo!![pos].LastName!!
                edtAllotmentTo.setText(mInquiryAllotmentTo)
                edtAllotmentTo.setError(null)
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
                        BottomSheetUsersListAdapter(this@AddLeadListActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquiryAllotmentToID = arrItemsFinal1!![pos].ID!!
                            mInquiryAllotmentTo = arrItemsFinal1!![pos].FirstName!! + " " + arrItemsFinal1!![pos].LastName!!
                            edtAllotmentTo.setText(mInquiryAllotmentTo)
                            edtAllotmentTo.setError(null)

                            dialogSelectInquiryAllotmentTo!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetUsersListAdapter(
                        this@AddLeadListActivity, arrayListInquiryAllotmentTo!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquiryAllotmentToID = arrayListInquiryAllotmentTo!![pos].ID!!
                            mInquiryAllotmentTo = arrayListInquiryAllotmentTo!![pos].FirstName!! + " " + arrayListInquiryAllotmentTo!![pos].LastName!!
                            edtAllotmentTo.setText(mInquiryAllotmentTo)
                            edtAllotmentTo.setError(null)

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
                edtFrequency.setText(mFrequency)
                edtFrequency.setError(null)
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

                    val itemAdapter = BottomSheetListAdapter(this@AddLeadListActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mFrequency = arrItemsFinal1!![pos].Name!!
                            mFrequencyID = arrItemsFinal1!![pos].ID!!
                            edtFrequency.setText(mFrequency)
                            edtFrequency.setError(null)
                            dialogSelectFrequency!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetListAdapter(this@AddLeadListActivity, arrayListFrequency!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mFrequency = arrayListFrequency!![pos].Name!!
                            mFrequencyID = arrayListFrequency!![pos].ID!!
                            edtFrequency.setText(mFrequency)
                            edtFrequency.setError(null)
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

                mLeadDate = mdate
                edtLeadDate.setText(date)
                edtLeadDate.setError(null)
            },
            calendarNow!!.get(Calendar.YEAR),
            calendarNow!!.get(Calendar.MONTH),
            calendarNow!!.get(Calendar.DAY_OF_MONTH)
        )
//                dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.datePicker.maxDate = System.currentTimeMillis()
        dpd.show()
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

                        setAPIData(arrayListFamily)

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

    private fun setAPIData(familyModel: ArrayList<FamilyModel>) {
        arrayListFamilyMember = familyModel
    }

}