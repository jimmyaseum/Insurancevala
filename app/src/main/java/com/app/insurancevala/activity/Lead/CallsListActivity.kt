package com.app.insurancevala.activity.Lead

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.CallsListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.CallsModel
import com.app.insurancevala.model.response.CallsResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_calls_list.*
import kotlinx.android.synthetic.main.activity_calls_list.layout
import kotlinx.android.synthetic.main.activity_calls_list.imgSearch
import kotlinx.android.synthetic.main.activity_calls_list.searchView
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallsListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter : CallsListAdapter
    var arrayListCalls: ArrayList<CallsModel>? = ArrayList()
    var arrayListCallsNew: ArrayList<CallsModel>? = ArrayList()
    var LeadID: Int? = null

    var ClosedCall : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calls_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        LeadID = intent.getIntExtra("LeadID",0)
        ClosedCall = intent.getStringExtra("ClosedCall")
    }

    override fun initializeView() {
        if (isOnline(this@CallsListActivity)) {
            callManageCalls()
        } else {
            internetErrordialog(this@CallsListActivity)
        }
        SetInitListner()
    }
    private fun SetInitListner() {

        imgBack.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvCallsList.layoutManager = manager

        arrayListCalls = ArrayList()
        adapter = CallsListAdapter(this, arrayListCalls!!,this@CallsListActivity)

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
                val arrItemsFinal1: ArrayList<CallsModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListCalls!!) {
                        try {
                            if (model.Subject!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.CallStatus!!.toLowerCase().contains(strSearch.toLowerCase())) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception){
                        }
                    }
                    arrayListCallsNew = arrItemsFinal1
                    val itemAdapter = CallsListAdapter(this@CallsListActivity, arrayListCallsNew!!,this@CallsListActivity)
                    RvCallsList.adapter = itemAdapter
                } else {
                    arrayListCallsNew = arrayListCalls
                    val itemAdapter = CallsListAdapter( this@CallsListActivity, arrayListCallsNew!!, this@CallsListActivity)
                    RvCallsList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListCallsNew = arrayListCalls
                val itemAdapter = CallsListAdapter( this@CallsListActivity, arrayListCallsNew!!, this@CallsListActivity)
                RvCallsList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(this@CallsListActivity, R.anim.searchview_close_anim)
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(this@CallsListActivity, R.anim.searchview_open_anim)
            }

        })

        refreshLayout.setOnRefreshListener {
            callManageCalls()
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
        }
    }

    @Suppress("DEPRECATION")
    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when(type) {
            101 -> {
                preventTwoClick(view)
                val intent = Intent(this, CallsDetailsActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("CallGUID",arrayListCallsNew!![position].CallGUID)
                startActivity(intent)
            }
            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddCallLogsActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("CallGUID",arrayListCallsNew!![position].CallGUID)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.INTENT_1001) {
            if (isOnline(this)) {
                callManageCalls()
            } else {
                internetErrordialog(this)
            }
        }
    }

    private fun callManageCalls() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("LeadID", LeadID)
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)

        if(ClosedCall != "") {
            jsonObject.put("CallStatus", ClosedCall)
        }

        val call = ApiUtils.apiInterface.ManageCalls(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CallsResponse> {
            override fun onResponse(call: Call<CallsResponse>, response: Response<CallsResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListCalls?.clear()
                        arrayListCallsNew?.clear()
                        arrayListCalls = response.body()?.Data!!
                        arrayListCallsNew = arrayListCalls

                        if(arrayListCallsNew!!.size > 0) {
                            adapter = CallsListAdapter(this@CallsListActivity, arrayListCallsNew!!,this@CallsListActivity)
                            RvCallsList.adapter = adapter

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

            override fun onFailure(call: Call<CallsResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })

    }
}