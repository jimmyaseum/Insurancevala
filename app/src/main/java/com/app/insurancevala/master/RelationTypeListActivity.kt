package com.app.insurancevala.master

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.master.RelationTypeListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.RelationTypeModel
import com.app.insurancevala.model.response.RelationTypeResponse
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
import kotlinx.android.synthetic.main.activity_relation_type_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RelationTypeListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: RelationTypeListAdapter
    var arrayListRelationType: ArrayList<RelationTypeModel>? = ArrayList()
    var arrayListRelationTypeNew: ArrayList<RelationTypeModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relation_type_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun onRestart() {
        super.onRestart()
        searchView.closeSearch()
        if (isOnline(this)) {
            callManageRelationType()
        } else {
            internetErrordialog(this)
        }
    }

    override fun initializeView() {
        if (isOnline(this@RelationTypeListActivity)) {
            callManageRelationType()
        } else {
            internetErrordialog(this@RelationTypeListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddRelationType.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvRelationTypeList.layoutManager = manager

        arrayListRelationType = ArrayList()
        adapter = RelationTypeListAdapter(this, arrayListRelationType!!, this@RelationTypeListActivity)

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
                val arrItemsFinal1: ArrayList<RelationTypeModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListRelationType!!) {
                        try {
                            if (model.RelationName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                        }
                    }
                    arrayListRelationTypeNew = arrItemsFinal1
                    val itemAdapter = RelationTypeListAdapter(
                        this@RelationTypeListActivity, arrayListRelationTypeNew!!, this@RelationTypeListActivity
                    )
                    RvRelationTypeList.adapter = itemAdapter
                } else {
                    arrayListRelationTypeNew = arrayListRelationType
                    val itemAdapter = RelationTypeListAdapter(
                        this@RelationTypeListActivity, arrayListRelationTypeNew!!, this@RelationTypeListActivity
                    )
                    RvRelationTypeList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListRelationTypeNew = arrayListRelationType
                val itemAdapter = RelationTypeListAdapter(
                    this@RelationTypeListActivity, arrayListRelationTypeNew!!, this@RelationTypeListActivity
                )
                RvRelationTypeList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(
                    this@RelationTypeListActivity, R.anim.searchview_close_anim
                )
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(
                    this@RelationTypeListActivity, R.anim.searchview_open_anim
                )
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@RelationTypeListActivity,refreshLayout)
            searchView.closeSearch()
            callManageRelationType()
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

            R.id.imgAddRelationType -> {
                preventTwoClick(v)
                val intent = Intent(this@RelationTypeListActivity, AddRelationTypeActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_ADD)
                startActivity(intent)
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, Status: Int) {
        when (Status) {

            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddRelationTypeActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra(
                    "RelationTypeGUID", arrayListRelationTypeNew!![position].RelationGUID
                )
                startActivity(intent)
            }

            103 -> {
                preventTwoClick(view)
                AwesomeDialog.build(this).title("Warning !!!")
                    .body("Are you sure want to delete this Relation Type?")
                    .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }.onPositive("Yes") {
                        CallDeleteAPI(arrayListRelationTypeNew!![position].RelationGUID!!)
                    }
            }
        }
    }

    private fun CallDeleteAPI(GUID: String) {

        var jsonObject = JSONObject()
        jsonObject.put("RelationGUID", GUID)

        val call = ApiUtils.apiInterface.ManageRelationDelete(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>, response: Response<CommonResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        callManageRelationType()
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

    private fun callManageRelationType() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("RelationName", "")

        val call = ApiUtils.apiInterface.ManageRelationFindAll(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<RelationTypeResponse> {
            override fun onResponse(
                call: Call<RelationTypeResponse>, response: Response<RelationTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListRelationType?.clear()
                        arrayListRelationTypeNew?.clear()
                        arrayListRelationType = response.body()?.Data!!
                        arrayListRelationTypeNew = arrayListRelationType

                        if (arrayListRelationTypeNew!!.size > 0) {
                            adapter = RelationTypeListAdapter(
                                this@RelationTypeListActivity,
                                arrayListRelationTypeNew!!,
                                this@RelationTypeListActivity
                            )
                            RvRelationTypeList.adapter = adapter

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

            override fun onFailure(call: Call<RelationTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }
}