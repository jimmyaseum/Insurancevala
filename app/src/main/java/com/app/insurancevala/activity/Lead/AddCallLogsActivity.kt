package com.app.insurancevala.activity.Lead

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
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
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetCallPurposeListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetCallResultListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetCallTypeListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetUserTypeListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.*
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_call_logs.*
import kotlinx.android.synthetic.main.activity_add_call_logs.layout
import kotlinx.android.synthetic.main.activity_add_call_logs.cbIsFollowup
import kotlinx.android.synthetic.main.activity_add_call_logs.imgBack
import kotlinx.android.synthetic.main.activity_add_call_logs.txtSave
import kotlinx.android.synthetic.main.activity_add_user.edtUserType

import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddCallLogsActivity : BaseActivity(), View.OnClickListener {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var ID: Int? = null
    var LeadID: Int? = null
    var CallGUID: String? = null

    // get from intent data
    var calltype : String = ""

    var arrayListcalltype : ArrayList<CallTypeModel>? = ArrayList()
    var mCalltype : String = ""
    var mCalltypeID : Int = 0

    var arrayListcallpurpose : ArrayList<CallPurposeModel>? = ArrayList()
    var mCallpurpose : String = ""
    var mCallpurposeID : Int = 0

    var arrayListcallresult  = ArrayList<CallResultModel>()
    var mCallresult : String = ""
    var mCallresultID : Int = 0

    //logdate
    val CalenderLogCallDate = Calendar.getInstance()
    var LogCallDate: String = ""

    //followup date
    val CalenderFollowUpCallDate = Calendar.getInstance()
    var FollowUpCallDate: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_call_logs)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()

    }

    override fun initializeView() {
        setMasterData()
        SetInitListner()
    }

    private fun setMasterData() {
        if (isOnline(this)) {
            callManageCallType(0)
            callManageCallPurpose(0)
            callManageCallResult(0)
        } else {
            internetErrordialog(this@AddCallLogsActivity)
        }
    }

    private fun getIntentData() {

        state = intent.getStringExtra(AppConstant.STATE)
        LeadID = intent.getIntExtra("LeadID",0)
        ID = intent.getIntExtra("ID",0)

        calltype = intent.getStringExtra("CALLTYPE").toString()
        if(calltype.equals("SCHEDULE")) {
            edtLOutgoingCallStatus.setText("Scheduled")
            LLLCallResult.gone()
            cbIsFollowup.gone()
            LLFollowupDate.gone()
            LLFollowupNotes.gone()
        } else if(calltype.equals("LOGCALL")) {
            edtLOutgoingCallStatus.setText("Completed")
        }

//        setDefaultDate()
    }

    private fun setDefaultDate() {

        val mDate = convertDateStringToString(getcurrentdate() , AppConstant.dd_MM_yyyy_HH_mm_ss,AppConstant.dd_LLL_yyyy)

        edtLCallDate.setText(mDate)
        edtFollowupDate.setText(mDate)

        LogCallDate = convertDateStringToString(getcurrentdate() , AppConstant.dd_MM_yyyy_HH_mm_ss,AppConstant.yyyy_MM_dd_Dash).toString()
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        txtSave.setOnClickListener(this)

        edtLCallType.setOnClickListener(this)
        edtLCallDate.setOnClickListener(this)

        edtLCallPurpose.setOnClickListener(this)
        edtLCallResult.setOnClickListener(this)
        edtFollowupDate.setOnClickListener(this)
        cbIsFollowup.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }
            R.id.txtSave -> {
                preventTwoClick(v)
                validation()
            }
            // log call click
            R.id.edtLCallType -> {
                preventTwoClick(v)
                if (arrayListcalltype.isNullOrEmpty()) {
                    callManageCallType(1)
                } else {
                    selectCallTypeDialog()
                }
            }
            R.id.edtLCallDate -> {
                preventTwoClick(v)
                val dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        CalenderLogCallDate.set(Calendar.YEAR, year)
                        CalenderLogCallDate.set(Calendar.MONTH, monthOfYear)
                        CalenderLogCallDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        LogCallDate = SimpleDateFormat(AppConstant.yyyy_MM_dd_Dash, Locale.US).format(CalenderLogCallDate.time)

                        val selecteddate = SimpleDateFormat(AppConstant.dd_MM_yyyy_HH_mm_ss, Locale.US).format(CalenderLogCallDate.time)
                        val mDate = convertDateStringToString(selecteddate , AppConstant.dd_MM_yyyy_HH_mm_ss,AppConstant.dd_LLL_yyyy)
                        edtLCallDate.setText(mDate)
                    },
                    CalenderLogCallDate.get(Calendar.YEAR),
                    CalenderLogCallDate.get(Calendar.MONTH),
                    CalenderLogCallDate.get(Calendar.DAY_OF_MONTH)
                )
                dpd.datePicker.minDate = System.currentTimeMillis()
//                dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
                dpd.show()
            }
            R.id.edtLCallPurpose -> {
                preventTwoClick(v)
                if (arrayListcallpurpose.isNullOrEmpty()) {
                    callManageCallPurpose(1)
                } else {
                    selectCallPurposeDialog()
                }
            }
            R.id.edtLCallResult -> {
                preventTwoClick(v)
                if (arrayListcallresult.isNullOrEmpty()) {
                    callManageCallResult(1)
                } else {
                    selectCallResultDialog()
                }

            }
            R.id.cbIsFollowup -> {
                if (cbIsFollowup.isChecked) {
                    LLFollowupDate.visible()
                    LLFollowupNotes.visible()
                } else {
                    LLFollowupDate.gone()
                    LLFollowupNotes.gone()
                }
            }
            R.id.edtFollowupDate -> {
                preventTwoClick(v)

                val dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        CalenderFollowUpCallDate.set(Calendar.YEAR, year)
                        CalenderFollowUpCallDate.set(Calendar.MONTH, monthOfYear)
                        CalenderFollowUpCallDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        FollowUpCallDate = SimpleDateFormat(AppConstant.yyyy_MM_dd_Dash, Locale.US).format(CalenderFollowUpCallDate.time)

                        val selecteddate = SimpleDateFormat(AppConstant.dd_MM_yyyy_HH_mm_ss, Locale.US).format(CalenderFollowUpCallDate.time)
                        val mDate = convertDateStringToString(selecteddate , AppConstant.dd_MM_yyyy_HH_mm_ss,AppConstant.dd_LLL_yyyy)
                        edtFollowupDate.setText(mDate)
                    },
                    CalenderFollowUpCallDate.get(Calendar.YEAR),
                    CalenderFollowUpCallDate.get(Calendar.MONTH),
                    CalenderFollowUpCallDate.get(Calendar.DAY_OF_MONTH)
                )
                dpd.datePicker.minDate = System.currentTimeMillis() - 1000
//                dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
                dpd.show()
            }

        }
    }

    private fun validation() {
        when (isValidate()) {
            true -> {
                if (isOnline(this)) {
                    ManageCreateCallsAPI()
                } else {
                    internetErrordialog(this@AddCallLogsActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {
        var isValidate = true
        if(calltype.equals("SCHEDULE")) {

            if (edtLCallType.text.isEmpty()) {
                edtLCallType.setError("Select Call Type",errortint(this))
                isValidate = false
            }
            if (edtLCallDate.text.isEmpty()) {
                edtLCallDate.setError("Select Call Date",errortint(this))
                isValidate = false
            }
            if (edtLCallPurpose.text.isEmpty()) {
                edtLCallPurpose.setError("Select Call Purpose",errortint(this))
                isValidate = false
            }
            if (edtLCallSubject.text.isEmpty()) {
                edtLCallSubject.setError("Select Call Subject",errortint(this))
                isValidate = false
            }
        }
        else if(calltype.equals("LOGCALL")) {
            if (edtLCallType.text.isEmpty()) {
                edtLCallType.setError("Select Call Type",errortint(this))
                isValidate = false
            }
            if (edtLCallDate.text.isEmpty()) {
                edtLCallDate.setError("Select Call Date",errortint(this))
                isValidate = false
            }
            if (edtLCallPurpose.text.isEmpty()) {
                edtLCallPurpose.setError("Select Call Purpose",errortint(this))
                isValidate = false
            }
            if (edtLCallResult.text.isEmpty()) {
                edtLCallResult.setError("Select Call Result",errortint(this))
                isValidate = false
            }
            if (edtLCallSubject.text.isEmpty()) {
                edtLCallSubject.setError("Select Call Subject",errortint(this))
                isValidate = false
            }
        }
        return isValidate
    }

    private fun ManageCreateCallsAPI() {

        showProgress()

        val call: Call<CommonResponse>

        var isreminder = false

        val jsonObject = JSONObject()
        jsonObject.put("NBInquiryTypeID", ID)
        jsonObject.put("LeadID", LeadID)
        jsonObject.put("CallTypeID", mCalltypeID)
        jsonObject.put("CallPurposeID", mCallpurposeID)
        jsonObject.put("IsActive", true)
        jsonObject.put("CallStatus", edtLOutgoingCallStatus.text.toString().trim())
        jsonObject.put("Subject", edtLCallSubject.text.toString().trim())
        jsonObject.put("Agenda", edtLCallAgenda.text.toString().trim())
        jsonObject.put("Description", edtLCallDescription.text.toString().trim())
        jsonObject.put("CallDate", LogCallDate)

        if(calltype.equals("LOGCALL")) {
            jsonObject.put("CallResultID", mCallresultID)
            if(cbIsFollowup.isChecked) {
                isreminder = true
                jsonObject.put("FollowupDate", FollowUpCallDate)
                jsonObject.put("FollowupNotes", edtFollowupNotes.text.toString().trim())
            } else {
                isreminder = false
            }
            jsonObject.put("IsFollowup", isreminder)
        } else {
            jsonObject.put("CallResultID", mCallresultID)
        }

        if(state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("CallGUID", CallGUID)
        }

        if(state.equals(AppConstant.S_ADD)) {
            call = ApiUtils.apiInterface.ManageCallsInsert(getRequestJSONBody(jsonObject.toString()))
        } else {
            call = ApiUtils.apiInterface.ManageCallsUpdate(getRequestJSONBody(jsonObject.toString()))
        }

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 201) {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
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

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        })
    }

    private fun callManageCallType(mode: Int) {
        if(mode == 1) {
            showProgress()
        }
        var jsonObject = JSONObject()
        jsonObject.put("OperationType",AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageCallType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CallTypeResponse> {
            override fun onResponse(call: Call<CallTypeResponse>, response: Response<CallTypeResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListcalltype = response.body()?.Data!!

                        if (mode == 1) {
                            selectCallTypeDialog()
                        }
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<CallTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }
    private fun selectCallTypeDialog() {
        var dialogSelectCallType = Dialog(this)
        dialogSelectCallType.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectCallType.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectCallType.window!!.attributes)

        dialogSelectCallType.window!!.attributes = lp
        dialogSelectCallType.setCancelable(true)
        dialogSelectCallType.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectCallType.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectCallType.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectCallType.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectCallType.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectCallType.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectCallType.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectCallType.dismiss()
        }

        txtid.text = "Select Call Type"

        val itemAdapter = BottomSheetCallTypeListAdapter(this, arrayListcalltype!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mCalltype = arrayListcalltype!![pos].CallType!!
                mCalltypeID = arrayListcalltype!![pos].ID!!
                edtLCallType.setText(mCalltype)
                dialogSelectCallType!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if(arrayListcalltype!!.size > 6) {
            edtSearchCustomer.visible()
        }
        else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<CallTypeModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListcalltype!!) {
                        if (model.CallType!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetCallTypeListAdapter(this@AddCallLogsActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mCalltype = arrItemsFinal1!![pos].CallType!!
                            mCalltypeID = arrItemsFinal1!![pos].ID!!
                            edtLCallType.setText(mCalltype)
                            dialogSelectCallType!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetCallTypeListAdapter(this@AddCallLogsActivity, arrayListcalltype!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mCalltype = arrayListcalltype!![pos].CallType!!
                            mCalltypeID = arrayListcalltype!![pos].ID!!
                            edtLCallType.setText(mCalltype)
                            dialogSelectCallType!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectCallType!!.show()
    }

    private fun callManageCallPurpose(mode: Int) {
        if(mode == 1) {
            showProgress()
        }
        var jsonObject = JSONObject()
        jsonObject.put("OperationType",AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageCallPurpose(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CallPurposeResponse> {
            override fun onResponse(call: Call<CallPurposeResponse>, response: Response<CallPurposeResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListcallpurpose = response.body()?.Data!!
//                        arrayListcallpurpose!!.sortBy { it.ID }
                        if (mode == 1) {
                            selectCallPurposeDialog()
                        }
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<CallPurposeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })

    }
    private fun selectCallPurposeDialog() {
        var dialogSelectCallPurpose = Dialog(this)
        dialogSelectCallPurpose.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectCallPurpose.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectCallPurpose.window!!.attributes)

        dialogSelectCallPurpose.window!!.attributes = lp
        dialogSelectCallPurpose.setCancelable(true)
        dialogSelectCallPurpose.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectCallPurpose.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectCallPurpose.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectCallPurpose.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectCallPurpose.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectCallPurpose.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectCallPurpose.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectCallPurpose.dismiss()
        }

        txtid.text = "Select Call Purpose"

        val itemAdapter = BottomSheetCallPurposeListAdapter(this, arrayListcallpurpose!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mCallpurpose = arrayListcallpurpose!![pos].CallPurpose!!
                mCallpurposeID = arrayListcallpurpose!![pos].ID!!
                edtLCallPurpose.setText(mCallpurpose)
                dialogSelectCallPurpose!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if(arrayListcallpurpose!!.size > 6) {
            edtSearchCustomer.visible()
        }
        else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<CallPurposeModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListcallpurpose!!) {
                        if (model.CallPurpose!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetCallPurposeListAdapter(this@AddCallLogsActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mCallpurpose = arrItemsFinal1!![pos].CallPurpose!!
                            mCallpurposeID = arrItemsFinal1!![pos].ID!!
                            edtLCallPurpose.setText(mCallpurpose)
                            dialogSelectCallPurpose!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetCallPurposeListAdapter(this@AddCallLogsActivity, arrayListcallpurpose!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mCallpurpose = arrayListcallpurpose!![pos].CallPurpose!!
                            mCallpurposeID = arrayListcallpurpose!![pos].ID!!
                            edtLCallPurpose.setText(mCallpurpose)
                            dialogSelectCallPurpose!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectCallPurpose!!.show()
    }

    private fun callManageCallResult(mode: Int) {
        if(mode == 1) {
            showProgress()
        }
        var jsonObject = JSONObject()
        jsonObject.put("OperationType",AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageCallResult(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CallResultResponse> {
            override fun onResponse(call: Call<CallResultResponse>, response: Response<CallResultResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListcallresult = response.body()?.Data!!
//                        arrayListcallresult!!.sortBy { it.ID }
                        if (mode == 1) {
                            selectCallResultDialog()
                        }
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<CallResultResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })

    }
    private fun selectCallResultDialog() {
        var dialogSelectCallResult = Dialog(this)
        dialogSelectCallResult.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectCallResult.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectCallResult.window!!.attributes)

        dialogSelectCallResult.window!!.attributes = lp
        dialogSelectCallResult.setCancelable(true)
        dialogSelectCallResult.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectCallResult.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectCallResult.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectCallResult.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectCallResult.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectCallResult.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectCallResult.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectCallResult.dismiss()
        }

        txtid.text = "Select Call Result"

        val itemAdapter = BottomSheetCallResultListAdapter(this, arrayListcallresult!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {
                itemAdapter.updateItem(pos)
                mCallresultID = arrayListcallresult!![pos].ID!!
                mCallresult = arrayListcallresult!![pos].CallResult!!
                edtLCallResult.setText(mCallresult)
                dialogSelectCallResult!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if(arrayListcallresult!!.size > 6) {
            edtSearchCustomer.visible()
        }
        else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<CallResultModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListcallresult!!) {
                        if (model.CallResult!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetCallResultListAdapter(this@AddCallLogsActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mCallresultID = arrItemsFinal1!![pos].ID!!
                            mCallresult = arrItemsFinal1!![pos].CallResult!!
                            edtLCallResult.setText(mCallresult)
                            dialogSelectCallResult!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetCallResultListAdapter(this@AddCallLogsActivity, arrayListcallresult!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mCallresultID = arrayListcallresult!![pos].ID!!
                            mCallresult = arrayListcallresult!![pos].CallResult!!
                            edtLCallResult.setText(mCallresult)
                            dialogSelectCallResult!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectCallResult!!.show()
    }

}