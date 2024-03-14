package com.app.insurancevala.master

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.master.CountryListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.CountryModel
import com.app.insurancevala.model.response.CountryResponse
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
import kotlinx.android.synthetic.main.activity_country_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CountryListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: CountryListAdapter
    var arrayListCountry: ArrayList<CountryModel>? = ArrayList()
    var arrayListCountryNew: ArrayList<CountryModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun onRestart() {
        super.onRestart()
        searchView.closeSearch()
        if (isOnline(this)) {
            callManageCountry()
        } else {
            internetErrordialog(this)
        }
    }

    override fun initializeView() {
        if (isOnline(this@CountryListActivity)) {
            callManageCountry()
        } else {
            internetErrordialog(this@CountryListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddCountry.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvCountryList.layoutManager = manager

        arrayListCountry = ArrayList()
        adapter = CountryListAdapter(this, arrayListCountry!!, this@CountryListActivity)

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
                val arrItemsFinal1: ArrayList<CountryModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListCountry!!) {
                        try {
                            if (model.CountryName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                        }
                    }
                    arrayListCountryNew = arrItemsFinal1
                    val itemAdapter = CountryListAdapter(
                        this@CountryListActivity, arrayListCountryNew!!, this@CountryListActivity
                    )
                    RvCountryList.adapter = itemAdapter
                } else {
                    arrayListCountryNew = arrayListCountry
                    val itemAdapter = CountryListAdapter(
                        this@CountryListActivity, arrayListCountryNew!!, this@CountryListActivity
                    )
                    RvCountryList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListCountryNew = arrayListCountry
                val itemAdapter = CountryListAdapter(
                    this@CountryListActivity, arrayListCountryNew!!, this@CountryListActivity
                )
                RvCountryList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(
                    this@CountryListActivity, R.anim.searchview_close_anim
                )
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(
                    this@CountryListActivity, R.anim.searchview_open_anim
                )
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@CountryListActivity,refreshLayout)
            searchView.closeSearch()
            callManageCountry()
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

            R.id.imgAddCountry -> {
                preventTwoClick(v)
                val intent = Intent(this@CountryListActivity, AddCountryActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_ADD)
                startActivity(intent)
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, Status: Int) {
        when (Status) {

            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddCountryActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra(
                    "CountryGUID", arrayListCountryNew!![position].CountryGUID
                )
                startActivity(intent)
            }

            103 -> {
                preventTwoClick(view)
                AwesomeDialog.build(this).title("Warning !!!")
                    .body("Are you sure want to delete this Country?")
                    .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }.onPositive("Yes") {
                        CallDeleteAPI(arrayListCountryNew!![position].CountryGUID!!)
                    }
            }
        }
    }

    private fun CallDeleteAPI(GUID: String) {

        var jsonObject = JSONObject()
        jsonObject.put("CountryGUID", GUID)

        val call = ApiUtils.apiInterface.ManageCountryDelete(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>, response: Response<CommonResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        callManageCountry()
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

    private fun callManageCountry() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("CountryName", "")

        val call = ApiUtils.apiInterface.ManageCountryFindAll(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CountryResponse> {
            override fun onResponse(
                call: Call<CountryResponse>, response: Response<CountryResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListCountry?.clear()
                        arrayListCountryNew?.clear()
                        arrayListCountry = response.body()?.Data!!
                        arrayListCountryNew = arrayListCountry

                        if (arrayListCountryNew!!.size > 0) {
                            adapter = CountryListAdapter(
                                this@CountryListActivity,
                                arrayListCountryNew!!,
                                this@CountryListActivity
                            )
                            RvCountryList.adapter = adapter

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

            override fun onFailure(call: Call<CountryResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }
}