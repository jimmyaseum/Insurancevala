package com.app.insurancevala.master

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.master.LeadSourceListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.LeadSourceModel
import com.app.insurancevala.model.response.LeadSourceResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.icon
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.position
import com.example.awesomedialog.title
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_lead_source_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeadSourceListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: LeadSourceListAdapter
    var arrayListLeadSource: ArrayList<LeadSourceModel>? = ArrayList()
    var arrayListLeadSourceNew: ArrayList<LeadSourceModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_source_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun onRestart() {
        super.onRestart()
        searchView.closeSearch()
        if (isOnline(this)) {
            callManageLeadSource()
        } else {
            internetErrordialog(this)
        }
    }

    override fun initializeView() {
        if (isOnline(this@LeadSourceListActivity)) {
            callManageLeadSource()
        } else {
            internetErrordialog(this@LeadSourceListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddLeadSource.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvLeadSourceList.layoutManager = manager

        arrayListLeadSource = ArrayList()
        adapter = LeadSourceListAdapter(this, arrayListLeadSource!!, this@LeadSourceListActivity)

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
                val arrItemsFinal1: ArrayList<LeadSourceModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListLeadSource!!) {
                        try {
                            if (model.LeadSource!!.toLowerCase().contains(strSearch.toLowerCase())) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                        }
                    }
                    arrayListLeadSourceNew = arrItemsFinal1
                    val itemAdapter = LeadSourceListAdapter(
                        this@LeadSourceListActivity, arrayListLeadSourceNew!!, this@LeadSourceListActivity
                    )
                    RvLeadSourceList.adapter = itemAdapter
                } else {
                    arrayListLeadSourceNew = arrayListLeadSource
                    val itemAdapter = LeadSourceListAdapter(
                        this@LeadSourceListActivity, arrayListLeadSourceNew!!, this@LeadSourceListActivity
                    )
                    RvLeadSourceList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListLeadSourceNew = arrayListLeadSource
                val itemAdapter = LeadSourceListAdapter(
                    this@LeadSourceListActivity, arrayListLeadSourceNew!!, this@LeadSourceListActivity
                )
                RvLeadSourceList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(
                    this@LeadSourceListActivity, R.anim.searchview_close_anim
                )
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(
                    this@LeadSourceListActivity, R.anim.searchview_open_anim
                )
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@LeadSourceListActivity,refreshLayout)
            searchView.closeSearch()
            callManageLeadSource()
            refreshLayout.isRefreshing = false
        }
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }

            R.id.imgAddLeadSource -> {
                preventTwoClick(v)
                val intent = Intent(this@LeadSourceListActivity, AddLeadSourceActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_ADD)
                startActivity(intent)
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, Status: Int) {
        when (Status) {

            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddLeadSourceActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra(
                    "LeadSourceGUID", arrayListLeadSourceNew!![position].LeadSourceGUID
                )
                startActivity(intent)
            }

            103 -> {
                preventTwoClick(view)
                AwesomeDialog.build(this).title("Warning !!!")
                    .body("Are you sure want to delete this Lead Source?")
                    .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }.onPositive("Yes") {
                        CallDeleteAPI(arrayListLeadSourceNew!![position].LeadSourceGUID!!)
                    }
            }
        }
    }

    private fun CallDeleteAPI(GUID: String) {

        var jsonObject = JSONObject()
        jsonObject.put("LeadSourceGUID", GUID)
        jsonObject.put("OperationType", AppConstant.DELETE)

        val call = ApiUtils.apiInterface.ManageLeadSource(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<LeadSourceResponse> {
            override fun onResponse(
                call: Call<LeadSourceResponse>, response: Response<LeadSourceResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        callManageLeadSource()
                    }
                }
            }

            override fun onFailure(call: Call<LeadSourceResponse>, t: Throwable) {
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun callManageLeadSource() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("OperationType", AppConstant.GETALL)

        val call = ApiUtils.apiInterface.ManageLeadSource(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<LeadSourceResponse> {
            override fun onResponse(
                call: Call<LeadSourceResponse>, response: Response<LeadSourceResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListLeadSource?.clear()
                        arrayListLeadSourceNew?.clear()
                        arrayListLeadSource = response.body()?.Data!!
                        arrayListLeadSourceNew = arrayListLeadSource

                        if (arrayListLeadSourceNew!!.size > 0) {
                            adapter = LeadSourceListAdapter(
                                this@LeadSourceListActivity,
                                arrayListLeadSourceNew!!,
                                this@LeadSourceListActivity
                            )
                            RvLeadSourceList.adapter = adapter

                            shimmer.stopShimmer()
                            shimmer.gone()

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

            override fun onFailure(call: Call<LeadSourceResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }
}