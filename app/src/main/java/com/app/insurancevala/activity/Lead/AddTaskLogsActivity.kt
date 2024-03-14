package com.app.insurancevala.activity.Lead

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.FilePickerBuilder
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.MultipleAttachmentListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetUsersListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.pojo.AttachmentModel
import com.app.insurancevala.model.pojo.DocumentsModel
import com.app.insurancevala.model.response.*
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.activity_add_task_logs.*
import kotlinx.android.synthetic.main.activity_add_task_logs.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddTaskLogsActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener, EasyPermissions.PermissionCallbacks {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var ID: Int? = null
    var LeadID: Int? = null
    var TaskGUID: String? = null
    var ReferenceGUID: String? = null

    // attachments
    var arrayListAttachment: ArrayList<DocumentsModel>? = null
    lateinit var adapter: MultipleAttachmentListAdapter
    val RC_FILE_PICKER_PERM = 900
    var ImagePaths = ArrayList<String>()

    //date
    val CalenderDate = Calendar.getInstance()
    var TaskDueDate: String = ""

    //followup date
    val CalenderFollowUpTaskDate = Calendar.getInstance()
    var FollowUpTaskDate: String = ""

    val arrayListtaskstatus  = ArrayList<SingleSelectionModel>()
    var mTaskstatus : String = ""

    val arrayListtaskpriority  = ArrayList<SingleSelectionModel>()
    var mTaskpriority : String = ""

    val arrayListrepeat = ArrayList<SingleSelectionModel>()
    var mTaskrepeat : String = ""
    var mTaskrepeatID : Int = 1

    val arrayListnotifyvia = ArrayList<SingleSelectionModel>()
    var mTasknotifyvia : String = ""
    var mTasknotifyviaID : Int = 1

    var arrayListUsers: ArrayList<UserModel>? = ArrayList()
    var mUsers: String = ""
    var mUsersID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task_logs)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()

    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
        ID = intent.getIntExtra("ID",0)
        LeadID = intent.getIntExtra("LeadID",0)
    }

    override fun initializeView() {

        if(state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Create Task"
        } else if(state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update Task"
        }

        setMasterData()
        SetInitListner()
    }

    private fun setDefaultDate() {
        val mDate = convertDateStringToString(getcurrentdate() , AppConstant.dd_MM_yyyy_HH_mm_ss,AppConstant.dd_LLL_yyyy)
        val mTime = convertDateStringToString(getcurrentdate() , AppConstant.dd_MM_yyyy_HH_mm_ss,AppConstant.HH_MM_AA_FORMAT)
        edtDueDate.setText(mDate)
        edtReminderDate.setText(mDate)

        TaskDueDate = convertDateStringToString(getcurrentdate() , AppConstant.dd_MM_yyyy_HH_mm_ss,AppConstant.yyyy_MM_dd_Dash).toString()
        FollowUpTaskDate = convertDateStringToString(getcurrentdate() , AppConstant.dd_MM_yyyy_HH_mm_ss,AppConstant.yyyy_MM_dd_Dash).toString()

    }

    private fun setMasterData() {

        arrayListtaskstatus.add(SingleSelectionModel(1, "Not Started", true))
        arrayListtaskstatus.add(SingleSelectionModel(2, "Deferred", false))
        arrayListtaskstatus.add(SingleSelectionModel(3, "In Progress", false))
        arrayListtaskstatus.add(SingleSelectionModel(4, "Completed", false))
        arrayListtaskstatus.add(SingleSelectionModel(5, "Waiting for input", false))

        arrayListtaskpriority.add(SingleSelectionModel(1, "High", true))
        arrayListtaskpriority.add(SingleSelectionModel(2, "Highest", false))
        arrayListtaskpriority.add(SingleSelectionModel(3, "Low", false))
        arrayListtaskpriority.add(SingleSelectionModel(4, "Lowest", false))
        arrayListtaskpriority.add(SingleSelectionModel(5, "Normal", false))

        arrayListrepeat.add(SingleSelectionModel(1, "None", true))
        arrayListrepeat.add(SingleSelectionModel(2, "Daily", false))
        arrayListrepeat.add(SingleSelectionModel(3, "Weekly", false))
        arrayListrepeat.add(SingleSelectionModel(4, "Monthly", false))
        arrayListrepeat.add(SingleSelectionModel(5, "Yearly", false))

        arrayListnotifyvia.add(SingleSelectionModel(1, "Email", true))
        arrayListnotifyvia.add(SingleSelectionModel(2, "Pop Up", false))
        arrayListnotifyvia.add(SingleSelectionModel(3, "Both", false))

        if(state.equals(AppConstant.S_ADD)) {
            if (isOnline(this)) {
                callManageUsers(0)
            } else {
                internetErrordialog(this@AddTaskLogsActivity)
            }
        } else if(state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update Task"

            if (isOnline(this)) {
                ID = intent.getIntExtra("ID",0)
                LeadID = intent.getIntExtra("LeadID",0)
                TaskGUID = intent.getStringExtra("TaskGUID")

                callManageTaskGUID()
            } else {
                internetErrordialog(this@AddTaskLogsActivity)
            }
        }
    }

    private fun SetInitListner() {

        // attachment
        rvAttachment.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvAttachment.isNestedScrollingEnabled = false

        arrayListAttachment = ArrayList()
        adapter = MultipleAttachmentListAdapter(this, true, arrayListAttachment, this)
        rvAttachment.adapter = adapter

        imgBack.setOnClickListener(this)
        txtSave.setOnClickListener(this)
        edtAttachments.setOnClickListener(this)
        cbIsReminder.setOnClickListener(this)

        edtDueDate.setOnClickListener(this)
        edtReminderDate.setOnClickListener(this)
        edtStatus.setOnClickListener(this)
        edtPriority.setOnClickListener(this)
        edtRepeat.setOnClickListener(this)
        edtNotifyvia.setOnClickListener(this)
        edtTaskOwner.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }
            R.id.txtSave -> {
                preventTwoClick(v)
                validation()
            }
            R.id.edtTaskOwner -> {
                preventTwoClick(v)
                if (arrayListUsers.isNullOrEmpty()) {
                    callManageUsers(1)
                } else {
                    selectUsersDialog()
                }
            }
            R.id.edtDueDate -> {
                preventTwoClick(v)
                val dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        CalenderDate.set(Calendar.YEAR, year)
                        CalenderDate.set(Calendar.MONTH, monthOfYear)
                        CalenderDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        TaskDueDate = SimpleDateFormat(AppConstant.yyyy_MM_dd_Dash, Locale.US).format(CalenderDate.time)

                        val selecteddate = SimpleDateFormat(AppConstant.dd_MM_yyyy_HH_mm_ss, Locale.US).format(CalenderDate.time)
                        val mDate = convertDateStringToString(selecteddate , AppConstant.dd_MM_yyyy_HH_mm_ss,AppConstant.dd_LLL_yyyy)
                        edtDueDate.setText(mDate)
                    },
                    CalenderDate.get(Calendar.YEAR),
                    CalenderDate.get(Calendar.MONTH),
                    CalenderDate.get(Calendar.DAY_OF_MONTH)
                )
//                dpd.datePicker.minDate = System.currentTimeMillis() - 1000
//                dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
                dpd.show()
            }
            R.id.edtStatus -> {
                preventTwoClick(v)
                selectStatusDialog()
            }
            R.id.edtPriority -> {
                preventTwoClick(v)
                selectPriorityDialog()
            }
            R.id.edtRepeat -> {
                preventTwoClick(v)
                selectRepeatDialog()
            }
            R.id.edtNotifyvia -> {
                preventTwoClick(v)
                selectNotifyviaDialog()
            }

            R.id.edtAttachments -> {
                preventTwoClick(v)
                showAttachmentBottomSheetDialog()
            }
            R.id.cbIsReminder -> {
                if (cbIsReminder.isChecked) {
                    LLreminder.visible()
                } else {
                    LLreminder.gone()
                }
            }
            R.id.edtReminderDate -> {
                preventTwoClick(v)

                val dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        CalenderFollowUpTaskDate.set(Calendar.YEAR, year)
                        CalenderFollowUpTaskDate.set(Calendar.MONTH, monthOfYear)
                        CalenderFollowUpTaskDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        FollowUpTaskDate = SimpleDateFormat(AppConstant.yyyy_MM_dd_Dash, Locale.US).format(CalenderFollowUpTaskDate.time)

                        val selecteddate = SimpleDateFormat(AppConstant.dd_MM_yyyy_HH_mm_ss, Locale.US).format(CalenderFollowUpTaskDate.time)
                        val mDate = convertDateStringToString(selecteddate , AppConstant.dd_MM_yyyy_HH_mm_ss,AppConstant.dd_LLL_yyyy)
                        edtReminderDate.setText(mDate)
                    },
                    CalenderFollowUpTaskDate.get(Calendar.YEAR),
                    CalenderFollowUpTaskDate.get(Calendar.MONTH),
                    CalenderFollowUpTaskDate.get(Calendar.DAY_OF_MONTH)
                )
//                dpd.datePicker.minDate = System.currentTimeMillis() - 1000
//                dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
                dpd.show()
            }
        }
    }

    private fun validation() {

        when (isValidate()) {
            true -> {
                if (isOnline(this)) {
                    ManageCreateTaskAPI()
                } else {
                    internetErrordialog(this@AddTaskLogsActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isValidate = true

        if (edtSubject.text.isEmpty()) {
            edtSubject.setError("Enter Subject",errortint(this))
            isValidate = false
        }
        if (edtDescription.text.isEmpty()) {
            edtDescription.setError("Enter Description",errortint(this))
            isValidate = false
        }
        if (edtDueDate.text.isEmpty()) {
            edtDueDate.setError("Select Due Date",errortint(this))
            isValidate = false
        }
        if (edtStatus.text.isEmpty()) {
            edtStatus.setError("Select Status",errortint(this))
            isValidate = false
        }
        if (edtPriority.text.isEmpty()) {
            edtPriority.setError("Select Priority",errortint(this))
            isValidate = false
        }
        if(cbIsReminder.isChecked) {
            if (edtReminderDate.text.isEmpty()) {
                edtReminderDate.setError("Select Reminder Date",errortint(this))
                isValidate = false
            }
        }

        return isValidate
    }

    private fun ManageCreateTaskAPI() {

        showProgress()

        var isreminder = false

        val jsonObject = JSONObject()
        jsonObject.put("NBInquiryTypeID", ID)
        jsonObject.put("LeadID", LeadID)
        jsonObject.put("TaskOwnerID", mUsersID)
        jsonObject.put("Subject", edtSubject.text.toString().trim())
        jsonObject.put("DueDate", TaskDueDate)
        jsonObject.put("TaskStatus", mTaskstatus)
        jsonObject.put("Priority", mTaskpriority)
        jsonObject.put("Description", edtDescription.text.toString().trim())

        if(cbIsReminder.isChecked) {
            isreminder = true
            jsonObject.put("ReminderDate",FollowUpTaskDate)
            jsonObject.put("Repeat", mTaskrepeatID)
            jsonObject.put("NotifyVia", mTasknotifyviaID)
        } else {
            isreminder = false
        }
        jsonObject.put("IsReminder", isreminder)
        jsonObject.put("IsActive", true)

        if(state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("TaskGUID", TaskGUID)
        }

        if(state.equals(AppConstant.S_ADD)) {
            val call = ApiUtils.apiInterface.ManageTasksInsert(getRequestJSONBody(jsonObject.toString()))
            call.enqueue(object : Callback<RefGUIDResponse> {
                override fun onResponse(call: Call<RefGUIDResponse>, response: Response<RefGUIDResponse>) {
                    hideProgress()
                    if (response.code() == 200) {
                        if (response.body()?.Status == 201) {

                            Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()

                            ReferenceGUID = response.body()?.Data!!.ReferenceGUID

                            if(arrayListAttachment!!.size > 0) {
                                CallUploadDocuments(ReferenceGUID)
                            } else {
                                Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                                val intent = Intent()
                                setResult(RESULT_OK, intent)
                                finish()
                            }

                        }  else {
                            Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                            val intent = Intent()
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<RefGUIDResponse>, t: Throwable) {
                    hideProgress()
                    Snackbar.make(
                        layout,
                        getString(R.string.error_failed_to_connect),
                        Snackbar.LENGTH_LONG
                    ).show()
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                }
            })
        } else {
            val call = ApiUtils.apiInterface.ManageTasksUpdate(getRequestJSONBody(jsonObject.toString()))
            call.enqueue(object : Callback<CommonResponse> {
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    hideProgress()
                    if (response.code() == 200) {
                        if (response.body()!!.Status == 200 && state!!.equals(AppConstant.S_EDIT)) {
                            Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                            val intent = Intent()
                            setResult(RESULT_OK, intent)
                            finish()
                        } else {
                            Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                            val intent = Intent()
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    hideProgress()
                    Snackbar.make(
                        layout,
                        getString(R.string.error_failed_to_connect),
                        Snackbar.LENGTH_LONG
                    ).show()
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                }
            })
        }
    }

    private fun callManageUsers(mode: Int) {
        if (mode == 1) {
            showProgress()
        }

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(this)
        }
        val Userid = sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_ID)!!

        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageUsers(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListUsers = response.body()?.Data!!

                        if(arrayListUsers!!.size > 0) {
                            for(i in 0 until arrayListUsers!!.size) {
                                if(state.equals(AppConstant.S_ADD)) {
                                    if (arrayListUsers!![i].ID == Userid.toInt()) {
                                        arrayListUsers!![i].IsSelected = true
                                        mUsersID = arrayListUsers!![i].ID!!
                                        mUsers = arrayListUsers!![i].FirstName!! + " " + arrayListUsers!![i].LastName!!
                                        edtTaskOwner.setText(mUsers)
                                        edtTaskOwner.setError(null)
                                    }
                                }
                                else if(state.equals(AppConstant.S_EDIT)) {
                                    if(arrayListUsers!![i].ID == mUsersID) {
                                        arrayListUsers!![i].IsSelected = true
                                        mUsersID = arrayListUsers!![i].ID!!
                                        mUsers = arrayListUsers!![i].FirstName!! + " "+ arrayListUsers!![i].LastName!!
                                        edtTaskOwner.setText(mUsers)
                                        edtTaskOwner.setError(null)
                                    }
                                }
                            }
                        }

                        if (mode == 1) {
                            selectUsersDialog()
                        }
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }
    private fun selectUsersDialog() {
        var dialogSelectUsers = Dialog(this)
        dialogSelectUsers.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectUsers.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectUsers.window!!.attributes)

        dialogSelectUsers.window!!.attributes = lp
        dialogSelectUsers.setCancelable(true)
        dialogSelectUsers.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectUsers.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectUsers.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectUsers.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectUsers.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectUsers.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectUsers.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectUsers.dismiss()
        }

        txtid.text = "Select Task Owner"

        val itemAdapter = BottomSheetUsersListAdapter(this, arrayListUsers!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mUsersID = arrayListUsers!![pos].ID!!
                mUsers = arrayListUsers!![pos].FirstName!! + " "+ arrayListUsers!![pos].LastName!!
                edtTaskOwner.setText(mUsers)
                edtTaskOwner.setError(null)
                dialogSelectUsers!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if(arrayListUsers!!.size > 6) {
            edtSearchCustomer.visible()
        }
        else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<UserModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListUsers!!) {
                        if (model.FirstName!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                            model.LastName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetUsersListAdapter(this@AddTaskLogsActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mUsersID = arrItemsFinal1!![pos].ID!!
                            mUsers = arrItemsFinal1!![pos].FirstName!! + " "+ arrItemsFinal1!![pos].LastName!!
                            edtTaskOwner.setText(mUsers)
                            edtTaskOwner.setError(null)
                            dialogSelectUsers!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetUsersListAdapter(this@AddTaskLogsActivity, arrayListUsers!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mUsersID = arrayListUsers!![pos].ID!!
                            mUsers = arrayListUsers!![pos].FirstName!! + " "+ arrayListUsers!![pos].LastName!!
                            edtTaskOwner.setText(mUsers)
                            edtTaskOwner.setError(null)
                            dialogSelectUsers!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectUsers!!.show()
    }

    private fun callManageTaskGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("TaskGUID", TaskGUID)

        val call = ApiUtils.apiInterface.ManageTasksFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<TasksByGUIDResponse> {
            override fun onResponse(call: Call<TasksByGUIDResponse>, response: Response<TasksByGUIDResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListLead = response.body()?.Data!!
                        setAPIData(arrayListLead)
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<TasksByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun setAPIData(model: TasksModel) {

        if(model.Subject != null && model.Subject != "") {
            edtSubject.setText(model.Subject)
        }
        if(model.Description != null && model.Description != "") {
            edtDescription.setText(model.Description)
        }
        if(model.TaskOwnerID != null && model.TaskOwnerID != 0) {
            mUsersID = model.TaskOwnerID.toInt()
            callManageUsers(0)
        }

        if(model.DueDate != null && model.DueDate != "") {
            edtDueDate.setText(model.DueDate)
            TaskDueDate = convertDateStringToString(model.DueDate , AppConstant.dd_MM_yyyy_Slash,AppConstant.yyyy_MM_dd_Dash).toString()
        }

        if(model.TaskStatus != null && model.TaskStatus != "") {
            edtStatus.setText(model.TaskStatus)
            mTaskstatus = model.TaskStatus
            for(i in 0 until arrayListtaskstatus.size) {
                if(arrayListtaskstatus[i].Name == mTaskstatus) {
                    arrayListtaskstatus[i].IsSelected = true
                }
            }
        }

        if(model.Priority != null && model.Priority != "") {
            edtPriority.setText(model.Priority)
            mTaskpriority = model.Priority
            for(i in 0 until arrayListtaskpriority.size) {
                if(arrayListtaskpriority[i].Name == mTaskpriority) {
                    arrayListtaskpriority[i].IsSelected = true
                }
            }
        }

        if(model.IsReminder != null) {
            if(model.IsReminder) {
                cbIsReminder.isChecked = true
                LLreminder.visible()
            } else {
                cbIsReminder.isChecked = false
                LLreminder.gone()
            }
        }

        if(model.Repeat != null && model.Repeat != 0) {
            mTaskrepeatID = model.Repeat
            arrayListrepeat[mTaskrepeatID-1].IsSelected = true
            if(model.Repeat == 1) {
                mTaskrepeat = "None"
            } else if(model.Repeat == 2) {
                mTaskrepeat = "Daily"
            } else if(model.Repeat == 3) {
                mTaskrepeat = "Weekly"
            } else if(model.Repeat == 4) {
                mTaskrepeat = "Monthly"
            } else if(model.Repeat == 5) {
                mTaskrepeat = "Yearly"
            }
            edtRepeat.setText(mTaskrepeat)
        }

        if(model.NotifyVia != null && model.NotifyVia != 0) {
            mTasknotifyviaID = model.NotifyVia
            arrayListnotifyvia[mTasknotifyviaID-1].IsSelected = true
            if(model.NotifyVia == 1) {
                mTasknotifyvia = "Email"
            } else if(model.NotifyVia == 2) {
                mTasknotifyvia = "Pop Up"
            } else if(model.NotifyVia == 3) {
                mTasknotifyvia = "Both"
            }
            edtNotifyvia.setText(mTasknotifyvia)
        }

        if(model.ReminderDate != null && model.ReminderDate != "") {
            edtReminderDate.setText(model.ReminderDate)
            FollowUpTaskDate = convertDateStringToString(model.ReminderDate , AppConstant.dd_MM_yyyy_Slash,AppConstant.yyyy_MM_dd_Dash).toString()
        }

        txtAttachments.gone()
        edtAttachments.gone()
        viewAttachments.gone()
        if(!model.TasksAttachmentList.isNullOrEmpty()) {
            arrayListAttachment = ArrayList()
            arrayListAttachment = model.TasksAttachmentList
            adapter = MultipleAttachmentListAdapter(this, false, arrayListAttachment, this)

            txtAttachments.visible()
            rvAttachment.adapter = adapter
        }
    }

    private fun selectStatusDialog() {
        var dialogSelectStatus = Dialog(this)
        dialogSelectStatus.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectStatus.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectStatus.window!!.attributes)

        dialogSelectStatus.window!!.attributes = lp
        dialogSelectStatus.setCancelable(true)
        dialogSelectStatus.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectStatus.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectStatus.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectStatus.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectStatus.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectStatus.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectStatus.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectStatus.dismiss()
        }

        txtid.text = "Select Task Status"

        val itemAdapter = BottomSheetListAdapter(this, arrayListtaskstatus!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {
                itemAdapter.updateItem(pos)
                mTaskstatus = arrayListtaskstatus[pos].Name!!
                edtStatus.setText(mTaskstatus)
                dialogSelectStatus!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if(arrayListtaskstatus!!.size > 6) {
            edtSearchCustomer.visible()
        } else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<SingleSelectionModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListtaskstatus!!) {
                        if (model.Name!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetListAdapter(this@AddTaskLogsActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mTaskstatus = arrItemsFinal1[pos].Name!!
                            edtStatus.setText(mTaskstatus)
                            dialogSelectStatus!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetListAdapter(this@AddTaskLogsActivity, arrayListtaskstatus!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mTaskstatus = arrayListtaskstatus[pos].Name!!
                            edtStatus.setText(mTaskstatus)
                            dialogSelectStatus!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectStatus!!.show()
    }

    private fun selectPriorityDialog() {
        var dialogSelectPriority = Dialog(this)
        dialogSelectPriority.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectPriority.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectPriority.window!!.attributes)

        dialogSelectPriority.window!!.attributes = lp
        dialogSelectPriority.setCancelable(true)
        dialogSelectPriority.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectPriority.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectPriority.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectPriority.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectPriority.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectPriority.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectPriority.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectPriority.dismiss()
        }

        txtid.text = "Select Priority"

        val itemAdapter = BottomSheetListAdapter(this, arrayListtaskpriority!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {
                itemAdapter.updateItem(pos)
                mTaskpriority = arrayListtaskpriority[pos].Name!!
                edtPriority.setText(mTaskpriority)
                dialogSelectPriority!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if(arrayListtaskpriority!!.size > 6) {
            edtSearchCustomer.visible()
        } else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<SingleSelectionModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListtaskpriority!!) {
                        if (model.Name!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetListAdapter(this@AddTaskLogsActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mTaskpriority = arrItemsFinal1[pos].Name!!
                            edtPriority.setText(mTaskpriority)
                            dialogSelectPriority!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetListAdapter(this@AddTaskLogsActivity, arrayListtaskpriority!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mTaskpriority = arrayListtaskpriority[pos].Name!!
                            edtPriority.setText(mTaskpriority)
                            dialogSelectPriority!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectPriority!!.show()
    }

    private fun selectRepeatDialog() {
        var dialogSelectRepeat = Dialog(this)
        dialogSelectRepeat.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectRepeat.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectRepeat.window!!.attributes)

        dialogSelectRepeat.window!!.attributes = lp
        dialogSelectRepeat.setCancelable(true)
        dialogSelectRepeat.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectRepeat.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectRepeat.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectRepeat.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectRepeat.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectRepeat.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectRepeat.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectRepeat.dismiss()
        }

        txtid.text = "Select Repeat"

        val itemAdapter = BottomSheetListAdapter(this, arrayListrepeat!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {
                itemAdapter.updateItem(pos)
                mTaskrepeat = arrayListrepeat[pos].Name!!
                mTaskrepeatID = arrayListrepeat[pos].ID!!
                edtRepeat.setText(mTaskrepeat)
                dialogSelectRepeat!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if(arrayListrepeat!!.size > 6) {
            edtSearchCustomer.visible()
        } else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<SingleSelectionModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListrepeat!!) {
                        if (model.Name!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetListAdapter(this@AddTaskLogsActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mTaskrepeat = arrItemsFinal1[pos].Name!!
                            mTaskrepeatID = arrItemsFinal1[pos].ID!!
                            edtRepeat.setText(mTaskrepeat)
                            dialogSelectRepeat!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetListAdapter(this@AddTaskLogsActivity, arrayListrepeat!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mTaskrepeat = arrayListrepeat[pos].Name!!
                            mTaskrepeatID = arrayListrepeat[pos].ID!!
                            edtRepeat.setText(mTaskrepeat)
                            dialogSelectRepeat!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectRepeat!!.show()
    }

    private fun selectNotifyviaDialog() {
        var dialogSelectNotifyvia = Dialog(this)
        dialogSelectNotifyvia.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectNotifyvia.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectNotifyvia.window!!.attributes)

        dialogSelectNotifyvia.window!!.attributes = lp
        dialogSelectNotifyvia.setCancelable(true)
        dialogSelectNotifyvia.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectNotifyvia.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectNotifyvia.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectNotifyvia.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectNotifyvia.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectNotifyvia.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectNotifyvia.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectNotifyvia.dismiss()
        }

        txtid.text = "Select Notify via"


        val itemAdapter = BottomSheetListAdapter(this, arrayListnotifyvia!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {
                itemAdapter.updateItem(pos)
                mTasknotifyvia = arrayListnotifyvia[pos].Name!!
                mTasknotifyviaID = arrayListnotifyvia[pos].ID!!
                edtNotifyvia.setText(mTasknotifyvia)
                dialogSelectNotifyvia!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if(arrayListnotifyvia!!.size > 6) {
            edtSearchCustomer.visible()
        } else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<SingleSelectionModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListnotifyvia!!) {
                        if (model.Name!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetListAdapter(this@AddTaskLogsActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mTasknotifyvia = arrItemsFinal1[pos].Name!!
                            mTasknotifyviaID = arrItemsFinal1[pos].ID!!
                            edtNotifyvia.setText(mTasknotifyvia)
                            dialogSelectNotifyvia!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetListAdapter(this@AddTaskLogsActivity, arrayListnotifyvia!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {
                            itemAdapter.updateItem(pos)
                            mTasknotifyvia = arrayListnotifyvia[pos].Name!!
                            mTasknotifyviaID = arrayListnotifyvia[pos].ID!!
                            edtNotifyvia.setText(mTasknotifyvia)
                            dialogSelectNotifyvia!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectNotifyvia!!.show()
    }

    private fun showAttachmentBottomSheetDialog() {
        if (Build.VERSION.SDK_INT <= 32) {
            if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {

                showBottomSheetDialogAttachments()
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.msg_permission_storage),
                    RC_FILE_PICKER_PERM,
                    FilePickerConst.PERMISSIONS_FILE_PICKER
                )
            }
        } else{
            showBottomSheetDialogAttachments()
        }
    }

    private fun showBottomSheetDialogAttachments() {
        val bottomSheetDialog = BottomSheetDialog(this,R.style.SheetDialog)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_attachment)

        val Select_Image = bottomSheetDialog.findViewById<LinearLayout>(R.id.Select_Image)
        val Select_Doc = bottomSheetDialog.findViewById<LinearLayout>(R.id.Select_Doc)
        val Select_WordDoc = bottomSheetDialog.findViewById<LinearLayout>(R.id.Select_WordDoc)

        Select_Image!!.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
                        photopicker()
                    } else {
                        EasyPermissions.requestPermissions(
                            this,
                            getString(R.string.msg_permission_camera),
                            900,
                            Manifest.permission.CAMERA
                        )
                    }
                } else {
                    EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.msg_permission_storage),
                        900,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                }
            } else {
                if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
                        photopicker()
                    } else {
                        EasyPermissions.requestPermissions(
                            this,
                            getString(R.string.msg_permission_camera),
                            900,
                            Manifest.permission.CAMERA
                        )
                    }
                } else {
                    EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.msg_permission_storage),
                        900,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
            }

            bottomSheetDialog.dismiss()
        }
        Select_Doc!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/*"
            startActivityForResult(intent, 101)
            bottomSheetDialog.dismiss()
        }
        Select_WordDoc!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/msword"
            startActivityForResult(Intent.createChooser(intent, "Select Ms Word File"), 438)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    // attachment
    private fun photopicker() {
        FilePickerBuilder.instance
            .setMaxCount(1)
            .setSelectedFiles(ImagePaths)
            .setActivityTheme(R.style.LibAppTheme)
            .pickPhoto(this, 20111)
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when (type) {
            1 -> {
                when(view.id) {
                    R.id.imgRemove -> {
                        removeAdapterItem(position)
                    }
                }
            }
        }
    }

    // attachment
    private fun removeAdapterItem(position: Int) {
        if (::adapter.isInitialized) {
            adapter.removeItem(position)
        }
    }

    // attachment
    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            //Photo Selection
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {

                    val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                    if (result.uri != null) {
                        val imageUri = result.uri!!

                        val fullName = imageUri.path!!.substringAfterLast("/")
                        val fileName = fullName.substringBeforeLast(".")
                        val extension = imageUri.path!!.substringAfterLast(".")

                        showBottomSheetDialogRename(fullName , imageUri , 2)
//                        arrayListAttachment!!.add(AttachmentModel(name = fullName, attachmentUri = imageUri, attachmentType = 2))
//                        adapter.notifyDataSetChanged()
                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val result: CropImage.ActivityResult = CropImage.getActivityResult(data)

                    val error = result.error
                    toast("No apps can perform this action.", Toast.LENGTH_LONG)
                }
            }

            20111 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {

                    ImagePaths = ArrayList()
//                    ImagePaths.addAll(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)!!)
                    ImagePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)!!)
                    if (!ImagePaths.isNullOrEmpty()){
//                        val PassportPath = ImagePaths[0]
                        val PassportPath = Uri.fromFile(File(ImagePaths[0]))
                        CropImage.activity(PassportPath)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this)
                    }
                }
            }

            101 -> if (resultCode == Activity.RESULT_OK && data != null) {
                val sUri = data!!.data
                val sPath = sUri!!.path

                val PassportPath = Uri.fromFile(fileFromContentUri1("passport", applicationContext, sUri))
                var displayName = ""

                if (sUri.toString().startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor = getContentResolver().query(sUri, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                    } finally {
                        cursor!!.close()
                    }
                } else {
                    displayName = sPath!!
                }

                showBottomSheetDialogRename(displayName , PassportPath , 1)

//                arrayListAttachment!!.add(AttachmentModel(name = displayName!!,attachmentUri = PassportPath, attachmentType = 1))
//                adapter.notifyDataSetChanged()
            }
        }
    }

    // attachment
    fun fileFromContentUri1(name  :String, context: Context, contentUri: Uri): File {
        // Preparing Temp file name
        val fileExtension = getFileExtension(context, contentUri)
        val fileName = "temp_file_" + name + if (fileExtension != null) ".$fileExtension" else ""

        // Creating Temp file
        val tempFile = File(context.cacheDir, fileName)
        tempFile.createNewFile()

        try {
            val oStream = FileOutputStream(tempFile)
            val inputStream = context.contentResolver.openInputStream(contentUri)

            inputStream?.let {
                copy(inputStream, oStream)
            }

            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tempFile
    }

    // attachment
    private fun getFileExtension(context: Context, uri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    // attachment
    @Throws(IOException::class)
    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }

    //Permission Result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //EasyPermissions.PermissionCallbacks
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    //EasyPermissions.PermissionCallbacks
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    private fun showBottomSheetDialogRename(name: String , fileuri : Uri , attachmenttype : Int) {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_rename_dialog)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)

        val img = bottomSheetDialog.findViewById<CircleImageView>(R.id.img)
        val edtName = bottomSheetDialog.findViewById<EditText>(R.id.edtName)
        val txtButtonCancel = bottomSheetDialog.findViewById<TextView>(R.id.txtButtonCancel)
        val txtButtonSubmit = bottomSheetDialog.findViewById<TextView>(R.id.txtButtonSubmit)

        if(attachmenttype == 1) {
            img!!.setImageResource(R.drawable.pdficon)
        } else {
            img!!.setImageURI(fileuri)
        }

        edtName!!.setText(name)
        edtName!!.requestFocus()

        txtButtonSubmit!!.setOnClickListener {
            if(!edtName.text.toString().trim().equals("")) {
                adapter.addItem(edtName.text.toString(), fileuri, attachmenttype)
                bottomSheetDialog.dismiss()
            } else {
                edtName.error = "Enter Name"
            }
        }

        txtButtonCancel!!.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun CallUploadDocuments(referenceGUID: String?) {

        showProgress()

        val partsList: ArrayList<MultipartBody.Part> = ArrayList()
        val AttachmentName: ArrayList<String> = ArrayList()

        if(!arrayListAttachment.isNullOrEmpty()) {
            for (i in arrayListAttachment!!.indices) {
                if (arrayListAttachment!![i].AttachmentType == "Image") {
                    if (arrayListAttachment!![i].AttachmentURL != null) {
                        partsList.add(CommonUtil.prepareFilePart(this, "image/*", "AttachmentURL", arrayListAttachment!![i].AttachmentURL!!.toUri()))
                        AttachmentName.add(arrayListAttachment!![i].AttachmentName!!)

                    } else {
                        val attachmentEmpty: RequestBody = RequestBody.create(MediaType.parse("text/plain"), "")
                        partsList.add(MultipartBody.Part.createFormData("AttachmentURL", "", attachmentEmpty))
                    }
                } else {
                    if (arrayListAttachment!![i].AttachmentURL != null) {
                        partsList.add(CommonUtil.prepareFilePart(this, "application/*", "AttachmentURL", arrayListAttachment!![i].AttachmentURL!!.toUri()))
                        AttachmentName.add(arrayListAttachment!![i].AttachmentName!!)
                    } else {
                        val attachmentEmpty: RequestBody = RequestBody.create(MediaType.parse("text/plain"), "")
                        partsList.add(MultipartBody.Part.createFormData("AttachmentURL", "", attachmentEmpty))
                    }
                }
            }
        }

        val a = AttachmentName.toString().replace("[", "").replace("]", "")
        LogUtil.d(TAG,"111===> "+a)
        val mAttachmentName = CommonUtil.createPartFromString(a)
        val mreferenceGUID = CommonUtil.createPartFromString(referenceGUID.toString())
        val mAttachmentType = CommonUtil.createPartFromString(AppConstant.TASK)
        val mID = CommonUtil.createPartFromString(ID.toString())

        val call = ApiUtils.apiInterface.ManageAttachment(
            LeadID = LeadID,
            ReferenceGUID = mreferenceGUID,
            NBInquiryTypeID = mID,
            AttachmentType = mAttachmentType,
            AttachmentName = mAttachmentName,
            attachment = partsList
        )
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                hideProgress()
                if (response.code() == 200) {

                    if (response.body()?.Status == 200) {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            }
            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        })
    }

}