package com.app.insurancevala.activity.NBInquiry

import android.app.DatePickerDialog
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
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.activity.ActivityLog.ParticularInquiryActivityLog
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.activity.Lead.AttachmentsListActivity
import com.app.insurancevala.activity.Lead.CallsListActivity
import com.app.insurancevala.activity.Lead.MeetingsListActivity
import com.app.insurancevala.activity.Lead.NotesListActivity
import com.app.insurancevala.activity.Lead.RecordingsListActivity
import com.app.insurancevala.activity.Lead.TasksListActivity
import com.app.insurancevala.activity.LeadList.LeadEditActivity
import com.app.insurancevala.adapter.NoActivityInquiryLeadListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.InquiryTypeMultiSelectAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.InquiryTypeModel
import com.app.insurancevala.model.response.InquiryTypeResponse
import com.app.insurancevala.model.response.NBNoLeadInquiryModel
import com.app.insurancevala.model.response.NBNoLeadInquiryResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.android.synthetic.main.activity_no_inquiry_lead.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NoInquiryLeadActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: NoActivityInquiryLeadListAdapter
    var arrayListInquiry: ArrayList<NBNoLeadInquiryModel>? = ArrayList()
    var arrayListInquiryNew: ArrayList<NBNoLeadInquiryModel>? = ArrayList()

    var tabPosition = 1

    var arrayListInquiryType: ArrayList<InquiryTypeModel>? = ArrayList()
    var mInquiryType: String = ""
    var mInquiryTypeID: String = ""

    var sharedPreference: SharedPreference? = null

    // FromDate
    val CalenderFromDate = Calendar.getInstance()
    var FromDate: String = ""

    // ToDate
    val CalenderToDate = Calendar.getInstance()
    var ToDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_inquiry_lead)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {

    }

    override fun initializeView() {

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(this)
        }

        if (isOnline(this@NoInquiryLeadActivity)) {
            callManageInquiryType(0)
        } else {
            internetErrordialog(this@NoInquiryLeadActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        edtInquiryType.setOnClickListener(this)
        edtFromDate.setOnClickListener(this)
        edtToDate.setOnClickListener(this)
        llInquiry.setOnClickListener(this)
        llLead.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvInquiryList.layoutManager = manager

        arrayListInquiry = ArrayList()
        adapter = NoActivityInquiryLeadListAdapter( this@NoInquiryLeadActivity, arrayListInquiryNew!!, sharedPreference!!, tabPosition,this@NoInquiryLeadActivity)

        imgSearch.setOnClickListener {
            if(searchView.isSearchOpen) {
                searchView.closeSearch()
            } else {
                searchView.showSearch()
            }

        }

        searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                val arrItemsFinal1: ArrayList<NBNoLeadInquiryModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListInquiry!!) {
                        try {
                            if (model.InquiryType!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.InquirySubType!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.AllotmentName!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.ProposedAmount.toString()!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.Frequency!!.toLowerCase().contains(strSearch.toLowerCase())  ||
                                model.LeadType!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.LeadStatus!!.toLowerCase().contains(strSearch.toLowerCase())) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception){
                        }
                    }
                    arrayListInquiryNew = arrItemsFinal1
                    val itemAdapter = NoActivityInquiryLeadListAdapter( this@NoInquiryLeadActivity, arrayListInquiryNew!!, sharedPreference!!, tabPosition,this@NoInquiryLeadActivity)
                    RvInquiryList.adapter = itemAdapter
                } else {
                    arrayListInquiryNew = arrayListInquiry
                    val itemAdapter = NoActivityInquiryLeadListAdapter( this@NoInquiryLeadActivity, arrayListInquiryNew!!, sharedPreference!!, tabPosition,this@NoInquiryLeadActivity)
                    RvInquiryList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListInquiryNew = arrayListInquiry
                val itemAdapter = NoActivityInquiryLeadListAdapter( this@NoInquiryLeadActivity, arrayListInquiryNew!!, sharedPreference!!, tabPosition,this@NoInquiryLeadActivity)
                RvInquiryList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(this@NoInquiryLeadActivity, R.anim.searchview_close_anim)
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(this@NoInquiryLeadActivity, R.anim.searchview_open_anim)
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@NoInquiryLeadActivity,refreshLayout)
            searchView.closeSearch()
            callManageInquiryType(0)
            refreshLayout.isRefreshing = false
        }
    }

    override fun onClick(v: View?) {
        hideKeyboard(this@NoInquiryLeadActivity, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }

            R.id.llInquiry -> {
                if (tabPosition != 1) {
                    tabPosition = 1
                    changeTabColor(tabPosition)
                }
            }

            R.id.llLead -> {
                if (tabPosition != 2) {
                    tabPosition = 2
                    changeTabColor(tabPosition)
                }
            }

            R.id.edtInquiryType -> {
                preventTwoClick(v)
                if (arrayListInquiryType.isNullOrEmpty()) {
                    callManageInquiryType(1)
                } else {
                    multiSelectInquiryTypeDialog()
                }
            }

            R.id.edtFromDate -> {
                preventTwoClick(v)
                val dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        CalenderFromDate.set(Calendar.YEAR, year)
                        CalenderFromDate.set(Calendar.MONTH, monthOfYear)
                        CalenderFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        FromDate = SimpleDateFormat(AppConstant.yyyy_MM_dd_Dash, Locale.US).format(
                            CalenderFromDate.time
                        )

                        val selecteddate =
                            SimpleDateFormat(AppConstant.dd_MM_yyyy_HH_mm_ss, Locale.US).format(
                                CalenderFromDate.time
                            )
                        val mDate = convertDateStringToString(
                            selecteddate, AppConstant.dd_MM_yyyy_HH_mm_ss,
                            AppConstant.dd_LLL_yyyy
                        )
                        edtFromDate.setText(mDate)

                        if (edtFromDate.text.toString().isNotEmpty() && edtToDate.text.toString().isNotEmpty()) {
                            callManageNB()
                        }
                    },
                    CalenderFromDate.get(Calendar.YEAR),
                    CalenderFromDate.get(Calendar.MONTH),
                    CalenderFromDate.get(Calendar.DAY_OF_MONTH)
                )
                dpd.show()
            }

            R.id.edtToDate -> {
                preventTwoClick(v)
                val dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        CalenderToDate.set(Calendar.YEAR, year)
                        CalenderToDate.set(Calendar.MONTH, monthOfYear)
                        CalenderToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        ToDate = SimpleDateFormat(AppConstant.yyyy_MM_dd_Dash, Locale.US).format(
                            CalenderToDate.time
                        )

                        val selecteddate =
                            SimpleDateFormat(AppConstant.dd_MM_yyyy_HH_mm_ss, Locale.US).format(
                                CalenderToDate.time
                            )
                        val mDate = convertDateStringToString(
                            selecteddate, AppConstant.dd_MM_yyyy_HH_mm_ss,
                            AppConstant.dd_LLL_yyyy
                        )
                        edtToDate.setText(mDate)

                        if (edtFromDate.text.toString().isNotEmpty() && edtToDate.text.toString().isNotEmpty()) {
                            callManageNB()
                        }
                    },
                    CalenderToDate.get(Calendar.YEAR),
                    CalenderToDate.get(Calendar.MONTH),
                    CalenderToDate.get(Calendar.DAY_OF_MONTH)
                )
                dpd.show()
            }
        }
    }

    private fun changeTabColor(position: Int) {
        tabPosition = position
        when (position) {

            1 -> {
                llInquiry.background = ContextCompat.getDrawable(this, R.drawable.bg_upcoming)
                tvInquiry.setTextColor(ContextCompat.getColor(this, R.color.white))

                llLead.background = null
                tvLead.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))

                if (edtFromDate.text.toString().isNotEmpty() && edtToDate.text.toString().isNotEmpty()) {
                    callManageNB()
                }
            }

            2 -> {

                llInquiry. background = null
                tvInquiry.setTextColor(ContextCompat.getColor(this, R.color.colorBlack))

                llLead.background = ContextCompat.getDrawable(this, R.drawable.bg_upcoming)
                tvLead.setTextColor(ContextCompat.getColor(this, R.color.white))

                if (edtFromDate.text.toString().isNotEmpty() && edtToDate.text.toString().isNotEmpty()) {
                    callManageNB()
                }
            }
        }
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

                        if (mode == 1) {
                            multiSelectInquiryTypeDialog()
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

    private fun multiSelectInquiryTypeDialog() {

        var dialogMultiSelectInquiryType = Dialog(this)
        dialogMultiSelectInquiryType.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogMultiSelectInquiryType.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogMultiSelectInquiryType.window!!.attributes)

        dialogMultiSelectInquiryType.window!!.attributes = lp
        dialogMultiSelectInquiryType.setCancelable(true)
        dialogMultiSelectInquiryType.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogMultiSelectInquiryType.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogMultiSelectInquiryType.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogMultiSelectInquiryType.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogMultiSelectInquiryType.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogMultiSelectInquiryType.findViewById(R.id.txtid) as TextView
        val imgClear = dialogMultiSelectInquiryType.findViewById(R.id.imgClear) as ImageView

        txtid.text = "Select Inquiry Type"
        imgClear.setImageResource(R.drawable.done)

        if (arrayListInquiryType!!.size > 0) {
            for (i in arrayListInquiryType!!.indices) {
                arrayListInquiryType!![i].IsSelected = false
                val splitList = mInquiryTypeID.split(",")
                for (j in splitList.indices) {
                    if (arrayListInquiryType!![i].ID.toString() == splitList[j].trim()) {
                        arrayListInquiryType!![i].IsSelected = true
                    }
                }
            }
        }

        var itemAdapter = InquiryTypeMultiSelectAdapter(this, arrayListInquiryType!!)
        rvDialogCustomer.adapter = itemAdapter

        if (arrayListInquiryType!!.size > 6) {
            edtSearchCustomer.visible()
        } else {
            edtSearchCustomer.gone()
        }

        imgClear.setOnClickListener {
            val inquiryTypes = itemAdapter.getCheckBoxSelected()
            val inquiryTypesId = itemAdapter.getCheckBoxSelectedID()

            if (!inquiryTypes.isNullOrEmpty()) {

                mInquiryType = inquiryTypes.toString().replace("[", "").replace("]", "")
                mInquiryTypeID = inquiryTypesId.toString().replace("[", "").replace("]", "")
                edtInquiryType.setText(mInquiryType)
                dialogMultiSelectInquiryType.dismiss()

                if (edtFromDate.text.toString().isNotEmpty() && edtToDate.text.toString().isNotEmpty()) {
                    callManageNB()
                }
            } else {
                Snackbar.make(
                    layout, "Select At-least One Inquiry Type", Snackbar.LENGTH_LONG
                ).show()
            }
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
                        InquiryTypeMultiSelectAdapter(this@NoInquiryLeadActivity, arrItemsFinal1)
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = InquiryTypeMultiSelectAdapter(
                        this@NoInquiryLeadActivity, arrayListInquiryType!!
                    )
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogMultiSelectInquiryType.show()
    }

    private fun callManageNB() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("InquiryTypeID",mInquiryTypeID)
        jsonObject.put("InquirySearch",tabPosition)
        jsonObject.put("FromDate",FromDate)
        jsonObject.put("ToDate",ToDate)

        val call = ApiUtils.apiInterface.ManageNoInquiryLead(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<NBNoLeadInquiryResponse> {
            override fun onResponse(call: Call<NBNoLeadInquiryResponse>, response: Response<NBNoLeadInquiryResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListInquiry?.clear()
                        arrayListInquiryNew?.clear()
                        arrayListInquiry = response.body()?.Data!!
                        arrayListInquiryNew = arrayListInquiry

                        if(arrayListInquiryNew!!.size > 0) {
                            val myAdapter = NoActivityInquiryLeadListAdapter( this@NoInquiryLeadActivity, arrayListInquiryNew!!, sharedPreference!!, tabPosition,this@NoInquiryLeadActivity)
                            RvInquiryList.adapter = myAdapter
                            RvInquiryList.visible()
                            RLNoData.gone()
                        } else {
                            Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                            RvInquiryList.gone()
                            RLNoData.visible()
                        }
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        RvInquiryList.gone()
                        RLNoData.visible()
                    }
                }
            }
            override fun onFailure(call: Call<NBNoLeadInquiryResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }


    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when (type) {
            102 -> {
                //edit
                preventTwoClick(view)
                if (tabPosition == 1) {
                    val intent = Intent(this, InquiryEditActivity::class.java)
                    intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                    intent.putExtra("LeadID", arrayListInquiryNew!![position].LeadID)
                    intent.putExtra(
                        "NBInquiryTypeGUID",
                        arrayListInquiryNew!![position].NBInquiryTypeGUID
                    )
                    startActivityForResult(intent, AppConstant.INTENT_1001)
                } else {
                    val intent = Intent(this, LeadEditActivity::class.java)
                    intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                    intent.putExtra("LeadID", arrayListInquiryNew!![position].LeadID)
                    intent.putExtra(
                        "NBLeadsGUID",
                        arrayListInquiryNew!![position].NBLeadsGUID
                    )
                    startActivityForResult(intent, AppConstant.INTENT_1001)
                }
            }
            103 -> {
                //delete
                preventTwoClick(view)
            }
            104 -> {
                //Notes
                preventTwoClick(view)
                val intent = Intent(this, NotesListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",arrayListInquiryNew!![position].LeadID)
                if (tabPosition == 2) {
                    intent.putExtra("Lead", true)
                }
                startActivity(intent)
            }
            105 -> {
                //Tasks
                preventTwoClick(view)
                val intent = Intent(this, TasksListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",arrayListInquiryNew!![position].LeadID)
                if (tabPosition == 2) {
                    intent.putExtra("Lead", true)
                }
                startActivity(intent)
            }
            106 -> {
                //Calls
                preventTwoClick(view)
                val intent = Intent(this, CallsListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",arrayListInquiryNew!![position].LeadID)
                if (tabPosition == 2) {
                    intent.putExtra("Lead", true)
                }
                startActivity(intent)
            }
            107 -> {
                //Meetings
                preventTwoClick(view)
                val intent = Intent(this, MeetingsListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",arrayListInquiryNew!![position].LeadID)
                if (tabPosition == 2) {
                    intent.putExtra("Lead", true)
                }
                startActivity(intent)
            }
            108 -> {
                //Attachments
                preventTwoClick(view)
                val intent = Intent(this, AttachmentsListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",arrayListInquiryNew!![position].LeadID)
                if (tabPosition == 2) {
                    intent.putExtra("Lead", true)
                }
                startActivity(intent)
            }
            109 -> {
                //Closed_Tasks
                preventTwoClick(view)
                val intent = Intent(this, TasksListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",arrayListInquiryNew!![position].LeadID)
                intent.putExtra("ClosedTask","Completed")
                if (tabPosition == 2) {
                    intent.putExtra("Lead", true)
                }
                startActivity(intent)
            }
            110 -> {
                //Closed_Calls
                preventTwoClick(view)
                val intent = Intent(this, CallsListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",arrayListInquiryNew!![position].LeadID)
                intent.putExtra("ClosedCall","Completed")
                if (tabPosition == 2) {
                    intent.putExtra("Lead", true)
                }
                startActivity(intent)
            }
            111 -> {
                //Closed_Meetings
                preventTwoClick(view)
                val intent = Intent(this, MeetingsListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",arrayListInquiryNew!![position].LeadID)
                intent.putExtra("ClosedMeeting",4)
                if (tabPosition == 2) {
                    intent.putExtra("Lead", true)
                }
                startActivity(intent)
            }
            112 -> {
                //Attachments
                preventTwoClick(view)
                val intent = Intent(this, RecordingsListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",arrayListInquiryNew!![position].LeadID)
                if (tabPosition == 2) {
                    intent.putExtra("Lead", true)
                }
                startActivity(intent)
            }
            113 -> {
                //Activity Log
                preventTwoClick(view)
                val intent = Intent(this, ParticularInquiryActivityLog::class.java)
                if (tabPosition == 1) {
                    intent.putExtra("NBInquiryTypeGUID",arrayListInquiryNew!![position].NBInquiryTypeGUID)
                } else {
                    intent.putExtra("NBLeadsGUID",arrayListInquiryNew!![position].NBLeadsGUID)
                    intent.putExtra("Lead", true)
                }
                startActivity(intent)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.INTENT_1001) {
            if (isOnline(this)) {
                callManageNB()
            } else {
                internetErrordialog(this)
            }
        }
    }
}