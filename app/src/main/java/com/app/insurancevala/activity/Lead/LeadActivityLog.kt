package com.app.insurancevala.activity.Lead

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
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
import com.app.insurancevala.activity.ActivityLog.ParticularInquiryAdapter
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.AttachmentListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.pojo.DocumentsModel
import com.app.insurancevala.model.pojo.DocumentsResponse
import com.app.insurancevala.model.response.ActivityLogModel
import com.app.insurancevala.model.response.ActivityLogResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.android.synthetic.main.activity_lead_log.*

class LeadActivityLog : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: ParticularInquiryAdapter
    var arrayListActivity: ArrayList<ActivityLogModel>? = ArrayList()
    var arrayListActivityNew: ArrayList<ActivityLogModel>? = ArrayList()

    var Status: String? = "Inquiry"

    var arrayListAttachment: ArrayList<DocumentsModel>? = ArrayList()
    lateinit var adapterAttachment: AttachmentListAdapter

    var tabPosition = 0

    var LeadID: Int? = null
    var LeadGUID: String? = null

    var sharedPreference: SharedPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_log)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        LeadID = intent.getIntExtra("LeadID",0)
        LogUtil.d(TAG,""+LeadID)
        LeadGUID = intent.getStringExtra("GUID")
        LogUtil.d(TAG,""+LeadGUID)
    }

    override fun initializeView() {
        if (isOnline(this@LeadActivityLog)) {
            callActivityLog()
        } else {
            internetErrordialog(this@LeadActivityLog)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        llUpcoming.setOnClickListener(this)
        llPast.setOnClickListener(this)
        txtInquiry.setOnClickListener(this)
        txtLead.setOnClickListener(this)
        txtAll.setOnClickListener(this)
        imgBack.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvInquiryActivityLog.layoutManager = manager

        arrayListActivity = ArrayList()
        adapter = ParticularInquiryAdapter(arrayListActivity!!, this@LeadActivityLog, tabPosition)

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
                            if (model.ActivityTitle!!.toLowerCase()
                                    .contains(strSearch.toLowerCase()) ||
                                model.ActivityDescription!!.toLowerCase()
                                    .contains(strSearch.toLowerCase()) ||
                                model.CreatedOn!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.ActivityDate!!.toLowerCase().contains(strSearch.toLowerCase())
                            ) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                        }
                    }
                    arrayListActivityNew = arrItemsFinal1
                    val itemAdapter = ParticularInquiryAdapter(arrayListActivity!!, this@LeadActivityLog, tabPosition)
                    RvInquiryActivityLog.adapter = itemAdapter
                } else {
                    arrayListActivityNew = arrayListActivity
                    val itemAdapter = ParticularInquiryAdapter(arrayListActivity!!, this@LeadActivityLog, tabPosition)
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
                val itemAdapter = ParticularInquiryAdapter(arrayListActivity!!, this@LeadActivityLog, tabPosition)
                RvInquiryActivityLog.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(
                    this@LeadActivityLog,
                    R.anim.searchview_close_anim
                )
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(
                    this@LeadActivityLog,
                    R.anim.searchview_open_anim
                )
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@LeadActivityLog, refreshLayout)
            searchView.closeSearch()
            callActivityLog()
            refreshLayout.isRefreshing = false
        }
    }

    override fun onClick(v: View?) {
        hideKeyboard(this@LeadActivityLog, v)
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

            R.id.txtInquiry -> {
                preventTwoClick(v)

                searchView.closeSearch()
                Status = "Inquiry"
                tabPosition = 0
                callActivityLog()

                txtInquiry.setBackgroundResource(R.drawable.bg_transparent)
                txtInquiry.setTextColor(ContextCompat.getColor(this, R.color.white))
                txtInquiry.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.colorPrimaryDark))

                txtLead.setBackgroundResource(R.drawable.bg)
                txtLead.setTextColor(ContextCompat.getColor(this, R.color.colorGray500))
                txtLead.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.transparent))

                txtAll.setBackgroundResource(R.drawable.bg)
                txtAll.setTextColor(ContextCompat.getColor(this, R.color.colorGray500))
                txtAll.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.transparent))

                llUpcoming.background = ContextCompat.getDrawable(this, R.color.colorPrimary)
                tvUpcoming.setTextColor(ContextCompat.getColor(this, R.color.white))

                llPast.background = ContextCompat.getDrawable(this, R.color.white)
                tvPast.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            }
            R.id.txtLead -> {
                preventTwoClick(v)

                searchView.closeSearch()
                Status = "Lead"
                tabPosition = 0
                callActivityLog()

                txtLead.setBackgroundResource(R.drawable.bg_transparent)
                txtLead.setTextColor(ContextCompat.getColor(this, R.color.white))
                txtLead.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.colorPrimaryDark))

                txtInquiry.setBackgroundResource(R.drawable.bg)
                txtInquiry.setTextColor(ContextCompat.getColor(this, R.color.colorGray500))
                txtInquiry.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.transparent))

                txtAll.setBackgroundResource(R.drawable.bg)
                txtAll.setTextColor(ContextCompat.getColor(this, R.color.colorGray500))
                txtAll.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.transparent))

                llUpcoming.background = ContextCompat.getDrawable(this, R.color.colorPrimary)
                tvUpcoming.setTextColor(ContextCompat.getColor(this, R.color.white))

                llPast.background = ContextCompat.getDrawable(this, R.color.white)
                tvPast.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            }
            R.id.txtAll -> {
                preventTwoClick(v)

                searchView.closeSearch()
                Status = "All "
                tabPosition = 0
                callActivityLog()

                txtAll.setBackgroundResource(R.drawable.bg_transparent)
                txtAll.setTextColor(ContextCompat.getColor(this, R.color.white))
                txtAll.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.colorPrimaryDark))

                txtInquiry.setBackgroundResource(R.drawable.bg)
                txtInquiry.setTextColor(ContextCompat.getColor(this, R.color.colorGray500))
                txtInquiry.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.transparent))

                txtLead.setBackgroundResource(R.drawable.bg)
                txtLead.setTextColor(ContextCompat.getColor(this, R.color.colorGray500))
                txtLead.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.transparent))

                llUpcoming.background = ContextCompat.getDrawable(this, R.color.colorPrimary)
                tvUpcoming.setTextColor(ContextCompat.getColor(this, R.color.white))

                llPast.background = ContextCompat.getDrawable(this, R.color.white)
                tvPast.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
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

    private fun callActivityLog() {

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(applicationContext)
        }

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("PastActivityFilters", tabPosition)
        jsonObject.put("NBGUID", LeadGUID)
        jsonObject.put("NBActivityType", Status)
        jsonObject.put("CreatedBy", 0)
        jsonObject.put("FromDate", "")
        jsonObject.put("ToDate", "")

        val call =
            ApiUtils.apiInterface.ActivityLogGUID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<ActivityLogResponse> {
            override fun onResponse(
                call: Call<ActivityLogResponse>,
                response: Response<ActivityLogResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListActivity?.clear()
                        arrayListActivityNew?.clear()
                        arrayListActivity = response.body()?.Data!!
                        arrayListActivityNew = arrayListActivity

                        if (arrayListActivityNew!!.size > 0) {
                            var myAdapter = ParticularInquiryAdapter(
                                arrayListActivityNew!!,
                                this@LeadActivityLog,
                                tabPosition
                            )
                            RvInquiryActivityLog.adapter = myAdapter
                            shimmer.stopShimmer()
                            shimmer.gone()
                            FL.visible()
                            RLNoData.gone()
                        } else {
                            Snackbar.make(
                                layout,
                                response.body()?.Details.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                            shimmer.stopShimmer()
                            shimmer.gone()
                            FL.gone()
                            RLNoData.visible()
                        }
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
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
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
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
        }
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