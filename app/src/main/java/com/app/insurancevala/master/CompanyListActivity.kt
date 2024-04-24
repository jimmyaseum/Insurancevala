package com.app.insurancevala.master

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.master.CompanyListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.CompanyModel
import com.app.insurancevala.model.response.CompanyResponse
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
import kotlinx.android.synthetic.main.activity_company_type_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompanyListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: CompanyListAdapter
    var arrayListCompany: ArrayList<CompanyModel>? = ArrayList()
    var arrayListCompanyNew: ArrayList<CompanyModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_type_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun initializeView() {
        if (isOnline(this@CompanyListActivity)) {
            callManageCompany()
        } else {
            internetErrordialog(this@CompanyListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddCompany.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvCompanyList.layoutManager = manager

        arrayListCompany = ArrayList()
        adapter = CompanyListAdapter(this, arrayListCompany!!, this@CompanyListActivity)

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
                val arrItemsFinal1: ArrayList<CompanyModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListCompany!!) {
                        try {
                            if (model.CompanyName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                        }
                    }
                    arrayListCompanyNew = arrItemsFinal1
                    val itemAdapter = CompanyListAdapter(
                        this@CompanyListActivity, arrayListCompanyNew!!, this@CompanyListActivity
                    )
                    RvCompanyList.adapter = itemAdapter
                } else {
                    arrayListCompanyNew = arrayListCompany
                    val itemAdapter = CompanyListAdapter(
                        this@CompanyListActivity, arrayListCompanyNew!!, this@CompanyListActivity
                    )
                    RvCompanyList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListCompanyNew = arrayListCompany
                val itemAdapter = CompanyListAdapter(
                    this@CompanyListActivity, arrayListCompanyNew!!, this@CompanyListActivity
                )
                RvCompanyList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(
                    this@CompanyListActivity, R.anim.searchview_close_anim
                )
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(
                    this@CompanyListActivity, R.anim.searchview_open_anim
                )
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@CompanyListActivity,refreshLayout)
            searchView.closeSearch()
            callManageCompany()
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

            R.id.imgAddCompany -> {
                preventTwoClick(v)
                val intent = Intent(this@CompanyListActivity, AddCompanyActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_ADD)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, Status: Int) {
        when (Status) {

            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddCompanyActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra("CompanyID", arrayListCompanyNew!![position].ID)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }

            103 -> {
                preventTwoClick(view)
                AwesomeDialog.build(this).title("Warning !!!")
                    .body("Are you sure want to delete this Company Type?")
                    .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }.onPositive("Yes") {
                        CallDeleteAPI(arrayListCompanyNew!![position].ID!!)
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
                callManageCompany()
            } else {
                internetErrordialog(this)
            }
        }
    }

    private fun CallDeleteAPI(ID: Int) {

        val jsonObject = JSONObject()
        jsonObject.put("ID", ID)

        val call = ApiUtils.apiInterface.ManageCompanyDelete(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>, response: Response<CommonResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        callManageCompany()
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun callManageCompany() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("CompanyName", "")

        val call = ApiUtils.apiInterface.ManageCompanyFindAll(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CompanyResponse> {
            override fun onResponse(
                call: Call<CompanyResponse>, response: Response<CompanyResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListCompany?.clear()
                        arrayListCompanyNew?.clear()
                        arrayListCompany = response.body()?.Data!!
                        arrayListCompanyNew = arrayListCompany

                        if (arrayListCompanyNew!!.size > 0) {
                            adapter = CompanyListAdapter(
                                this@CompanyListActivity,
                                arrayListCompanyNew!!,
                                this@CompanyListActivity
                            )
                            RvCompanyList.adapter = adapter

                            shimmer.stopShimmer()
                            shimmer.gone()
                            FL.visible()
                            RLNoData.gone()
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

            override fun onFailure(call: Call<CompanyResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }
}