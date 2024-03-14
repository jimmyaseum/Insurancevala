package com.app.insurancevala.activity.Lead

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.MeetingsListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetMeetingStatusListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.MeetingStatusModel
import com.app.insurancevala.model.response.MeetingStatusResponse
import com.app.insurancevala.model.response.MeetingsModel
import com.app.insurancevala.model.response.MeetingsResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_meetings_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MeetingsListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter : MeetingsListAdapter
    var arrayListMeetings: ArrayList<MeetingsModel>? = ArrayList()
    var arrayListMeetingsNew: ArrayList<MeetingsModel>? = ArrayList()
    var ID: Int? = null
    var LeadID: Int? = null

    var arrayListmeetingstatus  : ArrayList<MeetingStatusModel>? = ArrayList()
    var mMeetingstatus : String = ""
    var mMeetingstatusID : Int = 0

    var ClosedMeeting : Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meetings_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        LeadID = intent.getIntExtra("LeadID",0)
        ID = intent.getIntExtra("ID",0)
        ClosedMeeting = intent.getIntExtra("ClosedMeeting", 0)
        if (ClosedMeeting != 0) {
            txtHearderText.text = "Closed Meetings"
        }
    }

    override fun initializeView() {
        if (isOnline(this)) {
            callManageMeetingsStatus(0)
            callManageMeetings()
        } else {
            internetErrordialog(this)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddMeeting.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvMeetingList.layoutManager = manager

        arrayListMeetings = ArrayList()
        arrayListMeetingsNew = ArrayList()
        adapter = MeetingsListAdapter(this, arrayListMeetings!!,this@MeetingsListActivity)

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
                val arrItemsFinal1: ArrayList<MeetingsModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListMeetings!!) {
                        try {
                            if (model.Purpose!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.Location!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.Description!!.toLowerCase().contains(strSearch.toLowerCase()) ) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception){
                        }
                    }
                    arrayListMeetingsNew = arrItemsFinal1
                    val itemAdapter = MeetingsListAdapter(this@MeetingsListActivity, arrayListMeetingsNew!!,this@MeetingsListActivity)
                    RvMeetingList.adapter = itemAdapter
                } else {
                    arrayListMeetingsNew = arrayListMeetings
                    val itemAdapter = MeetingsListAdapter( this@MeetingsListActivity, arrayListMeetingsNew!!, this@MeetingsListActivity)
                    RvMeetingList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListMeetingsNew = arrayListMeetings
                val itemAdapter = MeetingsListAdapter( this@MeetingsListActivity, arrayListMeetingsNew!!, this@MeetingsListActivity)
                RvMeetingList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(this@MeetingsListActivity, R.anim.searchview_close_anim)
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(this@MeetingsListActivity, R.anim.searchview_open_anim)
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@MeetingsListActivity,refreshLayout)
            searchView.closeSearch()
            callManageMeetings()
            refreshLayout.isRefreshing = false
        }
    }

    override fun onClick(v: View?) {
        hideKeyboard(this@MeetingsListActivity, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }
            R.id.imgAddMeeting -> {
                preventTwoClick(v)
                val intent = Intent(this, AddMeetingsActivity::class.java)
                intent.putExtra("ID",ID)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra(AppConstant.STATE,AppConstant.S_ADD)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
        }
    }
    @Suppress("DEPRECATION")
    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when(type) {
            101 -> {
                preventTwoClick(view)
                val intent = Intent(this, MeetingsDetailsActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("ID",ID)
                intent.putExtra("MeetingGUID",arrayListMeetingsNew!![position].MeetingGUID)
                startActivity(intent)
            }
            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddMeetingsActivity::class.java)
                intent.putExtra(AppConstant.STATE,AppConstant.S_EDIT)
                intent.putExtra("ID",ID)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("MeetingGUID",arrayListMeetingsNew!![position].MeetingGUID)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
            103 -> {
                preventTwoClick(view)
                selectStatusDialog(arrayListMeetingsNew!![position].MeetingGUID)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.INTENT_1001) {
            if (isOnline(this)) {
                callManageMeetings()
            } else {
                internetErrordialog(this)
            }
        }
    }
    private fun callManageMeetings() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("NBInquiryTypeID", ID)
        jsonObject.put("LeadID", LeadID)

        if(ClosedMeeting != 0) {
            jsonObject.put("MeetingStatusID", ClosedMeeting)
        }

        val call = ApiUtils.apiInterface.ManageMeetingFindAll(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<MeetingsResponse> {
            override fun onResponse(call: Call<MeetingsResponse>, response: Response<MeetingsResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListMeetings?.clear()
                        arrayListMeetingsNew?.clear()
                        arrayListMeetings = response.body()?.Data!!
                        arrayListMeetingsNew = arrayListMeetings

                        if(arrayListMeetingsNew!!.size > 0) {
                            adapter = MeetingsListAdapter(this@MeetingsListActivity, arrayListMeetingsNew!!,this@MeetingsListActivity)
                            RvMeetingList.adapter = adapter

                            shimmer.stopShimmer()
                            shimmer.gone()
                            FL.visible()
                            RLNoData.gone()

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

            override fun onFailure(call: Call<MeetingsResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun callManageMeetingsStatus(mode: Int) {
        if(mode == 1) {
            showProgress()
        }
        var jsonObject = JSONObject()
        jsonObject.put("OperationType",AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageMeetingStatus(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<MeetingStatusResponse> {
            override fun onResponse(call: Call<MeetingStatusResponse>, response: Response<MeetingStatusResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListmeetingstatus = response.body()?.Data!!
                        arrayListmeetingstatus!!.sortBy { it.ID }

                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(call: Call<MeetingStatusResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun selectStatusDialog(meetingGUID : String?) {
        var dialogSelectUserType = Dialog(this)
        dialogSelectUserType.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectUserType.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectUserType.window!!.attributes)

        dialogSelectUserType.window!!.attributes = lp
        dialogSelectUserType.setCancelable(true)
        dialogSelectUserType.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectUserType.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectUserType.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectUserType.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectUserType.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectUserType.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectUserType.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectUserType.dismiss()
        }

        txtid.text = "Select Meeting Status"
        edtSearchCustomer.gone()

        val itemAdapter = BottomSheetMeetingStatusListAdapter(this, arrayListmeetingstatus!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mMeetingstatus = arrayListmeetingstatus!![pos].MeetingStatus!!
                mMeetingstatusID = arrayListmeetingstatus!![pos].ID!!

                if(mMeetingstatus != "") {
                    CallManageMeetingStatusUpdate(meetingGUID)
                    dialogSelectUserType.dismiss()
                } else {
                    dialogSelectUserType.dismiss()
                }
            }
        })

        rvDialogCustomer.adapter = itemAdapter
        dialogSelectUserType!!.show()
    }

    private fun CallManageMeetingStatusUpdate(MeetingGUID: String?) {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("MeetingGUID", MeetingGUID)
        jsonObject.put("MeetingStatusID", mMeetingstatusID)
        jsonObject.put("OperationType", AppConstant.STATUSUPDATE)

        val call = ApiUtils.apiInterface.ManageMeetings(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<MeetingsResponse> {
            override fun onResponse(call: Call<MeetingsResponse>, response: Response<MeetingsResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        callManageMeetings()
                    }
                }
            }

            override fun onFailure(call: Call<MeetingsResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

}