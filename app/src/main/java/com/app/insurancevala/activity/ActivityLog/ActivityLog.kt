package com.app.insurancevala.activity.ActivityLog

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.AttachmentListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetUsersListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.pojo.DocumentsModel
import com.app.insurancevala.model.pojo.DocumentsResponse
import com.app.insurancevala.model.response.ActivityLogModel
import com.app.insurancevala.model.response.ActivityLogResponse
import com.app.insurancevala.model.response.UserModel
import com.app.insurancevala.model.response.UserResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.android.synthetic.main.activity_log.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ActivityLog : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: ParticularInquiryAdapter
    var arrayListActivity: ArrayList<ActivityLogModel>? = ArrayList()
    var arrayListActivityNew: ArrayList<ActivityLogModel>? = ArrayList()

    var tabPosition = 0

    // FromDate
    val CalenderFromDate = Calendar.getInstance()
    var FromDate: String = ""

    // ToDate
    val CalenderToDate = Calendar.getInstance()
    var ToDate: String = ""

    var arrayListUser: ArrayList<UserModel>? = ArrayList()
    var mUser: String = ""
    var mUserID: Int = 0

    var arrayListAttachment: ArrayList<DocumentsModel>? = ArrayList()
    lateinit var adapterAttachment: AttachmentListAdapter

    var sharedPreference: SharedPreference? = null

    var NBActivityType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        NBActivityType = intent.getStringExtra("NBActivityType").toString()
        if (NBActivityType == "Inquiry") {
            txtHearderText.text = "Inquiry Activity Log"
        } else {
            txtHearderText.text = "Lead Activity Log"
        }
    }

    override fun initializeView() {
        if (isOnline(this@ActivityLog)) {
            callManageUser(0)
        } else {
            internetErrordialog(this@ActivityLog)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        llUpcoming.setOnClickListener(this)
        llPast.setOnClickListener(this)
        imgBack.setOnClickListener(this)
        edtFromDate.setOnClickListener(this)
        edtToDate.setOnClickListener(this)
        edtUser.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvInquiryActivityLog.layoutManager = manager

        arrayListActivity = ArrayList()
        adapter = ParticularInquiryAdapter(arrayListActivityNew!!, this@ActivityLog, tabPosition)

        imgAddInquiryType.gone()

        imgSearch.setOnClickListener {
            if (searchView.isSearchOpen) {
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
                val arrItemsFinal1: ArrayList<ActivityLogModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListActivity!!) {
                        try {
                            if (model.ActivityTitle!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.ActivityDescription!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.CreatedOn!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.ActivityDate!!.toLowerCase().contains(strSearch.toLowerCase())
                            ) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                        }
                    }
                    arrayListActivityNew = arrItemsFinal1
                    val itemAdapter =
                        ParticularInquiryAdapter(arrayListActivityNew!!, this@ActivityLog, tabPosition)
                    RvInquiryActivityLog.adapter = itemAdapter
                } else {
                    arrayListActivityNew = arrayListActivity
                    val itemAdapter =
                        ParticularInquiryAdapter(arrayListActivityNew!!, this@ActivityLog, tabPosition)
                    RvInquiryActivityLog.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListActivityNew = arrayListActivity
                val itemAdapter = ParticularInquiryAdapter(arrayListActivityNew!!, this@ActivityLog, tabPosition)
                RvInquiryActivityLog.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(this@ActivityLog, R.anim.searchview_close_anim)
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(this@ActivityLog, R.anim.searchview_open_anim)
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@ActivityLog, refreshLayout)
            searchView.closeSearch()
            callActivityLog()
            refreshLayout.isRefreshing = false
        }
    }

    override fun onClick(v: View?) {
        hideKeyboard(this@ActivityLog, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }

            R.id.llUpcoming -> {
                if (tabPosition != 0) {
                    tabPosition = 0
                    changeTabColor(tabPosition)
                }
            }

            R.id.llPast -> {
                if (tabPosition != 1) {
                    tabPosition = 1
                    changeTabColor(tabPosition)
                }
            }

            R.id.edtUser -> {
                preventTwoClick(v)
                if (arrayListUser.isNullOrEmpty()) {
                    callManageUser(1)
                } else {
                    selectUserDialog()
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
                            selecteddate, AppConstant.dd_MM_yyyy_HH_mm_ss, AppConstant.dd_LLL_yyyy
                        )
                        edtFromDate.setText(mDate)
                        if (!ToDate.equals("")) {
                            callActivityLog()
                        }
                    },
                    CalenderFromDate.get(Calendar.YEAR),
                    CalenderFromDate.get(Calendar.MONTH),
                    CalenderFromDate.get(Calendar.DAY_OF_MONTH)
                )
//                dpd.datePicker.minDate = System.currentTimeMillis() - 1000
                dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
                dpd.show()
            }

            R.id.edtToDate -> {
                preventTwoClick(v)
                if (edtFromDate.text.isNotEmpty()) {
                    val dpd = DatePickerDialog(
                        this,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            CalenderToDate.set(Calendar.YEAR, year)
                            CalenderToDate.set(Calendar.MONTH, monthOfYear)
                            CalenderToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                            ToDate =
                                SimpleDateFormat(AppConstant.yyyy_MM_dd_Dash, Locale.US).format(
                                    CalenderToDate.time
                                )

                            val selecteddate =
                                SimpleDateFormat(AppConstant.dd_MM_yyyy_HH_mm_ss, Locale.US).format(
                                    CalenderToDate.time
                                )
                            val mDate = convertDateStringToString(
                                selecteddate,
                                AppConstant.dd_MM_yyyy_HH_mm_ss,
                                AppConstant.dd_LLL_yyyy
                            )
                            edtToDate.setText(mDate)
                            if (!FromDate.equals("")) {
                                callActivityLog()
                            }
                        },
                        CalenderToDate.get(Calendar.YEAR),
                        CalenderToDate.get(Calendar.MONTH),
                        CalenderToDate.get(Calendar.DAY_OF_MONTH)
                    )
//                dpd.datePicker.minDate = System.currentTimeMillis() - 1000
                    dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
                    dpd.show()
                } else {
                    Snackbar.make(
                        layout, "Select From Date", Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun changeTabColor(position: Int) {
        tabPosition = position
        when (position) {

            0 -> {
                llUpcoming.background = ContextCompat.getDrawable(this, R.color.colorPrimary)
                tvUpcoming.setTextColor(ContextCompat.getColor(this, R.color.white))

                llPast.background = ContextCompat.getDrawable(this, R.color.white)
                tvPast.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            }

            1 -> {

                llUpcoming.background = ContextCompat.getDrawable(this, R.color.white)
                tvUpcoming.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

                llPast.background = ContextCompat.getDrawable(this, R.color.colorPrimary)
                tvPast.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
        }
        callActivityLog()
    }

    private fun callManageUser(mode: Int) {
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
                        arrayListUser = response.body()?.Data!!
                        if (mode == 1) {
                            selectUserDialog()
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

    private fun selectUserDialog() {
        var dialogSelectUser = Dialog(this)
        dialogSelectUser.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectUser.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectUser.window!!.attributes)

        dialogSelectUser.window!!.attributes = lp
        dialogSelectUser.setCancelable(true)
        dialogSelectUser.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectUser.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectUser.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectUser.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectUser.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectUser.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectUser.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectUser.dismiss()
        }

        txtid.text = "Select User"

        val itemAdapter = BottomSheetUsersListAdapter(this, arrayListUser!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                itemAdapter.updateItem(pos)
                mUserID = arrayListUser!![pos].ID!!
                mUser = arrayListUser!![pos].FirstName!! + " " + arrayListUser!![pos].LastName!!
                edtUser.setText(mUser)
                dialogSelectUser!!.dismiss()
                if (FromDate != "" && ToDate != "") {
                    callActivityLog()
                }
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListUser!!.size > 6) {
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
                    for (model in arrayListUser!!) {
                        if (model.FirstName!!.toLowerCase()
                                .contains(strSearch.toLowerCase()) || model.LastName!!.toLowerCase()
                                .contains(strSearch.toLowerCase())
                        ) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetUsersListAdapter(this@ActivityLog, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mUserID = arrItemsFinal1!![pos].ID!!
                            mUser =
                                arrItemsFinal1!![pos].FirstName!! + " " + arrItemsFinal1!![pos].LastName!!
                            edtUser.setText(mUser)
                            dialogSelectUser!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetUsersListAdapter(
                        this@ActivityLog, arrayListUser!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mUserID = arrayListUser!![pos].ID!!
                            mUser =
                                arrayListUser!![pos].FirstName!! + " " + arrayListUser!![pos].LastName!!
                            edtUser.setText(mUser)
                            dialogSelectUser!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
                if (FromDate != "" && ToDate != "") {
                    callActivityLog()
                }
            }
        })

        dialogSelectUser!!.show()
    }

    private fun callActivityLog() {

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(applicationContext)
        }

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("PastActivityFilters", tabPosition)
        jsonObject.put("NBInquiryTypeGUID", null)
        jsonObject.put("NBActivityType", NBActivityType)
        jsonObject.put("CreatedBy", mUserID)
        jsonObject.put("FromDate", FromDate)
        jsonObject.put("ToDate", ToDate)

        val call =
            ApiUtils.apiInterface.NBLeadActivityLogFindAll(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<ActivityLogResponse> {
            override fun onResponse(
                call: Call<ActivityLogResponse>, response: Response<ActivityLogResponse>
            ) {
                hideProgress()
                llType.visible()
                V1.visible()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListActivity?.clear()
                        arrayListActivityNew?.clear()
                        arrayListActivity = response.body()?.Data!!
                        arrayListActivityNew = arrayListActivity

                        if (arrayListActivityNew!!.size > 0) {
                            var myAdapter =
                                ParticularInquiryAdapter(arrayListActivityNew!!, this@ActivityLog, tabPosition)
                            RvInquiryActivityLog.adapter = myAdapter
                            shimmer.stopShimmer()
                            shimmer.gone()
                            FL.visible()
                            RLNoData.gone()
                        } else {
                            Snackbar.make(
                                layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                            ).show()
                            shimmer.stopShimmer()
                            shimmer.gone()
                            FL.gone()
                            RLNoData.visible()
                        }
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        shimmer.stopShimmer()
                        shimmer.gone()
                        FL.gone()
                        RLNoData.visible()
                    }
                }
            }

            override fun onFailure(call: Call<ActivityLogResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun callAttachmentList(LogGUID: String) {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("LogGUID", LogGUID)

        val call =
            ApiUtils.apiInterface.ManageAttachmentsLogFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<DocumentsResponse> {
            override fun onResponse(
                call: Call<DocumentsResponse>, response: Response<DocumentsResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListAttachment?.clear()
                        arrayListAttachment = response.body()?.Data!!

                        if (arrayListAttachment!!.size > 0) {
                            selectAttachmentDialog()
                        } else {
                            Snackbar.make(
                                layout, getString(R.string.no_data_found), Snackbar.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Snackbar.make(
                        layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<DocumentsResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }

    private fun selectAttachmentDialog() {
        var dialogSelectAttachment = Dialog(this)
        dialogSelectAttachment.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectAttachment.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectAttachment.window!!.attributes)

        dialogSelectAttachment.window!!.attributes = lp
        dialogSelectAttachment.setCancelable(true)
        dialogSelectAttachment.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectAttachment.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectAttachment.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectAttachment.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectAttachment.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectAttachment.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectAttachment.findViewById(R.id.imgClear) as ImageView

        imgClear.gone()
        edtSearchCustomer.gone()

        txtid.text = "Attachment List"

        rvDialogCustomer.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvDialogCustomer.isNestedScrollingEnabled = false

        adapterAttachment = AttachmentListAdapter(
            this,
            arrayListAttachment!!
        )
        rvDialogCustomer.adapter = adapterAttachment

        dialogSelectAttachment!!.show()
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when (type) {
            101 -> {
                preventTwoClick(view)
                callAttachmentList(arrayListActivity!![position].LogGUID!!)
            }

            102 -> {
                preventTwoClick(view)
                callAttachmentList(arrayListActivity!![position].LogGUID!!)
            }

            103 -> {
                preventTwoClick(view)
                callAttachmentList(arrayListActivity!![position].LogGUID!!)
            }

            104 -> {
                preventTwoClick(view)
                if (!arrayListActivity!![position].ActivityDescription.isNullOrEmpty()){
                    if(arrayListActivity!![position].ActivityDescription!!.contains(".pdf")) {
                        var format = "https://docs.google.com/gview?embedded=true&url=%s"
                        val fullPath: String = java.lang.String.format(Locale.ENGLISH, format, arrayListActivity!![position].ActivityDescription!!)
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fullPath))
                        startActivity(browserIntent)
                    } else {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(arrayListActivity!![position].ActivityDescription!!))
                        startActivity(browserIntent)
                    }
                }
            }

            105 -> {
                preventTwoClick(view)
                if (!arrayListActivity!![position].ActivityDescription.isNullOrEmpty()){
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(arrayListActivity!![position].ActivityDescription!!))
                    startActivity(browserIntent)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.INTENT_1001) {
            if (isOnline(this)) {
                callActivityLog()
            } else {
                internetErrordialog(this)
            }
        }
    }
}