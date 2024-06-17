package com.app.insurancevala.activity.DashBoard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.activity.NBInquiry.InquiryEditActivity
import com.app.insurancevala.activity.NBInquiry.InquiryListActivity
import com.app.insurancevala.adapter.DashboardInnerLeadListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.DashboardInnerLeadModel
import com.app.insurancevala.model.response.DashboardInnerLeadResponse
import com.app.insurancevala.model.response.DashboardInnerResponse
import com.app.insurancevala.model.response.NBInquiryTypeAddUpdateResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.PrefConstants
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.getRequestJSONBody
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.hideKeyboard
import com.app.insurancevala.utils.internetErrordialog
import com.app.insurancevala.utils.isOnline
import com.app.insurancevala.utils.preventTwoClick
import com.app.insurancevala.utils.visible
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home_inner_lead_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeInnerLeadListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: DashboardInnerLeadListAdapter
    var arrayList: ArrayList<DashboardInnerLeadModel>? = ArrayList()
    var arrayListNew: ArrayList<DashboardInnerLeadModel>? = ArrayList()

    var sharedPreference: SharedPreference? = null

    var OperationType: Int? = null
    var Header: String? = null
    var LeadStatusID: Int? = null
    var FromDate: String? = null
    var ToDate: String? = null
    var UserID: Int? = null
    var InquiryTypeID: Int? = null
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_inner_lead_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        LeadID = intent.getIntExtra("LeadID", 0)
        Header = intent.getStringExtra("Header")
        OperationType = intent.getIntExtra("OperationType", 0)
        LeadStatusID = intent.getIntExtra("LeadStatusID", 0)
        FromDate = intent.getStringExtra("FromDate")
        ToDate = intent.getStringExtra("ToDate")
        LeadStatusID = intent.getIntExtra("LeadStatusID", 0)
        InquiryTypeID = intent.getIntExtra("InquiryTypeID", 0)
        UserID = intent.getIntExtra("UserID", 0)
    }

    override fun initializeView() {

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(this)
        }

        txtHearderText.setText(Header)

        if (isOnline(this@HomeInnerLeadListActivity)) {
            callManageDashboardInnerList()
        } else {
            internetErrordialog(this@HomeInnerLeadListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvList.layoutManager = manager

        arrayList = ArrayList()
        adapter = DashboardInnerLeadListAdapter(this, arrayList!!, this@HomeInnerLeadListActivity)

        imgSearch.setOnClickListener {
            if (searchView.isSearchOpen) {
                searchView.closeSearch()
            } else {
                searchView.showSearch()
            }

        }

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
                val arrItemsFinal1: ArrayList<DashboardInnerLeadModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayList!!) {
                        try {
                            if (model.FirstName!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.LastName!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.MobileNo!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.CreatedByName.toString()!!.toLowerCase().contains(strSearch.toLowerCase())
                            ) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {

                        }
                    }
                    arrayListNew = arrItemsFinal1
                    val itemAdapter = DashboardInnerLeadListAdapter(
                        this@HomeInnerLeadListActivity,
                        arrayListNew!!,
                        this@HomeInnerLeadListActivity
                    )
                    RvList.adapter = itemAdapter
                } else {
                    arrayListNew = arrayList
                    val itemAdapter = DashboardInnerLeadListAdapter(
                        this@HomeInnerLeadListActivity,
                        arrayListNew!!,
                        this@HomeInnerLeadListActivity
                    )
                    RvList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })

        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListNew = arrayList
                val itemAdapter = DashboardInnerLeadListAdapter(
                    this@HomeInnerLeadListActivity,
                    arrayListNew!!,
                    this@HomeInnerLeadListActivity
                )
                RvList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(
                    this@HomeInnerLeadListActivity,
                    R.anim.searchview_close_anim
                )
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(
                    this@HomeInnerLeadListActivity,
                    R.anim.searchview_open_anim
                )
            }
        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@HomeInnerLeadListActivity, refreshLayout)
            searchView.closeSearch()
            callManageDashboardInnerList()
            refreshLayout.isRefreshing = false
        }

    }

    override fun onClick(v: View?) {
        hideKeyboard(this@HomeInnerLeadListActivity, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }
        }
    }

    private fun callManageDashboardInnerList() {

        showProgress()

        var jsonObject = JSONObject()

        if (UserID != 0 && UserID != null) {
            jsonObject.put("UserID", UserID)
        }

        val call = ApiUtils.apiInterface.ManageClientsProspectInnerView(getRequestJSONBody(jsonObject.toString()))

        call.enqueue(object : Callback<DashboardInnerLeadResponse> {
            override fun onResponse(
                call: Call<DashboardInnerLeadResponse>,
                response: Response<DashboardInnerLeadResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayList?.clear()
                        arrayListNew?.clear()
                        arrayList = response.body()?.Data!!
                        arrayListNew = arrayList

                        if (arrayListNew!!.size > 0) {
                            var myAdapter = DashboardInnerLeadListAdapter(
                                this@HomeInnerLeadListActivity,
                                arrayListNew!!,
                                this@HomeInnerLeadListActivity
                            )
                            RvList.adapter = myAdapter
                            shimmer.stopShimmer()
                            shimmer.gone()

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

            override fun onFailure(call: Call<DashboardInnerLeadResponse>, t: Throwable) {
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
            102 -> {
                //edit
                preventTwoClick(view)
                val intent = Intent(this, InquiryListActivity::class.java)
                intent.putExtra("LeadID", arrayListNew!![position].ID)
                intent.putExtra("GUID", arrayListNew!![position].LeadGUID)
                startActivity(intent)
            }
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.INTENT_1001) {
            if (isOnline(this)) {
                callManageDashboardInnerList()
            } else {
                internetErrordialog(this)
            }
        }
    }


}