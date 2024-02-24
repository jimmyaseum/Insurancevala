package com.app.insurancevala.master

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.master.InitialTypeListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.InitialModel
import com.app.insurancevala.model.response.InitialResponse
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
import kotlinx.android.synthetic.main.activity_initial_type_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InitialTypeListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: InitialTypeListAdapter
    var arrayListInitialType: ArrayList<InitialModel>? = ArrayList()
    var arrayListInitialTypeNew: ArrayList<InitialModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial_type_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun onRestart() {
        super.onRestart()
        searchView.closeSearch()
        if (isOnline(this)) {
            callManageInitialType()
        } else {
            internetErrordialog(this)
        }
    }

    override fun initializeView() {
        if (isOnline(this@InitialTypeListActivity)) {
            callManageInitialType()
        } else {
            internetErrordialog(this@InitialTypeListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddInitialType.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvInitialTypeList.layoutManager = manager

        arrayListInitialType = ArrayList()
        adapter = InitialTypeListAdapter(this, arrayListInitialType!!, this@InitialTypeListActivity)

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
                val arrItemsFinal1: ArrayList<InitialModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListInitialType!!) {
                        try {
                            if (model.Initial!!.toLowerCase()
                                    .contains(strSearch.toLowerCase())
                            ) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                        }
                    }
                    arrayListInitialTypeNew = arrItemsFinal1
                    val itemAdapter = InitialTypeListAdapter(
                        this@InitialTypeListActivity,
                        arrayListInitialTypeNew!!,
                        this@InitialTypeListActivity
                    )
                    RvInitialTypeList.adapter = itemAdapter
                } else {
                    arrayListInitialTypeNew = arrayListInitialType
                    val itemAdapter = InitialTypeListAdapter(
                        this@InitialTypeListActivity,
                        arrayListInitialTypeNew!!,
                        this@InitialTypeListActivity
                    )
                    RvInitialTypeList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListInitialTypeNew = arrayListInitialType
                val itemAdapter = InitialTypeListAdapter(
                    this@InitialTypeListActivity,
                    arrayListInitialTypeNew!!,
                    this@InitialTypeListActivity
                )
                RvInitialTypeList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(
                    this@InitialTypeListActivity, R.anim.searchview_close_anim
                )
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(
                    this@InitialTypeListActivity, R.anim.searchview_open_anim
                )
            }

        })

        refreshLayout.setOnRefreshListener {
            callManageInitialType()
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

            R.id.imgAddInitialType -> {
                preventTwoClick(v)
                val intent =
                    Intent(this@InitialTypeListActivity, AddInitialTypeActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_ADD)
                startActivity(intent)
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when (type) {

            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddInitialTypeActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra("InitialGUID", arrayListInitialTypeNew!![position].InitialGUID)
                startActivity(intent)
            }

            103 -> {
                preventTwoClick(view)
                AwesomeDialog.build(this).title("Warning !!!")
                    .body("Are you sure want to delete this Initial Type?")
                    .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }.onPositive("Yes") {
                        CallDeleteAPI(arrayListInitialTypeNew!![position].InitialGUID!!)
                    }
            }
        }
    }

    private fun CallDeleteAPI(GUID: String) {

        var jsonObject = JSONObject()
        jsonObject.put("InitialGUID", GUID)
        jsonObject.put("OperationType", AppConstant.DELETE)

        val call =
            ApiUtils.apiInterface.ManageInitial(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<InitialResponse> {
            override fun onResponse(
                call: Call<InitialResponse>, response: Response<InitialResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        callManageInitialType()
                    }
                }
            }

            override fun onFailure(call: Call<InitialResponse>, t: Throwable) {
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun callManageInitialType() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("OperationType", AppConstant.GETALL)

        val call =
            ApiUtils.apiInterface.ManageInitial(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<InitialResponse> {
            override fun onResponse(
                call: Call<InitialResponse>, response: Response<InitialResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListInitialType?.clear()
                        arrayListInitialTypeNew?.clear()
                        arrayListInitialType = response.body()?.Data!!
                        arrayListInitialTypeNew = arrayListInitialType

                        if (arrayListInitialTypeNew!!.size > 0) {
                            adapter = InitialTypeListAdapter(
                                this@InitialTypeListActivity,
                                arrayListInitialTypeNew!!,
                                this@InitialTypeListActivity
                            )
                            RvInitialTypeList.adapter = adapter

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

            override fun onFailure(call: Call<InitialResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }
}