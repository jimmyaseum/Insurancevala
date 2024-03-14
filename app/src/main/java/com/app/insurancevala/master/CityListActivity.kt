package com.app.insurancevala.master

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.master.CityListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.CityModel
import com.app.insurancevala.model.response.CityResponse
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
import kotlinx.android.synthetic.main.activity_city_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CityListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: CityListAdapter
    var arrayListCity: ArrayList<CityModel>? = ArrayList()
    var arrayListCityNew: ArrayList<CityModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceCity: Bundle?) {
        super.onCreate(savedInstanceCity)
        setContentView(R.layout.activity_city_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun onRestart() {
        super.onRestart()
        searchView.closeSearch()
        if (isOnline(this)) {
            callManageCity()
        } else {
            internetErrordialog(this)
        }
    }

    override fun initializeView() {
        if (isOnline(this@CityListActivity)) {
            callManageCity()
        } else {
            internetErrordialog(this@CityListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddCity.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvCityList.layoutManager = manager

        arrayListCity = ArrayList()
        adapter = CityListAdapter(this, arrayListCity!!, this@CityListActivity)

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
                val arrItemsFinal1: ArrayList<CityModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListCity!!) {
                        try {
                            if (model.CityName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                        }
                    }
                    arrayListCityNew = arrItemsFinal1
                    val itemAdapter = CityListAdapter(
                        this@CityListActivity, arrayListCityNew!!, this@CityListActivity
                    )
                    RvCityList.adapter = itemAdapter
                } else {
                    arrayListCityNew = arrayListCity
                    val itemAdapter = CityListAdapter(
                        this@CityListActivity, arrayListCityNew!!, this@CityListActivity
                    )
                    RvCityList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListCityNew = arrayListCity
                val itemAdapter = CityListAdapter(
                    this@CityListActivity, arrayListCityNew!!, this@CityListActivity
                )
                RvCityList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(
                    this@CityListActivity, R.anim.searchview_close_anim
                )
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(
                    this@CityListActivity, R.anim.searchview_open_anim
                )
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@CityListActivity,refreshLayout)
            searchView.closeSearch()
            callManageCity()
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

            R.id.imgAddCity -> {
                preventTwoClick(v)
                val intent = Intent(this@CityListActivity, AddCityActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_ADD)
                startActivity(intent)
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, Status: Int) {
        when (Status) {

            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddCityActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra("CityGUID", arrayListCityNew!![position].CityGUID)
                intent.putExtra("StateID", arrayListCityNew!![position].StateID)
                intent.putExtra("CountryID", arrayListCityNew!![position].CountryID)
                startActivity(intent)
            }

            103 -> {
                preventTwoClick(view)
                AwesomeDialog.build(this).title("Warning !!!")
                    .body("Are you sure want to delete this City?")
                    .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }.onPositive("Yes") {
                        CallDeleteAPI(arrayListCityNew!![position].CityGUID!!)
                    }
            }
        }
    }

    private fun CallDeleteAPI(GUID: String) {

        var jsonObject = JSONObject()
        jsonObject.put("CityGUID", GUID)

        val call = ApiUtils.apiInterface.ManageCityDelete(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>, response: Response<CommonResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        callManageCity()
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

    private fun callManageCity() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("CityName", "")

        val call = ApiUtils.apiInterface.ManageCityFindAll(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CityResponse> {
            override fun onResponse(
                call: Call<CityResponse>, response: Response<CityResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListCity?.clear()
                        arrayListCityNew?.clear()
                        arrayListCity = response.body()?.Data!!
                        arrayListCityNew = arrayListCity

                        if (arrayListCityNew!!.size > 0) {
                            adapter = CityListAdapter(
                                this@CityListActivity,
                                arrayListCityNew!!,
                                this@CityListActivity
                            )
                            RvCityList.adapter = adapter

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

            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }
}