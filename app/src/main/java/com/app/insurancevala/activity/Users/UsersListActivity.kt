package com.app.insurancevala.activity.Users

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
import kotlinx.android.synthetic.main.activity_users_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter : UsersListAdapter
    var arrayListUsers: ArrayList<UserModel>? = ArrayList()
    var arrayListUsersNew: ArrayList<UserModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun initializeView() {
        if (isOnline(this@UsersListActivity)) {
            callManageUsers()
        } else {
            internetErrordialog(this@UsersListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddUser.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvUsersList.layoutManager = manager

        arrayListUsers = ArrayList()
        adapter = UsersListAdapter(this, arrayListUsers!!,this@UsersListActivity)

        imgSearch.setOnClickListener {
            if(searchView.isSearchOpen) {
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
                val arrItemsFinal1: ArrayList<UserModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListUsers!!) {
                        try {
                            if (model.FirstName!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.EmailID!!.toLowerCase().contains(strSearch.toLowerCase())) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception){
                        }
                    }
                    arrayListUsersNew = arrItemsFinal1
                    val itemAdapter = UsersListAdapter(this@UsersListActivity, arrayListUsersNew!!,this@UsersListActivity)
                    RvUsersList.adapter = itemAdapter
                } else {
                    arrayListUsersNew = arrayListUsers
                    val itemAdapter = UsersListAdapter( this@UsersListActivity, arrayListUsersNew!!, this@UsersListActivity)
                    RvUsersList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListUsersNew = arrayListUsers
                val itemAdapter = UsersListAdapter( this@UsersListActivity, arrayListUsersNew!!, this@UsersListActivity)
                RvUsersList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(this@UsersListActivity, R.anim.searchview_close_anim)
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(this@UsersListActivity, R.anim.searchview_open_anim)
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@UsersListActivity,refreshLayout)
            searchView.closeSearch()
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
            R.id.imgAddUser -> {
                preventTwoClick(v)
                val intent = Intent(this@UsersListActivity, AddUsersActivity::class.java)
                intent.putExtra("IsFrom","UserList")
                intent.putExtra(AppConstant.STATE,AppConstant.S_ADD)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when(type) {

            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddUsersActivity::class.java)
                intent.putExtra("IsFrom","UserList")
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra("UserGUID",arrayListUsersNew!![position].UserGUID)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
            103 -> {
                preventTwoClick(view)
                AwesomeDialog.build(this)
                    .title("Warning !!!")
                    .body("Are you sure want to delete this user?")
                    .icon(R.drawable.ic_delete)
                    .position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }
                    .onPositive("Yes") {
                        CallDeleteAPI(arrayListUsersNew!![position].UserGUID!!)
                    }
            }
            104 -> {
                preventTwoClick(view)
                if (!arrayListUsersNew!![position].UserImage.isNullOrEmpty()) {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(arrayListUsersNew!![position].UserImage)
                    )
                    startActivity(browserIntent)
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
                            adapter = UsersListAdapter(this@UsersListActivity, arrayListUsersNew!!,this@UsersListActivity)
                            RvUsersList.adapter = adapter

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