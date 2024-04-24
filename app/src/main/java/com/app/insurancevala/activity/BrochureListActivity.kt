package com.app.insurancevala.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.PlanBrochuresListAdapter
import com.app.insurancevala.adapter.UsersListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.PlanBrochuresModel
import com.app.insurancevala.model.response.PlanBrochuresResponse
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
import kotlinx.android.synthetic.main.activity_brochure_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BrochureListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter : PlanBrochuresListAdapter
    var arrayListPlanBrochure: ArrayList<PlanBrochuresModel>? = ArrayList()
    var arrayListPlanBrochureNew: ArrayList<PlanBrochuresModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brochure_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun initializeView() {
        if (isOnline(this@BrochureListActivity)) {
            callManagePlanBrochure()
        } else {
            internetErrordialog(this@BrochureListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddPlanBrochure.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvBrochureList.layoutManager = manager

        arrayListPlanBrochure = ArrayList()
        adapter = PlanBrochuresListAdapter(this, arrayListPlanBrochure!!,this@BrochureListActivity)

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
                val arrItemsFinal1: ArrayList<PlanBrochuresModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListPlanBrochure!!) {
                        try {
                            if (model.CompanyName!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.PlanName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception){
                        }
                    }
                    arrayListPlanBrochureNew = arrItemsFinal1
                    val itemAdapter = PlanBrochuresListAdapter(this@BrochureListActivity, arrayListPlanBrochureNew!!,this@BrochureListActivity)
                    RvBrochureList.adapter = itemAdapter
                } else {
                    arrayListPlanBrochureNew = arrayListPlanBrochure
                    val itemAdapter = PlanBrochuresListAdapter( this@BrochureListActivity, arrayListPlanBrochureNew!!, this@BrochureListActivity)
                    RvBrochureList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListPlanBrochureNew = arrayListPlanBrochure
                val itemAdapter = PlanBrochuresListAdapter( this@BrochureListActivity, arrayListPlanBrochureNew!!, this@BrochureListActivity)
                RvBrochureList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(this@BrochureListActivity, R.anim.searchview_close_anim)
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(this@BrochureListActivity, R.anim.searchview_open_anim)
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@BrochureListActivity,refreshLayout)
            searchView.closeSearch()
            callManagePlanBrochure()
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
            R.id.imgAddPlanBrochure -> {
                preventTwoClick(v)
                val intent = Intent(this@BrochureListActivity, AddBrochureActivity::class.java)
                intent.putExtra(AppConstant.STATE,AppConstant.S_ADD)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when(type) {

            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddBrochureActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra("PlanBrochureID",arrayListPlanBrochureNew!![position].ID)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
            103 -> {
                preventTwoClick(view)
                AwesomeDialog.build(this)
                    .title("Warning !!!")
                    .body("Are you sure want to delete this PlanBrochure?")
                    .icon(R.drawable.ic_delete)
                    .position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }
                    .onPositive("Yes") {
                        CallDeleteAPI(arrayListPlanBrochureNew!![position].ID!!)
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
                callManagePlanBrochure()
            } else {
                internetErrordialog(this)
            }
        }
    }

    private fun CallDeleteAPI(ID : Int) {

        var jsonObject = JSONObject()
        jsonObject.put("ID", ID)
        jsonObject.put("OperationType", AppConstant.DELETE)

        val call = ApiUtils.apiInterface.ManagePlanBrochureDelete(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        callManagePlanBrochure()
                    }
                }
            }
            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun callManagePlanBrochure() {

        showProgress()

        val call = ApiUtils.apiInterface.getPlanBrochureAllActive()
        call.enqueue(object : Callback<PlanBrochuresResponse> {
            override fun onResponse(call: Call<PlanBrochuresResponse>, response: Response<PlanBrochuresResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListPlanBrochure?.clear()
                        arrayListPlanBrochureNew?.clear()
                        arrayListPlanBrochure = response.body()?.Data!!
                        arrayListPlanBrochureNew = arrayListPlanBrochure

                        if(arrayListPlanBrochureNew!!.size > 0) {
                            adapter = PlanBrochuresListAdapter(this@BrochureListActivity, arrayListPlanBrochureNew!!,this@BrochureListActivity)
                            RvBrochureList.adapter = adapter

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

            override fun onFailure(call: Call<PlanBrochuresResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })

    }
}