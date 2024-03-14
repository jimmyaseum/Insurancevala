package com.app.insurancevala.activity.Lead

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.TasksListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.SingleSelectionModel
import com.app.insurancevala.model.response.TasksModel
import com.app.insurancevala.model.response.TasksResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_tasks_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TasksListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter: TasksListAdapter
    var arrayListTasks: ArrayList<TasksModel>? = ArrayList()
    var arrayListTasksNew: ArrayList<TasksModel>? = ArrayList()
    var LeadID: Int? = null
    var ID: Int? = null

    val arrayListtaskstatus  = ArrayList<SingleSelectionModel>()
    var mTaskstatus : String = ""

    var ClosedTask : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        LeadID = intent.getIntExtra("LeadID",0)
        ID = intent.getIntExtra("ID",0)
        ClosedTask = intent.getStringExtra("ClosedTask")
        if (!ClosedTask.isNullOrEmpty()) {
            txtHearderText.text = "Closed Tasks"
        }
    }

    override fun initializeView() {

        arrayListtaskstatus.add(SingleSelectionModel(1, "Not Started", false))
        arrayListtaskstatus.add(SingleSelectionModel(2, "Deferred", false))
        arrayListtaskstatus.add(SingleSelectionModel(3, "In Progress", false))
        arrayListtaskstatus.add(SingleSelectionModel(4, "Completed", false))
        arrayListtaskstatus.add(SingleSelectionModel(5, "Waiting for input", false))


        if (isOnline(this@TasksListActivity)) {
            callManageTasks()
        } else {
            internetErrordialog(this@TasksListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        imgAddTasks.setOnClickListener(this)
        var manager = LinearLayoutManager(this)
        RvTaskList.layoutManager = manager

        arrayListTasks = ArrayList()
        adapter = TasksListAdapter(this,arrayListTasks!!, this@TasksListActivity)

        imgSearch.setOnClickListener {
            if(searchView.isSearchOpen) {
                searchView.closeSearch()
            } else {
                searchView.showSearch()
            }

        }
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
                val arrItemsFinal1: ArrayList<TasksModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListTasks!!) {
                        try {
                            if (model.Subject!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.TaskStatus!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.Priority!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.Description!!.toLowerCase().contains(strSearch.toLowerCase())) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception){
                        }
                    }
                    arrayListTasksNew = arrItemsFinal1
                    val itemAdapter = TasksListAdapter(this@TasksListActivity, arrayListTasksNew!!,this@TasksListActivity)
                    RvTaskList.adapter = itemAdapter
                } else {
                    arrayListTasksNew = arrayListTasks
                    val itemAdapter = TasksListAdapter( this@TasksListActivity, arrayListTasksNew!!, this@TasksListActivity)
                    RvTaskList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListTasksNew = arrayListTasks
                val itemAdapter = TasksListAdapter( this@TasksListActivity, arrayListTasksNew!!, this@TasksListActivity)
                RvTaskList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(this@TasksListActivity, R.anim.searchview_close_anim)
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(this@TasksListActivity, R.anim.searchview_open_anim)
            }

        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@TasksListActivity,refreshLayout)
            searchView.closeSearch()
            callManageTasks()
            refreshLayout.isRefreshing = false
        }
    }
    override fun onClick(v: View?) {
        hideKeyboard(this@TasksListActivity, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }
            R.id.imgAddTasks -> {
                preventTwoClick(v)
                val intent = Intent(this, AddTaskLogsActivity::class.java)
                intent.putExtra("ID",ID)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra(AppConstant.STATE,AppConstant.S_ADD)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
        }
    }
    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when(type) {
            101 -> {
                preventTwoClick(view)
                val intent = Intent(this, TasksDetailsActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("ID",ID)
                intent.putExtra("TaskGUID",arrayListTasksNew!![position].TaskGUID)
                startActivity(intent)
            }
            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddTaskLogsActivity::class.java)
                intent.putExtra(AppConstant.STATE,AppConstant.S_EDIT)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("ID",ID)
                intent.putExtra("TaskGUID",arrayListTasksNew!![position].TaskGUID)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
            103 -> {
                preventTwoClick(view)
                selectStatusDialog(arrayListTasksNew!![position].TaskGUID)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.INTENT_1001) {
            if (isOnline(this)) {
                callManageTasks()
            } else {
                internetErrordialog(this)
            }
        }
    }
    private fun callManageTasks() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("LeadID", LeadID)
        jsonObject.put("NBInquiryTypeID", ID)

        if(!ClosedTask.isNullOrEmpty()) {
            jsonObject.put("TaskStatus", ClosedTask)
        }
        val call = ApiUtils.apiInterface.ManageTasksFindAll(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<TasksResponse> {
            override fun onResponse(call: Call<TasksResponse>, response: Response<TasksResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListTasks?.clear()
                        arrayListTasksNew?.clear()
                        arrayListTasks = response.body()?.Data!!
                        arrayListTasksNew = arrayListTasks

                        if(arrayListTasksNew!!.size > 0) {
                            adapter = TasksListAdapter(this@TasksListActivity, arrayListTasksNew!!,this@TasksListActivity)
                            RvTaskList.adapter = adapter

                            shimmer.stopShimmer()
                            shimmer.gone()
                            FL.visible()
                            RLNoData.gone()

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

            override fun onFailure(call: Call<TasksResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })

    }

    private fun selectStatusDialog(taskGUID: String?) {
        var dialogSelectUserType = Dialog(this)
        dialogSelectUserType.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectUserType.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectUserType.window!!.attributes)

        dialogSelectUserType.window!!.attributes = lp
        dialogSelectUserType.setCancelable(true)
        dialogSelectUserType.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectUserType.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectUserType.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectUserType.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectUserType.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectUserType.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectUserType.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectUserType.dismiss()
        }

        txtid.text = "Select Task Status"
        edtSearchCustomer.gone()

        val itemAdapter = BottomSheetListAdapter(this, arrayListtaskstatus!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mTaskstatus = arrayListtaskstatus[pos].Name!!

                if(mTaskstatus != "") {
                    CallManageTaskStatusUpdate(taskGUID)
                    dialogSelectUserType.dismiss()
                } else {
                    dialogSelectUserType.dismiss()
                }
            }
        })

        rvDialogCustomer.adapter = itemAdapter
        dialogSelectUserType!!.show()
    }

    private fun CallManageTaskStatusUpdate(taskGUID: String?) {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("TaskGUID", taskGUID)
        jsonObject.put("TaskStatus", mTaskstatus)

        val call = ApiUtils.apiInterface.ManageTaskStatusUpdate(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        callManageTasks()
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

}