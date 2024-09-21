package com.app.insurancevala.activity.NBInquiry

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.ActivityLog.ParticularInquiryActivityLog
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.activity.Lead.AttachmentsListActivity
import com.app.insurancevala.activity.Lead.CallsListActivity
import com.app.insurancevala.activity.Lead.MeetingsListActivity
import com.app.insurancevala.activity.Lead.NotesListActivity
import com.app.insurancevala.activity.Lead.RecordingsListActivity
import com.app.insurancevala.activity.Lead.TasksListActivity
import com.app.insurancevala.adapter.InquiryListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.NBInquiryLeadStatusResponse
import com.app.insurancevala.model.response.NBInquiryModel
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.android.synthetic.main.activity_inquiry_type_list.*

class InquiryTypeListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: InquiryListAdapter
    var arrayListInquiry: ArrayList<NBInquiryModel>? = ArrayList()
    var arrayListInquiryNew: ArrayList<NBInquiryModel>? = ArrayList()
    var LeadID: Int? = null
    var LeadGUID: String? = null
    var InquiryStatus: String? = null

    var sharedPreference: SharedPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inquiry_type_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        LeadID = intent.getIntExtra("LeadID",0)
        LeadGUID = intent.getStringExtra("GUID")
        InquiryStatus = intent.getStringExtra("InquiryStatus")
    }

    override fun initializeView() {

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(this)
        }

        if (isOnline(this@InquiryTypeListActivity)) {
            callManageNB()
        } else {
            internetErrordialog(this@InquiryTypeListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        txtOpen.setOnClickListener(this)
        txtClose.setOnClickListener(this)
        txtAll.setOnClickListener(this)
        var manager = LinearLayoutManager(this)
        RvInquiryTypeList.layoutManager = manager

        arrayListInquiry = ArrayList()
        adapter = InquiryListAdapter(this,arrayListInquiry!!, sharedPreference!!, this@InquiryTypeListActivity)

        imgAddInquiryType.gone()

        imgSearch.setOnClickListener {
            if(searchView.isSearchOpen) {
                searchView.closeSearch()
            } else {
                searchView.showSearch()
            }

        }

        if( InquiryStatus == "Open") {
            txtOpen.performClick()
        } else if (InquiryStatus == "Closed") {
            txtClose.performClick()
        }

        searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                val arrItemsFinal1: ArrayList<NBInquiryModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListInquiry!!) {
                        try {
                            if (model.InquiryType!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.InquirySubType!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.InquiryAllotmentName!!.toLowerCase().contains(strSearch.toLowerCase()) ||
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
                    val itemAdapter = InquiryListAdapter(this@InquiryTypeListActivity, arrayListInquiryNew!!, sharedPreference!!,this@InquiryTypeListActivity)
                    RvInquiryTypeList.adapter = itemAdapter
                } else {
                    arrayListInquiryNew = arrayListInquiry
                    val itemAdapter = InquiryListAdapter( this@InquiryTypeListActivity, arrayListInquiryNew!!, sharedPreference!!, this@InquiryTypeListActivity)
                    RvInquiryTypeList.adapter = itemAdapter
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
                val itemAdapter = InquiryListAdapter( this@InquiryTypeListActivity, arrayListInquiryNew!!, sharedPreference!!,this@InquiryTypeListActivity)
                RvInquiryTypeList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(this@InquiryTypeListActivity, R.anim.searchview_close_anim)
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(this@InquiryTypeListActivity, R.anim.searchview_open_anim)
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@InquiryTypeListActivity,refreshLayout)
            searchView.closeSearch()
            callManageNB()
            refreshLayout.isRefreshing = false
        }
    }

    override fun onClick(v: View?) {
        hideKeyboard(this@InquiryTypeListActivity, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }
            R.id.txtOpen -> {
                preventTwoClick(v)

                searchView.closeSearch()
                InquiryStatus = "Open"
                callManageNB()

                txtOpen.setBackgroundResource(R.drawable.bg_transparent)
                txtOpen.setTextColor(ContextCompat.getColor(this, R.color.white))
                txtOpen.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.colorPrimaryDark))

                txtClose.setBackgroundResource(R.drawable.bg)
                txtClose.setTextColor(ContextCompat.getColor(this, R.color.colorGray500))
                txtClose.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.transparent))

                txtAll.setBackgroundResource(R.drawable.bg)
                txtAll.setTextColor(ContextCompat.getColor(this, R.color.colorGray500))
                txtAll.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.transparent))
            }
            R.id.txtClose -> {
                preventTwoClick(v)

                searchView.closeSearch()
                InquiryStatus = "Closed"
                callManageNB()

                txtClose.setBackgroundResource(R.drawable.bg_transparent)
                txtClose.setTextColor(ContextCompat.getColor(this, R.color.white))
                txtClose.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.colorPrimaryDark))

                txtOpen.setBackgroundResource(R.drawable.bg)
                txtOpen.setTextColor(ContextCompat.getColor(this, R.color.colorGray500))
                txtOpen.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.transparent))

                txtAll.setBackgroundResource(R.drawable.bg)
                txtAll.setTextColor(ContextCompat.getColor(this, R.color.colorGray500))
                txtAll.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.transparent))
            }
            R.id.txtAll -> {
                preventTwoClick(v)

                searchView.closeSearch()
                InquiryStatus = ""
                callManageNB()

                txtAll.setBackgroundResource(R.drawable.bg_transparent)
                txtAll.setTextColor(ContextCompat.getColor(this, R.color.white))
                txtAll.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.colorPrimaryDark))

                txtOpen.setBackgroundResource(R.drawable.bg)
                txtOpen.setTextColor(ContextCompat.getColor(this, R.color.colorGray500))
                txtOpen.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.transparent))

                txtClose.setBackgroundResource(R.drawable.bg)
                txtClose.setTextColor(ContextCompat.getColor(this, R.color.colorGray500))
                txtClose.backgroundTintList = ColorStateList.valueOf( ContextCompat.getColor(this,R.color.transparent))
            }
        }
    }

    private fun callManageNB() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("LeadGUID",LeadGUID)
        jsonObject.put("InquiryStatus",InquiryStatus)
//        jsonObject.put("OperationType", AppConstant.GETBYGUID)

        val call = ApiUtils.apiInterface.ManageNBInquiryFindLeadStatus(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<NBInquiryLeadStatusResponse> {
            override fun onResponse(call: Call<NBInquiryLeadStatusResponse>, response: Response<NBInquiryLeadStatusResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListInquiry?.clear()
                        arrayListInquiryNew?.clear()
                        arrayListInquiry = response.body()?.Data!!
                        arrayListInquiryNew = arrayListInquiry

                        if(arrayListInquiryNew!!.size > 0) {
                            var myAdapter = InquiryListAdapter(this@InquiryTypeListActivity, arrayListInquiryNew!!, sharedPreference!!, this@InquiryTypeListActivity)
                            RvInquiryTypeList.adapter = myAdapter
                            FL.visible()
                            RLNoData.gone()
                            shimmer.stopShimmer()
                            shimmer.gone()
                        } else {
                            Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                            shimmer.stopShimmer()
                            shimmer.gone()
                            FL.gone()
                            RLNoData.visible()
                        }
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        shimmer.stopShimmer()
                        shimmer.gone()
                        FL.gone()
                        RLNoData.visible()
                    }
                }
            }
            override fun onFailure(call: Call<NBInquiryLeadStatusResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    @Suppress("DEPRECATION")
    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when (type) {
            102 -> {
                //edit
                preventTwoClick(view)
                val intent = Intent(this, InquiryEditActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("NBInquiryTypeGUID",arrayListInquiryNew!![position].NBInquiryTypeGUID)
                startActivityForResult(intent, AppConstant.INTENT_1001)
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
                intent.putExtra("LeadID",LeadID)
                startActivity(intent)
            }
            105 -> {
                //Tasks
                preventTwoClick(view)
                val intent = Intent(this, TasksListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",LeadID)
                startActivity(intent)
            }
            106 -> {
                //Calls
                preventTwoClick(view)
                val intent = Intent(this, CallsListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",LeadID)
                startActivity(intent)
            }
            107 -> {
                //Meetings
                preventTwoClick(view)
                val intent = Intent(this, MeetingsListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",LeadID)
                startActivity(intent)
            }
            108 -> {
                //Attachments
                preventTwoClick(view)
                val intent = Intent(this, AttachmentsListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",LeadID)
                startActivity(intent)
            }
            109 -> {
                //Closed_Tasks
                preventTwoClick(view)
                val intent = Intent(this, TasksListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("ClosedTask","Completed")
                startActivity(intent)
            }
            110 -> {
                //Closed_Calls
                preventTwoClick(view)
                val intent = Intent(this, CallsListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("ClosedCall","Completed")
                startActivity(intent)
            }
            111 -> {
                //Closed_Meetings
                preventTwoClick(view)
                val intent = Intent(this, MeetingsListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("ClosedMeeting",4)
                startActivity(intent)
            }
            112 -> {
                //Attachments
                preventTwoClick(view)
                val intent = Intent(this, RecordingsListActivity::class.java)
                intent.putExtra("ID",arrayListInquiryNew!![position].ID)
                intent.putExtra("LeadID",LeadID)
                startActivity(intent)
            }
            113 -> {
                //Activity Log
                preventTwoClick(view)
                val intent = Intent(this, ParticularInquiryActivityLog::class.java)
                intent.putExtra("NBInquiryTypeGUID",arrayListInquiryNew!![position].NBInquiryTypeGUID)
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