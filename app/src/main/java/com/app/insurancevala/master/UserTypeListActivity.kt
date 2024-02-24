package com.app.insurancevala.master

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.master.UserTypeListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.UserTypeModel
import com.app.insurancevala.model.response.UserTypeResponse
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
import kotlinx.android.synthetic.main.activity_user_type_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserTypeListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: UserTypeListAdapter
    var arrayListUserType: ArrayList<UserTypeModel>? = ArrayList()
    var arrayListUserTypeNew: ArrayList<UserTypeModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_type_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun onRestart() {
        super.onRestart()
        searchView.closeSearch()
        if (isOnline(this)) {
            callManageUserType()
        } else {
            internetErrordialog(this)
        }
    }

    override fun initializeView() {
        if (isOnline(this@UserTypeListActivity)) {
            callManageUserType()
        } else {
            internetErrordialog(this@UserTypeListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddUserType.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvUserTypeList.layoutManager = manager

        arrayListUserType = ArrayList()
        adapter = UserTypeListAdapter(this, arrayListUserType!!, this@UserTypeListActivity)

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
                val arrItemsFinal1: ArrayList<UserTypeModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListUserType!!) {
                        try {
                            if (model.UserType!!.toLowerCase()
                                    .contains(strSearch.toLowerCase())
                            ) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                        }
                    }
                    arrayListUserTypeNew = arrItemsFinal1
                    val itemAdapter = UserTypeListAdapter(
                        this@UserTypeListActivity,
                        arrayListUserTypeNew!!,
                        this@UserTypeListActivity
                    )
                    RvUserTypeList.adapter = itemAdapter
                } else {
                    arrayListUserTypeNew = arrayListUserType
                    val itemAdapter = UserTypeListAdapter(
                        this@UserTypeListActivity,
                        arrayListUserTypeNew!!,
                        this@UserTypeListActivity
                    )
                    RvUserTypeList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListUserTypeNew = arrayListUserType
                val itemAdapter = UserTypeListAdapter(
                    this@UserTypeListActivity,
                    arrayListUserTypeNew!!,
                    this@UserTypeListActivity
                )
                RvUserTypeList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(
                    this@UserTypeListActivity, R.anim.searchview_close_anim
                )
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(
                    this@UserTypeListActivity, R.anim.searchview_open_anim
                )
            }

        })

        refreshLayout.setOnRefreshListener {
            callManageUserType()
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

            R.id.imgAddUserType -> {
                preventTwoClick(v)
                val intent =
                    Intent(this@UserTypeListActivity, AddUserTypeActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_ADD)
                startActivity(intent)
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when (type) {

            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddUserTypeActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra(
                    "UserTypeGUID", arrayListUserTypeNew!![position].UserTypeGUID
                )
                startActivity(intent)
            }

            103 -> {
                preventTwoClick(view)
                AwesomeDialog.build(this).title("Warning !!!")
                    .body("Are you sure want to delete this User Type?")
                    .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }.onPositive("Yes") {
                        CallDeleteAPI(arrayListUserTypeNew!![position].UserTypeGUID!!)
                    }
            }
        }
    }

    private fun CallDeleteAPI(GUID: String) {

        var jsonObject = JSONObject()
        jsonObject.put("UserTypeGUID", GUID)
        jsonObject.put("OperationType", AppConstant.DELETE)

        val call =
            ApiUtils.apiInterface.ManageUserType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserTypeResponse> {
            override fun onResponse(
                call: Call<UserTypeResponse>, response: Response<UserTypeResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        callManageUserType()
                    }
                }
            }

            override fun onFailure(call: Call<UserTypeResponse>, t: Throwable) {
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun callManageUserType() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("OperationType", AppConstant.GETALL)

        val call =
            ApiUtils.apiInterface.ManageUserType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserTypeResponse> {
            override fun onResponse(
                call: Call<UserTypeResponse>, response: Response<UserTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListUserType?.clear()
                        arrayListUserTypeNew?.clear()
                        arrayListUserType = response.body()?.Data!!
                        arrayListUserTypeNew = arrayListUserType

                        if (arrayListUserTypeNew!!.size > 0) {
                            adapter = UserTypeListAdapter(
                                this@UserTypeListActivity,
                                arrayListUserTypeNew!!,
                                this@UserTypeListActivity
                            )
                            RvUserTypeList.adapter = adapter

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

            override fun onFailure(call: Call<UserTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }
}