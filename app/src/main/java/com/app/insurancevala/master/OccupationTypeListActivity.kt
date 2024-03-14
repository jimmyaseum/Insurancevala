package com.app.insurancevala.master

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.master.OccupationTypeListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.OccupationTypeModel
import com.app.insurancevala.model.response.OccupationTypeResponse
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
import kotlinx.android.synthetic.main.activity_occupation_type_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OccupationTypeListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: OccupationTypeListAdapter
    var arrayListOccupationType: ArrayList<OccupationTypeModel>? = ArrayList()
    var arrayListOccupationTypeNew: ArrayList<OccupationTypeModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_occupation_type_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun onRestart() {
        super.onRestart()
        searchView.closeSearch()
        if (isOnline(this)) {
            callManageOccupationType()
        } else {
            internetErrordialog(this)
        }
    }

    override fun initializeView() {
        if (isOnline(this@OccupationTypeListActivity)) {
            callManageOccupationType()
        } else {
            internetErrordialog(this@OccupationTypeListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddOccupationType.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvOccupationTypeList.layoutManager = manager

        arrayListOccupationType = ArrayList()
        adapter = OccupationTypeListAdapter(this, arrayListOccupationType!!, this@OccupationTypeListActivity)

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
                val arrItemsFinal1: ArrayList<OccupationTypeModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListOccupationType!!) {
                        try {
                            if (model.OccupationName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                        }
                    }
                    arrayListOccupationTypeNew = arrItemsFinal1
                    val itemAdapter = OccupationTypeListAdapter(
                        this@OccupationTypeListActivity, arrayListOccupationTypeNew!!, this@OccupationTypeListActivity
                    )
                    RvOccupationTypeList.adapter = itemAdapter
                } else {
                    arrayListOccupationTypeNew = arrayListOccupationType
                    val itemAdapter = OccupationTypeListAdapter(
                        this@OccupationTypeListActivity, arrayListOccupationTypeNew!!, this@OccupationTypeListActivity
                    )
                    RvOccupationTypeList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListOccupationTypeNew = arrayListOccupationType
                val itemAdapter = OccupationTypeListAdapter(
                    this@OccupationTypeListActivity, arrayListOccupationTypeNew!!, this@OccupationTypeListActivity
                )
                RvOccupationTypeList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(
                    this@OccupationTypeListActivity, R.anim.searchview_close_anim
                )
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(
                    this@OccupationTypeListActivity, R.anim.searchview_open_anim
                )
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@OccupationTypeListActivity,refreshLayout)
            searchView.closeSearch()
            callManageOccupationType()
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

            R.id.imgAddOccupationType -> {
                preventTwoClick(v)
                val intent = Intent(this@OccupationTypeListActivity, AddOccupationTypeActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_ADD)
                startActivity(intent)
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, Status: Int) {
        when (Status) {

            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddOccupationTypeActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra(
                    "OccupationTypeGUID", arrayListOccupationTypeNew!![position].OccupationGUID
                )
                startActivity(intent)
            }

            103 -> {
                preventTwoClick(view)
                AwesomeDialog.build(this).title("Warning !!!")
                    .body("Are you sure want to delete this Occupation Type?")
                    .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }.onPositive("Yes") {
                        CallDeleteAPI(arrayListOccupationTypeNew!![position].OccupationGUID!!)
                    }
            }
        }
    }

    private fun CallDeleteAPI(GUID: String) {

        var jsonObject = JSONObject()
        jsonObject.put("OccupationGUID", GUID)

        val call = ApiUtils.apiInterface.ManageOccupationDelete(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>, response: Response<CommonResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        callManageOccupationType()
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

    private fun callManageOccupationType() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("OccupationName", "")

        val call = ApiUtils.apiInterface.ManageOccupationFindAll(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<OccupationTypeResponse> {
            override fun onResponse(
                call: Call<OccupationTypeResponse>, response: Response<OccupationTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListOccupationType?.clear()
                        arrayListOccupationTypeNew?.clear()
                        arrayListOccupationType = response.body()?.Data!!
                        arrayListOccupationTypeNew = arrayListOccupationType

                        if (arrayListOccupationTypeNew!!.size > 0) {
                            adapter = OccupationTypeListAdapter(
                                this@OccupationTypeListActivity,
                                arrayListOccupationTypeNew!!,
                                this@OccupationTypeListActivity
                            )
                            RvOccupationTypeList.adapter = adapter

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

            override fun onFailure(call: Call<OccupationTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }
}