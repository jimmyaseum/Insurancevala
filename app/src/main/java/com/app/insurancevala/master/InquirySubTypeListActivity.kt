package com.app.insurancevala.master

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.master.InquirySubTypeListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.InquirySubTypeModel
import com.app.insurancevala.model.response.InquirySubTypeResponse
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
import kotlinx.android.synthetic.main.activity_inquiry_sub_type_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InquirySubTypeListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: InquirySubTypeListAdapter
    var arrayListInquirySubType: ArrayList<InquirySubTypeModel>? = ArrayList()
    var arrayListInquirySubTypeNew: ArrayList<InquirySubTypeModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inquiry_sub_type_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun onRestart() {
        super.onRestart()
        searchView.closeSearch()
        if (isOnline(this)) {
            callManageInquirySubType()
        } else {
            internetErrordialog(this)
        }
    }

    override fun initializeView() {
        if (isOnline(this@InquirySubTypeListActivity)) {
            callManageInquirySubType()
        } else {
            internetErrordialog(this@InquirySubTypeListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddInquirySubType.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvInquiryTypeList.layoutManager = manager

        arrayListInquirySubType = ArrayList()
        adapter = InquirySubTypeListAdapter(this, arrayListInquirySubType!!, this@InquirySubTypeListActivity)

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
                val arrItemsFinal1: ArrayList<InquirySubTypeModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListInquirySubType!!) {
                        try {
                            if (model.InquiryType!!.toLowerCase()
                                    .contains(strSearch.toLowerCase())
                            ) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                        }
                    }
                    arrayListInquirySubTypeNew = arrItemsFinal1
                    val itemAdapter = InquirySubTypeListAdapter(
                        this@InquirySubTypeListActivity,
                        arrayListInquirySubTypeNew!!,
                        this@InquirySubTypeListActivity
                    )
                    RvInquiryTypeList.adapter = itemAdapter
                } else {
                    arrayListInquirySubTypeNew = arrayListInquirySubType
                    val itemAdapter = InquirySubTypeListAdapter(
                        this@InquirySubTypeListActivity,
                        arrayListInquirySubTypeNew!!,
                        this@InquirySubTypeListActivity
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
                arrayListInquirySubTypeNew = arrayListInquirySubType
                val itemAdapter = InquirySubTypeListAdapter(
                    this@InquirySubTypeListActivity,
                    arrayListInquirySubTypeNew!!,
                    this@InquirySubTypeListActivity
                )
                RvInquiryTypeList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(
                    this@InquirySubTypeListActivity, R.anim.searchview_close_anim
                )
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(
                    this@InquirySubTypeListActivity, R.anim.searchview_open_anim
                )
            }

        })

        refreshLayout.setOnRefreshListener {
            callManageInquirySubType()
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

            R.id.imgAddInquirySubType -> {
                preventTwoClick(v)
                val intent = Intent(this@InquirySubTypeListActivity, AddInquirySubTypeActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_ADD)
                startActivity(intent)
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when (type) {

            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddInquirySubTypeActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra("InquiryTypeGUID", arrayListInquirySubTypeNew!![position].InquirySubTypeGUID)
                intent.putExtra("InquiryTypeID", arrayListInquirySubTypeNew!![position].InquiryTypeID!!.toInt())
                startActivity(intent)
            }

            103 -> {
                preventTwoClick(view)
                AwesomeDialog.build(this).title("Warning !!!")
                    .body("Are you sure want to delete this Inquiry Sub Type?")
                    .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }.onPositive("Yes") {
                        CallDeleteAPI(arrayListInquirySubTypeNew!![position].InquirySubTypeGUID!!)
                    }
            }
        }
    }

    private fun CallDeleteAPI(GUID: String) {

        var jsonObject = JSONObject()
        jsonObject.put("InquirySubTypeGUID", GUID)
        jsonObject.put("OperationType", AppConstant.DELETE)

        val call =
            ApiUtils.apiInterface.ManageInquirySubType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<InquirySubTypeResponse> {
            override fun onResponse(
                call: Call<InquirySubTypeResponse>, response: Response<InquirySubTypeResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        callManageInquirySubType()
                    }
                }
            }

            override fun onFailure(call: Call<InquirySubTypeResponse>, t: Throwable) {
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun callManageInquirySubType() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("OperationType", AppConstant.GETALL)

        val call =
            ApiUtils.apiInterface.ManageInquirySubType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<InquirySubTypeResponse> {
            override fun onResponse(
                call: Call<InquirySubTypeResponse>, response: Response<InquirySubTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListInquirySubType?.clear()
                        arrayListInquirySubTypeNew?.clear()
                        arrayListInquirySubType = response.body()?.Data!!
                        arrayListInquirySubTypeNew = arrayListInquirySubType

                        if (arrayListInquirySubTypeNew!!.size > 0) {
                            adapter = InquirySubTypeListAdapter(
                                this@InquirySubTypeListActivity,
                                arrayListInquirySubTypeNew!!,
                                this@InquirySubTypeListActivity
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

            override fun onFailure(call: Call<InquirySubTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }
}