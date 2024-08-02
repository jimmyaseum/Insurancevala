package com.app.insurancevala.activity.LeadList

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
import com.app.insurancevala.adapter.bottomsheetadapter.*
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.*
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_edit_lead.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeadEditActivity : BaseActivity(), View.OnClickListener {
    var sharedPreference: SharedPreference? = null
    var state: String? = null

    var NBLeadsGUID: String? = null
    var LeadID: Int? = null

    var arrayListleadtype: ArrayList<LeadTypeModel>? = ArrayList()
    var mLeadtype: String = ""
    var mLeadtypeID: Int = 0

    var arrayListleadstatus: ArrayList<LeadStatusModel>? = ArrayList()
    var mLeadstatus: String = ""
    var mLeadstatusID: Int = 0

    var arrayListInquiryType: ArrayList<InquiryTypeModel>? = ArrayList()
    var mInquiryType: String = ""
    var mInquiryTypeID: Int = 0

    var arrayListFamilyMember: ArrayList<FamilyModel>? = ArrayList()
    var mFamilyMember: String = ""
    var mFamilyMemberID: Int = 0

    var arrayListFrequency: ArrayList<SingleSelectionModel> = ArrayList()
    var mFrequency: String = ""

    var arrayListInquirySubType: ArrayList<InquirySubTypeModel>? = ArrayList()
    var mInquirySubType: String = ""
    var mInquirySubTypeID: Int = 0

    var Edit: Boolean = true

    var arrayListInquiryAllotmentTo: ArrayList<UserModel>? = ArrayList()
    var mInquiryAllotmentTo: String = ""
    var mInquiryAllotmentToID: Int = 0

    var arrayListInquiryCoPersonAllotmentTo: ArrayList<UserModel>? = ArrayList()
    var mInquiryCoPersonAllotmentTo: String = ""
    var mInquiryCoPersonAllotmentToID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_lead)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        if (intent.hasExtra("Edit")) {
            Edit = intent.getBooleanExtra("Edit", true)
        }
        state = intent.getStringExtra(AppConstant.STATE)
        NBLeadsGUID = intent.getStringExtra("NBLeadsGUID")
        LeadID = intent.getIntExtra("LeadID", 0)

        if (Edit) {
            txtButtonSubmit.visible()
            txtHearderText.text = "Update Lead"
            enableDisableViewGroup(llContent,true)
        } else {
            txtButtonSubmit.gone()
            txtHearderText.text = "View Lead"
            enableDisableViewGroup(llContent,false)
        }
    }

    override fun initializeView() {
        setMasterData()
        SetInitListner()
    }

    private fun setMasterData() {
        if (state.equals(AppConstant.S_EDIT)) {
            if (isOnline(this)) {
                callManageNBLeadGUID()
            } else {
                internetErrordialog(this@LeadEditActivity)
            }
        }

        arrayListFrequency.add(SingleSelectionModel(1, "Single", false))
        arrayListFrequency.add(SingleSelectionModel(2, "Yearly", false))
        arrayListFrequency.add(SingleSelectionModel(3, "Half Yearly", false))
        arrayListFrequency.add(SingleSelectionModel(4, "Quaterly", false))
        arrayListFrequency.add(SingleSelectionModel(5, "Monthly", false))

    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        txtButtonSubmit.setOnClickListener(this)

        edtFamilyMember.setOnClickListener(this)
        edtInquiryType.setOnClickListener(this)
        edtInquirySub.setOnClickListener(this)
        edtLeadType.setOnClickListener(this)
        edtLeadStatus.setOnClickListener(this)
        edtFrequency.setOnClickListener(this)
        edtAllotmentTo.setOnClickListener(this)
        edtCoPersonAllotmentTo.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }

            R.id.txtButtonSubmit -> {
                preventTwoClick(v)
                validation()
            }

            R.id.edtFamilyMember -> {
                preventTwoClick(v)
                if (arrayListFamilyMember.isNullOrEmpty()) {
                    callManageFamilyDetails(1)
                } else {
                    selectFamilyMemberDialog()
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
                if (arrayListInquirySubType.isNullOrEmpty()) {
                    callManageInquirySubType(1, mInquiryTypeID)
                } else {
                    selectInquirySubTypeDialog()
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

            R.id.edtAllotmentTo -> {
                preventTwoClick(v)
                if (arrayListInquiryAllotmentTo.isNullOrEmpty()) {
                    callManageInquiryAllotmentTo(1, 1)
                } else {
                    selectInquiryAllotmentToDialog()
                }
            }

            R.id.edtCoPersonAllotmentTo -> {
                preventTwoClick(v)
                if (arrayListInquiryCoPersonAllotmentTo.isNullOrEmpty()) {
                    callManageInquiryAllotmentTo(1, 2)
                } else {
                    selectInquiryCoPersonAllotmentToDialog()
                }
            }

            R.id.edtFrequency -> {
                preventTwoClick(v)
                selectFrequencyDialog()
            }
        }
    }

    private fun validation() {
        when (isValidate()) {
            true -> {
                if (isOnline(this)) {
                    callManageNBInquiryUpdate()
                } else {
                    internetErrordialog(this@LeadEditActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isValidate = true

        if (edtFamilyMember.text.isEmpty()) {
            edtFamilyMember.setError("Select Family Member", errortint(this))
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

    private fun callManageNBLeadGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("NBLeadsGUID", NBLeadsGUID)
//        jsonObject.put("OperationType", 11)

        val call =
            ApiUtils.apiInterface.ManageNBLeadFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<NBLeadListByGUIDResponse> {
            override fun onResponse(
                call: Call<NBLeadListByGUIDResponse>,
                response: Response<NBLeadListByGUIDResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
//                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        val arraylist = response.body()?.Data!!
                        SetAPIData(arraylist)
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<NBLeadListByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun callManageNBInquiryUpdate() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("NBLeadsGUID", NBLeadsGUID)
        jsonObject.put("NBInquiryBy", mFamilyMemberID)
        jsonObject.put("InquiryTypeID", mInquiryTypeID)
        jsonObject.put("InquirySubTypeID", mInquirySubTypeID)
        jsonObject.put("InquiryAllotmentID", mInquiryAllotmentToID)
        jsonObject.put("CoPersonAllotmentID", mInquiryCoPersonAllotmentToID)
        jsonObject.put("LeadTypeID", mLeadtypeID)
        jsonObject.put("LeadStatusID", mLeadstatusID)
        jsonObject.put("ProposedAmount", edtProposedAmount.text.toString().toDouble())
        jsonObject.put("Frequency", mFrequency)
//        jsonObject.put("OperationType", 8)

        val call =
            ApiUtils.apiInterface.ManageNBLeadUpdate(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<NBInquiryTypeAddUpdateResponse> {
            override fun onResponse(
                call: Call<NBInquiryTypeAddUpdateResponse>,
                response: Response<NBInquiryTypeAddUpdateResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<NBInquiryTypeAddUpdateResponse>, t: Throwable) {
                hideProgress()
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }

    private fun SetAPIData(arraylist: NBLeadListModel?) {

        if (arraylist!!.NBLeadBy != null && arraylist.NBLeadBy != 0) {
            mFamilyMemberID = arraylist.NBLeadBy!!
            mFamilyMember = arraylist.NBLeadByName.toString()
            edtFamilyMember.setText(mFamilyMember)
            callManageFamilyDetails(0)
        }

        if (arraylist!!.InquiryTypeID != null && arraylist.InquiryTypeID != 0) {
            mInquiryType = arraylist.InquiryType!!
            mInquiryTypeID = arraylist.InquiryTypeID!!
            edtInquiryType.setText(mInquiryType)
            callManageInquiryType(0)
        }

        if (arraylist.InquirySubTypeID != null && arraylist.InquirySubTypeID != 0) {
            mInquirySubType = arraylist.InquirySubType!!
            mInquirySubTypeID = arraylist.InquirySubTypeID!!
            edtInquirySub.setText(mInquirySubType)
            callManageInquirySubType(0, mInquiryTypeID)
        }

        if (arraylist.LeadTypeID != null && arraylist.LeadTypeID != 0) {
            mLeadtype = arraylist.LeadType!!
            mLeadtypeID = arraylist.LeadTypeID!!
            edtLeadType.setText(mLeadtype)
            callManageLeadType(0)
        }

        if (arraylist.LeadStatusID != null && arraylist.LeadStatusID != 0) {
            mLeadstatus = arraylist.LeadStatus!!
            mLeadstatusID = arraylist.LeadStatusID!!
            edtLeadStatus.setText(mLeadstatus)
            callManageLeadStatus(0)
        }

        if (arraylist.ProposedAmount != null && arraylist.ProposedAmount != 0.0) {
            edtProposedAmount.setText(arraylist.ProposedAmount.toString())
        }

        if (arraylist.Frequency != null && arraylist.Frequency != "") {
            mFrequency = arraylist.Frequency!!
            edtFrequency.setText(mFrequency)
            for (i in 0 until arrayListFrequency.size) {
                if (arrayListFrequency[i].Name == mFrequency) {
                    arrayListFrequency[i].IsSelected = true
                }
            }
        }

        if (arraylist.LeadAllotmentID != null && arraylist.LeadAllotmentID != 0) {
            mInquiryAllotmentTo = arraylist.LeadAllotmentName!!
            mInquiryAllotmentToID = arraylist.LeadAllotmentID!!.toInt()
            edtAllotmentTo.setText(mInquiryAllotmentTo)
            callManageInquiryAllotmentTo(0, 0)
        }

        if (arraylist.CoPersonAllotmentID != null && arraylist.CoPersonAllotmentID != 0) {
            mInquiryCoPersonAllotmentTo = arraylist.CoPersonAllotmentName!!
            mInquiryCoPersonAllotmentToID = arraylist.CoPersonAllotmentID!!.toInt()
            edtCoPersonAllotmentTo.setText(mInquiryCoPersonAllotmentTo)
            callManageInquiryAllotmentTo(0, 0)
        }

    }

    private fun callManageFamilyDetails(mode: Int) {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("ID", LeadID)

        val call = ApiUtils.apiInterface.ManageFamilyList(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<FamilyResponse> {
            override fun onResponse(
                call: Call<FamilyResponse>,
                response: Response<FamilyResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListFamilyMember = response.body()?.Data!!

                        if (arrayListFamilyMember!!.size > 0) {
                            for (i in 0 until arrayListFamilyMember!!.size) {
                                if (state.equals(AppConstant.S_EDIT)) {
                                    if (arrayListFamilyMember!![i].ID == mFamilyMemberID) {
                                        arrayListFamilyMember!![i].IsSelected = true
                                    }
                                }
                            }
                        }
                        if (mode == 1) {
                            selectFamilyMemberDialog()
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

            override fun onFailure(call: Call<FamilyResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
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
                        BottomSheetFamilyMemberListAdapter(this@LeadEditActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mFamilyMemberID = arrItemsFinal1[pos].ID!!
                            mFamilyMember = arrItemsFinal1[pos].FirstName + " " + arrItemsFinal1[pos].LastName
                            edtFamilyMember.setText(mFamilyMember)
                            dialogSelectFamilyMember.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetFamilyMemberListAdapter(
                        this@LeadEditActivity,
                        arrayListFamilyMember!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mFamilyMemberID = arrayListFamilyMember!![pos].ID!!
                            mFamilyMember = arrayListFamilyMember!![pos].FirstName + " " + arrayListFamilyMember!![pos].LastName
                            edtFamilyMember.setText(mFamilyMember)
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
                call: Call<InquiryTypeResponse>,
                response: Response<InquiryTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListInquiryType = response.body()?.Data!!

                        if (arrayListInquiryType!!.size > 0) {
                            for (i in 0 until arrayListInquiryType!!.size) {
                                if (state.equals(AppConstant.S_EDIT)) {
                                    if (arrayListInquiryType!![i].ID == mInquiryTypeID) {
                                        arrayListInquiryType!![i].IsSelected = true
                                    }
                                }
                            }
                        }

                        if (mode == 1) {
                            selectInquiryTypeDialog()
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

                arrayListInquirySubType = ArrayList()
                mInquirySubType = ""
                mInquirySubTypeID = 0
                edtInquirySub.setText("")

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
                        BottomSheetInquiryTypeListAdapter(this@LeadEditActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquiryTypeID = arrItemsFinal1!![pos].ID!!
                            mInquiryType = arrItemsFinal1!![pos].InquiryType!!
                            edtInquiryType.setText(mInquiryType)

                            arrayListInquirySubType = ArrayList()
                            mInquirySubType = ""
                            mInquirySubTypeID = 0
                            edtInquirySub.setText("")

                            callManageInquirySubType(1, mInquiryTypeID)

                            dialogSelectInquiryType!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetInquiryTypeListAdapter(
                        this@LeadEditActivity,
                        arrayListInquiryType!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquiryTypeID = arrayListInquiryType!![pos].ID!!
                            mInquiryType = arrayListInquiryType!![pos].InquiryType!!
                            edtInquiryType.setText(mInquiryType)

                            arrayListInquirySubType = ArrayList()
                            mInquirySubType = ""
                            mInquirySubTypeID = 0
                            edtInquirySub.setText("")

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
                call: Call<InquirySubTypeResponse>,
                response: Response<InquirySubTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListInquirySubType = response.body()?.Data!!

                        if (arrayListInquirySubType!!.size > 0) {
                            for (i in 0 until arrayListInquirySubType!!.size) {
                                if (state.equals(AppConstant.S_EDIT)) {
                                    if (arrayListInquirySubType!![i].ID == mInquirySubTypeID) {
                                        arrayListInquirySubType!![i].IsSelected = true
                                    }
                                }
                            }
                        }

                        if (mode == 1) {
                            selectInquirySubTypeDialog()
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

            override fun onFailure(call: Call<InquirySubTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
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
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
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

                    val itemAdapter = BottomSheetInquirySubTypeListAdapter(
                        this@LeadEditActivity,
                        arrItemsFinal1
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mInquirySubTypeID = arrItemsFinal1!![pos].ID!!
                            mInquirySubType = arrItemsFinal1!![pos].InquirySubType!!
                            edtInquirySub.setText(mInquirySubType)
                            dialogSelectInquirySubType!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetInquirySubTypeListAdapter(
                        this@LeadEditActivity,
                        arrayListInquirySubType!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mInquirySubTypeID = arrayListInquirySubType!![pos].ID!!
                            mInquirySubType = arrayListInquirySubType!![pos].InquirySubType!!
                            edtInquirySub.setText(mInquirySubType)
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
                call: Call<LeadTypeResponse>,
                response: Response<LeadTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListleadtype = response.body()?.Data!!

                        if (arrayListleadtype!!.size > 0) {
                            for (i in 0 until arrayListleadtype!!.size) {
                                if (state.equals(AppConstant.S_EDIT)) {
                                    if (arrayListleadtype!![i].ID == mLeadtypeID) {
                                        arrayListleadtype!![i].IsSelected = true
                                    }
                                }
                            }
                        }
                        if (mode == 1) {
                            selectLeadTypeDialog()
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

            override fun onFailure(call: Call<LeadTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
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
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
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
                        BottomSheetLeadTypeListAdapter(this@LeadEditActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mLeadtype = arrItemsFinal1!![pos].LeadType!!
                            mLeadtypeID = arrItemsFinal1!![pos].ID!!
                            edtLeadType.setText(mLeadtype)
                            dialogSelectLeadType!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetLeadTypeListAdapter(
                        this@LeadEditActivity,
                        arrayListleadtype!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mLeadtype = arrayListleadtype!![pos].LeadType!!
                            mLeadtypeID = arrayListleadtype!![pos].ID!!
                            edtLeadType.setText(mLeadtype)
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
                call: Call<LeadStatusResponse>,
                response: Response<LeadStatusResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListleadstatus = response.body()?.Data!!
                        if (arrayListleadstatus!!.size > 0) {
                            for (i in 0 until arrayListleadstatus!!.size) {
                                if (state.equals(AppConstant.S_EDIT)) {
                                    if (arrayListleadstatus!![i].ID == mLeadstatusID) {
                                        arrayListleadstatus!![i].IsSelected = true
                                    }
                                }
                            }
                        }
                        if (mode == 1) {
                            selectLeadStatusDialog()
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

            override fun onFailure(call: Call<LeadStatusResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
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
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
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
                        BottomSheetLeadStatusListAdapter(this@LeadEditActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mLeadstatus = arrItemsFinal1!![pos].LeadStatus!!
                            mLeadstatusID = arrItemsFinal1!![pos].ID!!
                            edtLeadStatus.setText(mLeadstatus)
                            dialogSelectLeadStatus!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetLeadStatusListAdapter(
                        this@LeadEditActivity,
                        arrayListleadstatus!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mLeadstatus = arrayListleadstatus!![pos].LeadStatus!!
                            mLeadstatusID = arrayListleadstatus!![pos].ID!!
                            edtLeadStatus.setText(mLeadstatus)
                            dialogSelectLeadStatus!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectLeadStatus!!.show()
    }

    private fun callManageInquiryAllotmentTo(mode: Int, type: Int) {
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
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListInquiryAllotmentTo = response.body()?.Data!!
                        arrayListInquiryCoPersonAllotmentTo = response.body()?.Data!!
                        if (mode == 1 && type == 1) {
                            selectInquiryAllotmentToDialog()
                        } else if (mode == 1 && type == 2) {
                            selectInquiryCoPersonAllotmentToDialog()
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

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
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
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
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
                        if (model.FirstName!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                            model.LastName!!.toLowerCase().contains(strSearch.toLowerCase())
                        ) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetUsersListAdapter(this@LeadEditActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mInquiryAllotmentToID = arrItemsFinal1!![pos].ID!!
                            mInquiryAllotmentTo =
                                arrItemsFinal1!![pos].FirstName!! + " " + arrItemsFinal1!![pos].LastName!!
                            edtAllotmentTo.setText(mInquiryAllotmentTo)
                            edtAllotmentTo.setError(null)
                            dialogSelectInquiryAllotmentTo!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetUsersListAdapter(
                        this@LeadEditActivity,
                        arrayListInquiryAllotmentTo!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mInquiryAllotmentToID = arrayListInquiryAllotmentTo!![pos].ID!!
                            mInquiryAllotmentTo =
                                arrayListInquiryAllotmentTo!![pos].FirstName!! + " " + arrayListInquiryAllotmentTo!![pos].LastName!!
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

    private fun selectInquiryCoPersonAllotmentToDialog() {
        var dialogSelectInquiryCoPersonAllotmentTo = Dialog(this)
        dialogSelectInquiryCoPersonAllotmentTo.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectInquiryCoPersonAllotmentTo.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectInquiryCoPersonAllotmentTo.window!!.attributes)

        dialogSelectInquiryCoPersonAllotmentTo.window!!.attributes = lp
        dialogSelectInquiryCoPersonAllotmentTo.setCancelable(true)
        dialogSelectInquiryCoPersonAllotmentTo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectInquiryCoPersonAllotmentTo.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectInquiryCoPersonAllotmentTo.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectInquiryCoPersonAllotmentTo.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectInquiryCoPersonAllotmentTo.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectInquiryCoPersonAllotmentTo.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectInquiryCoPersonAllotmentTo.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectInquiryCoPersonAllotmentTo.dismiss()
        }

        txtid.text = "Select Co-Person Allotment"

        val itemAdapter = BottomSheetUsersListAdapter(this, arrayListInquiryCoPersonAllotmentTo!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                itemAdapter.updateItem(pos)
                mInquiryCoPersonAllotmentToID = arrayListInquiryCoPersonAllotmentTo!![pos].ID!!
                mInquiryCoPersonAllotmentTo = arrayListInquiryCoPersonAllotmentTo!![pos].FirstName!! + " " + arrayListInquiryCoPersonAllotmentTo!![pos].LastName!!
                edtCoPersonAllotmentTo.setText(mInquiryCoPersonAllotmentTo)
                edtCoPersonAllotmentTo.setError(null)
                dialogSelectInquiryCoPersonAllotmentTo!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListInquiryCoPersonAllotmentTo!!.size > 6) {
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
                    for (model in arrayListInquiryCoPersonAllotmentTo!!) {
                        if (model.FirstName!!.toLowerCase()
                                .contains(strSearch.toLowerCase()) || model.LastName!!.toLowerCase()
                                .contains(strSearch.toLowerCase())
                        ) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetUsersListAdapter(this@LeadEditActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquiryCoPersonAllotmentToID = arrItemsFinal1!![pos].ID!!
                            mInquiryCoPersonAllotmentTo = arrItemsFinal1!![pos].FirstName!! + " " + arrItemsFinal1!![pos].LastName!!
                            edtCoPersonAllotmentTo.setText(mInquiryCoPersonAllotmentTo)
                            edtCoPersonAllotmentTo.setError(null)

                            dialogSelectInquiryCoPersonAllotmentTo!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetUsersListAdapter(
                        this@LeadEditActivity, arrayListInquiryCoPersonAllotmentTo!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mInquiryCoPersonAllotmentToID = arrayListInquiryCoPersonAllotmentTo!![pos].ID!!
                            mInquiryCoPersonAllotmentTo = arrayListInquiryCoPersonAllotmentTo!![pos].FirstName!! + " " + arrayListInquiryCoPersonAllotmentTo!![pos].LastName!!
                            edtCoPersonAllotmentTo.setText(mInquiryCoPersonAllotmentTo)
                            edtCoPersonAllotmentTo.setError(null)

                            dialogSelectInquiryCoPersonAllotmentTo!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectInquiryCoPersonAllotmentTo!!.show()
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
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
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
                edtFrequency.setText(mFrequency)
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

                    val itemAdapter =
                        BottomSheetListAdapter(this@LeadEditActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mFrequency = arrItemsFinal1!![pos].Name!!
                            edtFrequency.setText(mFrequency)
                            dialogSelectFrequency!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetListAdapter(this@LeadEditActivity, arrayListFrequency!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mFrequency = arrayListFrequency!![pos].Name!!
                            edtFrequency.setText(mFrequency)
                            dialogSelectFrequency!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectFrequency!!.show()
    }

}