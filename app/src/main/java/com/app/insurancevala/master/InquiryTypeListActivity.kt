package com.app.insurancevala.master

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.master.InquiryTypeListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.InquiryTypeModel
import com.app.insurancevala.model.response.InquiryTypeResponse
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
import kotlinx.android.synthetic.main.activity_inquiry_type_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InquiryTypeListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: InquiryTypeListAdapter
    var arrayListInquiryType: ArrayList<InquiryTypeModel>? = ArrayList()
    var arrayListInquiryTypeNew: ArrayList<InquiryTypeModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inquiry_type_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun onRestart() {
        super.onRestart()
        searchView.closeSearch()
        if (isOnline(this)) {
            callManageInquiryType()
        } else {
            internetErrordialog(this)
        }
    }

    override fun initializeView() {
        if (isOnline(this@InquiryTypeListActivity)) {
            callManageInquiryType()
        } else {
            internetErrordialog(this@InquiryTypeListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddInquiryType.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvInquiryTypeList.layoutManager = manager

        arrayListInquiryType = ArrayList()
        adapter = InquiryTypeListAdapter(this, arrayListInquiryType!!, this@InquiryTypeListActivity)

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
                val arrItemsFinal1: ArrayList<InquiryTypeModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListInquiryType!!) {
                        try {
                            if (model.InquiryType!!.toLowerCase()
                                    .contains(strSearch.toLowerCase())
                            ) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                        }
                    }
                    arrayListInquiryTypeNew = arrItemsFinal1
                    val itemAdapter = InquiryTypeListAdapter(
                        this@InquiryTypeListActivity,
                        arrayListInquiryTypeNew!!,
                        this@InquiryTypeListActivity
                    )
                    RvInquiryTypeList.adapter = itemAdapter
                } else {
                    arrayListInquiryTypeNew = arrayListInquiryType
                    val itemAdapter = InquiryTypeListAdapter(
                        this@InquiryTypeListActivity,
                        arrayListInquiryTypeNew!!,
                        this@InquiryTypeListActivity
                    )
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
                arrayListInquiryTypeNew = arrayListInquiryType
                val itemAdapter = InquiryTypeListAdapter(
                    this@InquiryTypeListActivity,
                    arrayListInquiryTypeNew!!,
                    this@InquiryTypeListActivity
                )
                RvInquiryTypeList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(
                    this@InquiryTypeListActivity, R.anim.searchview_close_anim
                )
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(
                    this@InquiryTypeListActivity, R.anim.searchview_open_anim
                )
            }

        })

        refreshLayout.setOnRefreshListener {
            callManageInquiryType()
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

            R.id.imgAddInquiryType -> {
                preventTwoClick(v)
                val intent =
                    Intent(this@InquiryTypeListActivity, AddInquiryTypeActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_ADD)
                startActivity(intent)
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when (type) {

            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddInquiryTypeActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra(
                    "InquiryTypeGUID", arrayListInquiryTypeNew!![position].InquiryTypeGUID
                )
                startActivity(intent)
            }

            103 -> {
                preventTwoClick(view)
                AwesomeDialog.build(this).title("Warning !!!")
                    .body("Are you sure want to delete this Inquiry Type?")
                    .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }.onPositive("Yes") {
                        CallDeleteAPI(arrayListInquiryTypeNew!![position].InquiryTypeGUID!!)
                    }
            }
        }
    }

    private fun CallDeleteAPI(GUID: String) {

        var jsonObject = JSONObject()
        jsonObject.put("InquiryTypeGUID", GUID)
        jsonObject.put("OperationType", AppConstant.DELETE)

        val call =
            ApiUtils.apiInterface.ManageInquiryType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<InquiryTypeResponse> {
            override fun onResponse(
                call: Call<InquiryTypeResponse>, response: Response<InquiryTypeResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        callManageInquiryType()
                    }
                }
            }

            override fun onFailure(call: Call<InquiryTypeResponse>, t: Throwable) {
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun callManageInquiryType() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("OperationType", AppConstant.GETALL)

        val call =
            ApiUtils.apiInterface.ManageInquiryType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<InquiryTypeResponse> {
            override fun onResponse(
                call: Call<InquiryTypeResponse>, response: Response<InquiryTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListInquiryType?.clear()
                        arrayListInquiryTypeNew?.clear()
                        arrayListInquiryType = response.body()?.Data!!
                        arrayListInquiryTypeNew = arrayListInquiryType

                        if (arrayListInquiryTypeNew!!.size > 0) {
                            adapter = InquiryTypeListAdapter(
                                this@InquiryTypeListActivity,
                                arrayListInquiryTypeNew!!,
                                this@InquiryTypeListActivity
                            )
                            RvInquiryTypeList.adapter = adapter

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

            override fun onFailure(call: Call<InquiryTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }
}