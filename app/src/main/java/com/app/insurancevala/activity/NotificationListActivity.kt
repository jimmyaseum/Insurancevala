package com.app.insurancevala.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.UsersListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.UserModel
import com.app.insurancevala.model.response.UserResponse
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
import kotlinx.android.synthetic.main.activity_notification_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter : UsersListAdapter
    var arrayListUsers: ArrayList<UserModel>? = ArrayList()
    var arrayListUsersNew: ArrayList<UserModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun initializeView() {
        if (isOnline(this@NotificationListActivity)) {
            callManageUsers()
        } else {
            internetErrordialog(this@NotificationListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvNotification.layoutManager = manager

        arrayListUsers = ArrayList()
        adapter = UsersListAdapter(this, arrayListUsers!!,this@NotificationListActivity)

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@NotificationListActivity,refreshLayout)
            callManageUsers()
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
        }
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when(type) {

        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.INTENT_1001) {
            if (isOnline(this)) {
                callManageUsers()
            } else {
                internetErrordialog(this)
            }
        }
    }

    private fun CallDeleteAPI(GUID : String) {

        var jsonObject = JSONObject()
        jsonObject.put("ID", GUID)
        jsonObject.put("OperationType", AppConstant.DELETE)

        val call = ApiUtils.apiInterface.ManageUsers(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        callManageUsers()
                    }
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun callManageUsers() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)

        val call = ApiUtils.apiInterface.ManageUsers(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListUsers?.clear()
                        arrayListUsersNew?.clear()
                        arrayListUsers = response.body()?.Data!!
                        arrayListUsersNew = arrayListUsers

                        if(arrayListUsersNew!!.size > 0) {
                            adapter = UsersListAdapter(this@NotificationListActivity, arrayListUsersNew!!,this@NotificationListActivity)
                            RvNotification.adapter = adapter

                            shimmer.stopShimmer()
                            shimmer.gone()

                        } else {
                            Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                            shimmer.stopShimmer()
                            shimmer.gone()
                            FL.gone()
                            RLNoData.visible()
                        }
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        shimmer.stopShimmer()
                        shimmer.gone()
                        FL.gone()
                        RLNoData.visible()
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })

    }
}