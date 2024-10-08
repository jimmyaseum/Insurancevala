package com.app.insurancevala.master

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.master.CategoryTypeListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.CategoryTypeModel
import com.app.insurancevala.model.response.CategoryTypeResponse
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
import kotlinx.android.synthetic.main.activity_category_type_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryTypeListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: CategoryTypeListAdapter
    var arrayListCategoryType: ArrayList<CategoryTypeModel>? = ArrayList()
    var arrayListCategoryTypeNew: ArrayList<CategoryTypeModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_type_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun onRestart() {
        super.onRestart()
        searchView.closeSearch()
        if (isOnline(this)) {
            callManageCategoryType()
        } else {
            internetErrordialog(this)
        }
    }

    override fun initializeView() {
        if (isOnline(this@CategoryTypeListActivity)) {
            callManageCategoryType()
        } else {
            internetErrordialog(this@CategoryTypeListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddCategoryType.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvCategoryTypeList.layoutManager = manager

        arrayListCategoryType = ArrayList()
        adapter = CategoryTypeListAdapter(this, arrayListCategoryType!!, this@CategoryTypeListActivity)

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
                val arrItemsFinal1: ArrayList<CategoryTypeModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListCategoryType!!) {
                        try {
                            if (model.CategoryName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                        }
                    }
                    arrayListCategoryTypeNew = arrItemsFinal1
                    val itemAdapter = CategoryTypeListAdapter(
                        this@CategoryTypeListActivity, arrayListCategoryTypeNew!!, this@CategoryTypeListActivity
                    )
                    RvCategoryTypeList.adapter = itemAdapter
                } else {
                    arrayListCategoryTypeNew = arrayListCategoryType
                    val itemAdapter = CategoryTypeListAdapter(
                        this@CategoryTypeListActivity, arrayListCategoryTypeNew!!, this@CategoryTypeListActivity
                    )
                    RvCategoryTypeList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListCategoryTypeNew = arrayListCategoryType
                val itemAdapter = CategoryTypeListAdapter(
                    this@CategoryTypeListActivity, arrayListCategoryTypeNew!!, this@CategoryTypeListActivity
                )
                RvCategoryTypeList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(
                    this@CategoryTypeListActivity, R.anim.searchview_close_anim
                )
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(
                    this@CategoryTypeListActivity, R.anim.searchview_open_anim
                )
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@CategoryTypeListActivity,refreshLayout)
            searchView.closeSearch()
            callManageCategoryType()
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

            R.id.imgAddCategoryType -> {
                preventTwoClick(v)
                val intent = Intent(this@CategoryTypeListActivity, AddCategoryTypeActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_ADD)
                startActivity(intent)
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, Status: Int) {
        when (Status) {

            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddCategoryTypeActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra(
                    "CategoryTypeGUID", arrayListCategoryTypeNew!![position].CategoryGUID
                )
                startActivity(intent)
            }

            103 -> {
                preventTwoClick(view)
                AwesomeDialog.build(this).title("Warning !!!")
                    .body("Are you sure want to delete this Category Type?")
                    .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }.onPositive("Yes") {
                        CallDeleteAPI(arrayListCategoryTypeNew!![position].CategoryGUID!!)
                    }
            }
        }
    }

    private fun CallDeleteAPI(GUID: String) {

        var jsonObject = JSONObject()
        jsonObject.put("CategoryGUID", GUID)

        val call = ApiUtils.apiInterface.ManageCategoryDelete(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>, response: Response<CommonResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        callManageCategoryType()
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

    private fun callManageCategoryType() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("CategoryName", "")

        val call = ApiUtils.apiInterface.ManageCategoryFindAll(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CategoryTypeResponse> {
            override fun onResponse(
                call: Call<CategoryTypeResponse>, response: Response<CategoryTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListCategoryType?.clear()
                        arrayListCategoryTypeNew?.clear()
                        arrayListCategoryType = response.body()?.Data!!
                        arrayListCategoryTypeNew = arrayListCategoryType

                        if (arrayListCategoryTypeNew!!.size > 0) {
                            adapter = CategoryTypeListAdapter(
                                this@CategoryTypeListActivity,
                                arrayListCategoryTypeNew!!,
                                this@CategoryTypeListActivity
                            )
                            RvCategoryTypeList.adapter = adapter

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

            override fun onFailure(call: Call<CategoryTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }
}