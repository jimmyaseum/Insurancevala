package com.app.insurancevala.activity.Lead

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
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
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetMeetingOutcomeListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetMeetingStatusListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetMeetingTypeListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetUserTypeListAdapter
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
import kotlinx.android.synthetic.main.activity_add_meeting_logs.*
import kotlinx.android.synthetic.main.activity_add_meeting_logs.layout
import kotlinx.android.synthetic.main.activity_add_meeting_logs.view.*
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

class AddMeetingsActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener,
    EasyPermissions.PermissionCallbacks {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var ID: Int? = null
    var LeadID: Int? = null
    var Lead: Boolean? = false
    var MeetingGUID: String? = null
    var ReferenceGUID: String? = null

    // attachments
    var arrayListAttachment: ArrayList<DocumentsModel>? = null
    lateinit var adapter: MultipleAttachmentListAdapter
    val RC_FILE_PICKER_PERM = 900
    var ImagePaths = ArrayList<String>()

    //date
    val CalenderDate = Calendar.getInstance()
    var MeetingDate: String = ""

    var MeetingStartTime: String = ""
    var MeetingEndTime: String = ""
    var FollowUpTime: String = ""

    //followup date
    val CalenderFollowUpMeetingDate = Calendar.getInstance()
    var FollowUpMeetingDate: String = ""

    var arrayListmeetingtype: ArrayList<MeetingTypeModel>? = ArrayList()
    var mMeetingtype: String = ""
    var mMeetingtypeID: Int = 0

    var arrayListmeetingstatus: ArrayList<MeetingStatusModel>? = ArrayList()
    var mMeetingstatus: String = ""
    var mMeetingstatusID: Int = 0

    var arrayListmeetingoutcome: ArrayList<MeetingOutcomeModel>? = ArrayList()
    var mMeetingoutcome: String = ""
    var mMeetingoutcomeID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meeting_logs)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        if (intent.hasExtra("Lead")) {
            Lead = intent.getBooleanExtra("Lead", false)
        }
        state = intent.getStringExtra(AppConstant.STATE)
        ID = intent.getIntExtra("ID", 0)
        LeadID = intent.getIntExtra("LeadID", 0)
    }

    override fun initializeView() {
        if (state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Create Meeting"
            setMasterData()
        } else if (state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update Meeting"

            if (isOnline(this)) {
                ID = intent.getIntExtra("ID", 0)
                LeadID = intent.getIntExtra("LeadID", 0)
                MeetingGUID = intent.getStringExtra("MeetingGUID")

                callManageMeetingsGUID()
            } else {
                internetErrordialog(this@AddMeetingsActivity)
            }
        }
        SetInitListner()
    }

    private fun setMasterData() {
        if (isOnline(this)) {
            callManageMeetingsType(0)
            callManageMeetingsStatus(0)
            callManageMeetingsOutcome(0)
        } else {
            internetErrordialog(this@AddMeetingsActivity)
        }
//        location()
    }

    private fun setDefaultDate() {

        val mDate = convertDateStringToString(
            getcurrentdate(),
            AppConstant.dd_MM_yyyy_HH_mm_ss,
            AppConstant.dd_LLL_yyyy
        )
        val mTime = convertDateStringToString(
            getcurrentdate(),
            AppConstant.dd_MM_yyyy_HH_mm_ss,
            AppConstant.HH_MM_AA_FORMAT
        )

        edtMeetingDate.setText(mDate)

        edtMeetingStartTime.setText(mTime)
        edtMeetingEndTime.setText(mTime)

        MeetingDate = convertDateStringToString(
            getcurrentdate(),
            AppConstant.dd_MM_yyyy_HH_mm_ss,
            AppConstant.yyyy_MM_dd_Dash
        ).toString()
        FollowUpMeetingDate = convertDateStringToString(
            getcurrentdate(),
            AppConstant.dd_MM_yyyy_HH_mm_ss,
            AppConstant.yyyy_MM_dd_Dash
        ).toString()

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
        edtMeetingType.setOnClickListener(this)
        edtMeetingDate.setOnClickListener(this)
        edtMeetingStartTime.setOnClickListener(this)
        edtMeetingEndTime.setOnClickListener(this)
        edtMeetingStatus.setOnClickListener(this)
        edtMeetingOutcome.setOnClickListener(this)
        edtAttachments.setOnClickListener(this)
        cbIsFollowup.setOnClickListener(this)
        edtFollowupDate.setOnClickListener(this)
        edtFollowupTime.setOnClickListener(this)
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

            R.id.edtMeetingType -> {
                preventTwoClick(v)
                if (arrayListmeetingtype.isNullOrEmpty()) {
                    callManageMeetingsType(1)
                } else {
                    selectTypeDialog()
                }
            }

            R.id.edtMeetingDate -> {
                preventTwoClick(v)
                val dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        CalenderDate.set(Calendar.YEAR, year)
                        CalenderDate.set(Calendar.MONTH, monthOfYear)
                        CalenderDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        MeetingDate =
                            SimpleDateFormat(AppConstant.yyyy_MM_dd_Dash, Locale.US).format(
                                CalenderDate.time
                            )

                        val selecteddate =
                            SimpleDateFormat(AppConstant.dd_MM_yyyy_HH_mm_ss, Locale.US).format(
                                CalenderDate.time
                            )
                        val mDate = convertDateStringToString(
                            selecteddate,
                            AppConstant.dd_MM_yyyy_HH_mm_ss,
                            AppConstant.dd_LLL_yyyy
                        )
                        edtMeetingDate.setText(mDate)
                    },
                    CalenderDate.get(Calendar.YEAR),
                    CalenderDate.get(Calendar.MONTH),
                    CalenderDate.get(Calendar.DAY_OF_MONTH)
                )
//                dpd.datePicker.minDate = System.currentTimeMillis() - 1000
//                dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
                dpd.show()
            }

            R.id.edtMeetingStartTime -> {
                preventTwoClick(v)
                val caltime = Calendar.getInstance()
                val hour = caltime.get(Calendar.HOUR_OF_DAY)
                val minute = caltime.get(Calendar.MINUTE)
                val timePickerDialog = TimePickerDialog(this, { view, hourOfDay, minute ->
                    val mTime = convertDateStringToString(
                        "$hourOfDay:$minute",
                        AppConstant.HH_MM_FORMAT,
                        AppConstant.HH_MM_AA_FORMAT
                    )!!
                    MeetingStartTime = convertDateStringToString(
                        "$hourOfDay:$minute",
                        AppConstant.HH_MM_FORMAT,
                        AppConstant.HH_MM_SS_FORMAT
                    )!!
                    edtMeetingStartTime.setText(mTime)
                }, hour, minute, false)

                timePickerDialog.show()
            }

            R.id.edtMeetingEndTime -> {
                preventTwoClick(v)
                val caltime = Calendar.getInstance()
                val hour = caltime.get(Calendar.HOUR_OF_DAY)
                val minute = caltime.get(Calendar.MINUTE)
                val timePickerDialog = TimePickerDialog(this, { view, hourOfDay, minute ->
                    val mTime = convertDateStringToString(
                        "$hourOfDay:$minute",
                        AppConstant.HH_MM_FORMAT,
                        AppConstant.HH_MM_AA_FORMAT
                    )!!
                    MeetingEndTime = convertDateStringToString(
                        "$hourOfDay:$minute",
                        AppConstant.HH_MM_FORMAT,
                        AppConstant.HH_MM_SS_FORMAT
                    )!!
                    edtMeetingEndTime.setText(mTime)
                }, hour, minute, false)

                timePickerDialog.show()
            }

            R.id.edtMeetingStatus -> {
                preventTwoClick(v)
                if (arrayListmeetingstatus.isNullOrEmpty()) {
                    callManageMeetingsStatus(1)
                } else {
                    selectMeetingStatusDialog()
                }
            }

            R.id.edtMeetingOutcome -> {
                preventTwoClick(v)
                if (arrayListmeetingoutcome.isNullOrEmpty()) {
                    callManageMeetingsOutcome(1)
                } else {
                    selectOutcomeDialog()
                }
            }

            R.id.edtAttachments -> {
                preventTwoClick(v)
                showAttachmentBottomSheetDialog()
            }

            R.id.cbIsFollowup -> {
                if (cbIsFollowup.isChecked) {
                    LLFollowupDate.visible()
                    LLFollowUpTime.visible()
                    LLFollowupNotes.visible()
                } else {
                    LLFollowupDate.gone()
                    LLFollowUpTime.gone()
                    LLFollowupNotes.gone()
                }
            }

            R.id.edtFollowupDate -> {
                preventTwoClick(v)

                val dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        CalenderFollowUpMeetingDate.set(Calendar.YEAR, year)
                        CalenderFollowUpMeetingDate.set(Calendar.MONTH, monthOfYear)
                        CalenderFollowUpMeetingDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        FollowUpMeetingDate =
                            SimpleDateFormat(AppConstant.yyyy_MM_dd_Dash, Locale.US).format(
                                CalenderFollowUpMeetingDate.time
                            )

                        val selecteddate =
                            SimpleDateFormat(AppConstant.dd_MM_yyyy_HH_mm_ss, Locale.US).format(
                                CalenderFollowUpMeetingDate.time
                            )
                        val mDate = convertDateStringToString(
                            selecteddate,
                            AppConstant.dd_MM_yyyy_HH_mm_ss,
                            AppConstant.dd_LLL_yyyy
                        )
                        edtFollowupDate.setText(mDate)
                    },
                    CalenderFollowUpMeetingDate.get(Calendar.YEAR),
                    CalenderFollowUpMeetingDate.get(Calendar.MONTH),
                    CalenderFollowUpMeetingDate.get(Calendar.DAY_OF_MONTH)
                )
//                dpd.datePicker.minDate = System.currentTimeMillis() - 1000
//                dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
                dpd.show()
            }

            R.id.edtFollowupTime -> {
                preventTwoClick(v)
                val caltime = Calendar.getInstance()
                val hour = caltime.get(Calendar.HOUR_OF_DAY)
                val minute = caltime.get(Calendar.MINUTE)
                val timePickerDialog = TimePickerDialog(this, { view, hourOfDay, minute ->
                    val mTime = convertDateStringToString(
                        "$hourOfDay:$minute",
                        AppConstant.HH_MM_FORMAT,
                        AppConstant.HH_MM_AA_FORMAT
                    )!!
                    FollowUpTime = convertDateStringToString(
                        "$hourOfDay:$minute",
                        AppConstant.HH_MM_FORMAT,
                        AppConstant.HH_MM_SS_FORMAT
                    )!!
                    edtFollowupTime.setText(mTime)
                }, hour, minute, false)

                timePickerDialog.show()
            }
        }
    }

    private fun validation() {

        when (isValidate()) {
            true -> {
                if (isOnline(this)) {
                    ManageCreateMeetingAPI()
                } else {
                    internetErrordialog(this@AddMeetingsActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isValidate = true

        if (edtMeetingPurpose.text.isEmpty()) {
            edtMeetingPurpose.setError("Enter Purpose", errortint(this))
            isValidate = false
        }
        if (edtMeetingDescription.text.isEmpty()) {
            edtMeetingDescription.setError("Enter Description", errortint(this))
            isValidate = false
        }
        if (edtLocation.text.isEmpty()) {
            edtLocation.setError("Enter Location", errortint(this))
            isValidate = false
        }
        if (edtMeetingDate.text.isEmpty()) {
            edtMeetingDate.setError("Select MeetingDate", errortint(this))
            isValidate = false
        }
        if (edtMeetingStartTime.text.isEmpty()) {
            edtMeetingStartTime.setError("Select Meeting StartTime", errortint(this))
            isValidate = false
        }
        if (edtMeetingEndTime.text.isEmpty()) {
            edtMeetingEndTime.setError("Select Meeting EndTime", errortint(this))
            isValidate = false
        }
        if (cbIsFollowup.isChecked) {
            if (edtFollowupDate.text.isEmpty()) {
                edtFollowupDate.setError("Select Followup Date", errortint(this))
                isValidate = false
            }
            if (edtFollowupTime.text.isEmpty()) {
                edtFollowupTime.setError("Select Followup Time", errortint(this))
                isValidate = false
            }
        }

        return isValidate
    }

    private fun ManageCreateMeetingAPI() {

        showProgress()

        var isFollowup = false

        val jsonObject = JSONObject()
        if (Lead!!) {
            jsonObject.put("NBLeadTypeID", ID)
            jsonObject.put("NBInquiryTypeID", null)
        } else {
            jsonObject.put("NBLeadTypeID", null)
            jsonObject.put("NBInquiryTypeID", ID)
        }
        jsonObject.put("LeadID", LeadID)
        jsonObject.put("MeetingTypeID", mMeetingtypeID)
        jsonObject.put("MeetingDate", MeetingDate)
        jsonObject.put("StartTime", MeetingStartTime)
        jsonObject.put("EndTime", MeetingEndTime)
        jsonObject.put("Location", edtLocation.text.toString().trim())
        jsonObject.put("Purpose", edtMeetingPurpose.text.toString().trim())
        jsonObject.put("Description", edtMeetingDescription.text.toString().trim())
        jsonObject.put("MeetingStatusID", mMeetingstatusID)
        jsonObject.put("MeetingOutcomeID", mMeetingoutcomeID)
        jsonObject.put("Attendee", edtAttendee.text.toString().trim())

        if (cbIsFollowup.isChecked) {
            isFollowup = true
            jsonObject.put("FollowupDate", FollowUpMeetingDate)
            jsonObject.put("FollowupTime", FollowUpTime)
            jsonObject.put("FollowupNotes", edtFollowupNotes.text.toString().trim())
        } else {
            isFollowup = false
        }
        jsonObject.put("IsFollowup", isFollowup)
        jsonObject.put("IsActive", true)

        if (state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("MeetingGUID", MeetingGUID)
        }

        if (state.equals(AppConstant.S_ADD)) {
            val call =
                ApiUtils.apiInterface.ManageMeetingInsert(getRequestJSONBody(jsonObject.toString()))
            call.enqueue(object : Callback<RefGUIDResponse> {
                override fun onResponse(
                    call: Call<RefGUIDResponse>,
                    response: Response<RefGUIDResponse>
                ) {
                    hideProgress()
                    if (response.code() == 200) {
                        if (response.body()?.Status == 201) {

                            Snackbar.make(
                                layout,
                                response.body()?.Details.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                            ReferenceGUID = response.body()?.Data!!.ReferenceGUID

                            if (arrayListAttachment!!.size > 0) {
                                CallUploadDocuments(ReferenceGUID)
                            } else {
                                val intent = Intent()
                                setResult(RESULT_OK, intent)
                                finish()
                            }
                        } else {
                            Snackbar.make(
                                layout,
                                response.body()?.Details.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
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
            val call =
                ApiUtils.apiInterface.ManageMeetingUpdate(getRequestJSONBody(jsonObject.toString()))
            call.enqueue(object : Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                ) {
                    hideProgress()
                    if (response.code() == 200) {
                        if (response.body()?.Status == 201) {
                            Snackbar.make(
                                layout,
                                response.body()?.Details.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                            val intent = Intent()
                            setResult(RESULT_OK, intent)
                            finish()
                        } else {
                            Snackbar.make(
                                layout,
                                response.body()?.Details.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
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

    private fun callManageMeetingsGUID() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("MeetingGUID", MeetingGUID)

        val call =
            ApiUtils.apiInterface.ManageMeetingFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<MeetingsByGUIDResponse> {
            override fun onResponse(
                call: Call<MeetingsByGUIDResponse>,
                response: Response<MeetingsByGUIDResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListLead = response.body()?.Data!!
                        setAPIData(arrayListLead)
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<MeetingsByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setAPIData(model: MeetingsModel) {
        if (model.MeetingDate != null && model.MeetingDate != "") {
            edtMeetingDate.setText(model.MeetingDate)
            MeetingDate = convertDateStringToString(
                model.MeetingDate,
                AppConstant.dd_MM_yyyy_Slash,
                AppConstant.yyyy_MM_dd_Dash
            ).toString()

        }
        if (model.Purpose != null && model.Purpose != "") {
            edtMeetingPurpose.setText(model.Purpose)
        }
        if (model.MeetingType != null && model.MeetingType != "") {
            edtMeetingType.setText(model.MeetingType)
            mMeetingtypeID = model.MeetingTypeID!!
            callManageMeetingsType(0)
        }
        if (model.StartTime != null && model.Purpose != "" && model.EndTime != null && model.EndTime != "") {
            val mStime = convertDateStringToString(
                model.StartTime!!,
                AppConstant.HH_MM_SS_FORMAT,
                AppConstant.HH_MM_AA_FORMAT
            )
            val mEtime = convertDateStringToString(
                model.EndTime!!,
                AppConstant.HH_MM_SS_FORMAT,
                AppConstant.HH_MM_AA_FORMAT
            )
            edtMeetingStartTime.setText(mStime)
            edtMeetingEndTime.setText(mEtime)

            MeetingStartTime = model.StartTime!!
            MeetingEndTime = model.EndTime!!
        }
        if (model.MeetingStatus != null && model.MeetingStatus != "") {
            edtMeetingStatus.setText(model.MeetingStatus)
            mMeetingstatusID = model.MeetingStatusID!!
            mMeetingstatus = model.MeetingStatus
            callManageMeetingsStatus(0)
        }
        if (model.MeetingOutcome != null && model.MeetingOutcome != "") {
            edtMeetingOutcome.setText(model.MeetingOutcome)
            mMeetingoutcomeID = model.MeetingOutcomeID!!
            mMeetingoutcome = model.MeetingOutcome
            callManageMeetingsOutcome(0)
        }
        if (model.Description != null && model.Description != "") {
            edtMeetingDescription.setText(model.Description)
        }
        if (model.Location != null && model.Location != "") {
            edtLocation.setText(model.Location)
        }
        if (model.Attendee != null && model.Attendee != "") {
            edtAttendee.setText(model.Attendee)
        }
        if (model.IsFollowup != null) {
            if (model.IsFollowup) {
                cbIsFollowup.isChecked = true
                LLFollowupDate.visible()
                LLFollowUpTime.visible()
                LLFollowupNotes.visible()
            } else {
                cbIsFollowup.isChecked = false
                LLFollowupDate.gone()
                LLFollowUpTime.gone()
                LLFollowupNotes.gone()
            }
        }

        if (model.FollowupDate != null && model.FollowupDate != "") {
            edtFollowupDate.setText(model.FollowupDate)
            FollowUpMeetingDate = convertDateStringToString(
                model.FollowupDate,
                AppConstant.dd_MM_yyyy_Slash,
                AppConstant.yyyy_MM_dd_Dash
            ).toString()
        }

        if (model.FollowupTime != null && model.FollowupTime != "") {
            val FollowupTime = convertDateStringToString(
                model.FollowupTime,
                AppConstant.HH_MM_SS_FORMAT,
                AppConstant.HH_MM_AA_FORMAT
            )
            edtFollowupTime.setText(FollowupTime)

            FollowUpTime = model.FollowupTime
        }

        if (model.FollowupNotes != null && model.FollowupNotes != "") {
            edtFollowupNotes.setText(model.FollowupNotes)
        }

        txtAttachments.gone()
        edtAttachments.gone()
        viewAttachments.gone()
        if (!model.MeetingAttachmentList.isNullOrEmpty()) {
            arrayListAttachment = java.util.ArrayList()
            arrayListAttachment = model.MeetingAttachmentList
            adapter = MultipleAttachmentListAdapter(this, false, arrayListAttachment, this)

            rvAttachment.adapter = adapter
            txtAttachments.visible()
        }
    }

    private fun callManageMeetingsType(mode: Int) {
        if (mode == 1) {
            showProgress()
        }
        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call =
            ApiUtils.apiInterface.ManageMeetingType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<MeetingTypeResponse> {
            override fun onResponse(
                call: Call<MeetingTypeResponse>,
                response: Response<MeetingTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListmeetingtype = response.body()?.Data!!
                        arrayListmeetingtype!!.sortBy { it.ID }
                        if (mode == 1) {
                            selectTypeDialog()
                        } else {
                            mMeetingtype = arrayListmeetingtype!![0].MeetingType!!
                            mMeetingtypeID = arrayListmeetingtype!![0].ID!!
                            edtMeetingType.setText(mMeetingtype)
                            arrayListmeetingtype!![0].IsSelected = true

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

            override fun onFailure(call: Call<MeetingTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }

    private fun selectTypeDialog() {
        var dialogSelectType = Dialog(this)
        dialogSelectType.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectType.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectType.window!!.attributes)

        dialogSelectType.window!!.attributes = lp
        dialogSelectType.setCancelable(true)
        dialogSelectType.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectType.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectType.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectType.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectType.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectType.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectType.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectType.dismiss()
        }

        txtid.text = "Select Meeting Type"

        val itemAdapter = BottomSheetMeetingTypeListAdapter(this, arrayListmeetingtype!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mMeetingtype = arrayListmeetingtype!![pos].MeetingType!!
                mMeetingtypeID = arrayListmeetingtype!![pos].ID!!
                edtMeetingType.setText(mMeetingtype)
                dialogSelectType!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListmeetingtype!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<MeetingTypeModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListmeetingtype!!) {
                        if (model.MeetingType!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetMeetingTypeListAdapter(this@AddMeetingsActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mMeetingtype = arrItemsFinal1!![pos].MeetingType!!
                            mMeetingtypeID = arrItemsFinal1!![pos].ID!!
                            edtMeetingType.setText(mMeetingtype)
                            dialogSelectType!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetMeetingTypeListAdapter(
                        this@AddMeetingsActivity,
                        arrayListmeetingtype!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mMeetingtype = arrayListmeetingtype!![pos].MeetingType!!
                            mMeetingtypeID = arrayListmeetingtype!![pos].ID!!
                            edtMeetingType.setText(mMeetingtype)
                            dialogSelectType!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectType!!.show()
    }

    private fun callManageMeetingsStatus(mode: Int) {
        if (mode == 1) {
            showProgress()
        }
        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call =
            ApiUtils.apiInterface.ManageMeetingStatus(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<MeetingStatusResponse> {
            override fun onResponse(
                call: Call<MeetingStatusResponse>,
                response: Response<MeetingStatusResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListmeetingstatus = response.body()?.Data!!
                        arrayListmeetingstatus!!.sortBy { it.ID }
                        if (mode == 1) {
                            selectMeetingStatusDialog()
                        } else {
                            if (state == AppConstant.S_ADD) {
                                mMeetingstatus = arrayListmeetingstatus!![0].MeetingStatus!!
                                mMeetingstatusID = arrayListmeetingstatus!![0].ID!!
                                edtMeetingStatus.setText(mMeetingstatus)
                                arrayListmeetingstatus!![0].IsSelected = true
                            }
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

            override fun onFailure(call: Call<MeetingStatusResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectMeetingStatusDialog() {
        var dialogSelectMeetingStatus = Dialog(this)
        dialogSelectMeetingStatus.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectMeetingStatus.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectMeetingStatus.window!!.attributes)

        dialogSelectMeetingStatus.window!!.attributes = lp
        dialogSelectMeetingStatus.setCancelable(true)
        dialogSelectMeetingStatus.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectMeetingStatus.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectMeetingStatus.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectMeetingStatus.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectMeetingStatus.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectMeetingStatus.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectMeetingStatus.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectMeetingStatus.dismiss()
        }

        txtid.text = "Select Meeting Status"

        val itemAdapter = BottomSheetMeetingStatusListAdapter(this, arrayListmeetingstatus!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mMeetingstatus = arrayListmeetingstatus!![pos].MeetingStatus!!
                mMeetingstatusID = arrayListmeetingstatus!![pos].ID!!
                edtMeetingStatus.setText(mMeetingstatus)
                dialogSelectMeetingStatus!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListmeetingstatus!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<MeetingStatusModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListmeetingstatus!!) {
                        if (model.MeetingStatus!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetMeetingStatusListAdapter(
                        this@AddMeetingsActivity,
                        arrItemsFinal1
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mMeetingstatus = arrItemsFinal1!![pos].MeetingStatus!!
                            mMeetingstatusID = arrItemsFinal1!![pos].ID!!
                            edtMeetingStatus.setText(mMeetingstatus)
                            dialogSelectMeetingStatus!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetMeetingStatusListAdapter(
                        this@AddMeetingsActivity,
                        arrayListmeetingstatus!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mMeetingstatus = arrayListmeetingstatus!![pos].MeetingStatus!!
                            mMeetingstatusID = arrayListmeetingstatus!![pos].ID!!
                            edtMeetingStatus.setText(mMeetingstatus)
                            dialogSelectMeetingStatus!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectMeetingStatus!!.show()
    }

    private fun callManageMeetingsOutcome(mode: Int) {
        if (mode == 1) {
            showProgress()
        }
        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call =
            ApiUtils.apiInterface.ManageMeetingOutcome(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<MeetingOutcomeResponse> {
            override fun onResponse(
                call: Call<MeetingOutcomeResponse>,
                response: Response<MeetingOutcomeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListmeetingoutcome = response.body()?.Data!!
                        arrayListmeetingoutcome!!.sortBy { it.ID }

                        if (mode == 1) {
                            selectOutcomeDialog()
                        } else {
                            if (state == AppConstant.S_ADD) {
                                mMeetingoutcome = arrayListmeetingoutcome!![0].MeetingOutcome!!
                                mMeetingoutcomeID = arrayListmeetingoutcome!![0].ID!!
                                edtMeetingOutcome.setText(mMeetingoutcome)
                                arrayListmeetingoutcome!![0].IsSelected = true
                            }
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

            override fun onFailure(call: Call<MeetingOutcomeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectOutcomeDialog() {
        var dialogSelectOutcome = Dialog(this)
        dialogSelectOutcome.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectOutcome.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectOutcome.window!!.attributes)

        dialogSelectOutcome.window!!.attributes = lp
        dialogSelectOutcome.setCancelable(true)
        dialogSelectOutcome.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectOutcome.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectOutcome.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectOutcome.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectOutcome.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectOutcome.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectOutcome.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectOutcome.dismiss()
        }

        txtid.text = "Select Meeting Outcome"

        val itemAdapter = BottomSheetMeetingOutcomeListAdapter(this, arrayListmeetingoutcome!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mMeetingoutcome = arrayListmeetingoutcome!![pos].MeetingOutcome!!
                mMeetingoutcomeID = arrayListmeetingoutcome!![pos].ID!!
                edtMeetingOutcome.setText(mMeetingoutcome)
                dialogSelectOutcome!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListmeetingoutcome!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<MeetingOutcomeModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListmeetingoutcome!!) {
                        if (model.MeetingOutcome!!.toLowerCase()
                                .contains(strSearch.toLowerCase())
                        ) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetMeetingOutcomeListAdapter(
                        this@AddMeetingsActivity,
                        arrItemsFinal1
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mMeetingoutcome = arrItemsFinal1!![pos].MeetingOutcome!!
                            mMeetingoutcomeID = arrItemsFinal1!![pos].ID!!
                            edtMeetingOutcome.setText(mMeetingoutcome)
                            dialogSelectOutcome!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetMeetingOutcomeListAdapter(
                        this@AddMeetingsActivity,
                        arrayListmeetingoutcome!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mMeetingoutcome = arrayListmeetingoutcome!![pos].MeetingOutcome!!
                            mMeetingoutcomeID = arrayListmeetingoutcome!![pos].ID!!
                            edtMeetingOutcome.setText(mMeetingoutcome)
                            dialogSelectOutcome!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectOutcome!!.show()
    }

//    private fun location() {

//        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
//        val locationListener: LocationListener = object : LocationListener {
//            override fun onLocationChanged(location: Location) {
//               updateLocation(location)
//            }
//
//            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
//            override fun onProviderEnabled(provider: String) {}
//            override fun onProviderDisabled(provider: String) {}
//        }
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
//            PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//        locationManager.requestLocationUpdates(
//            LocationManager.NETWORK_PROVIDER,
//            1000,
//            0f,
//            locationListener
//        )
//    }
//
//    fun updateLocation(location: Location) {
//        val geocoder = Geocoder(applicationContext, Locale.getDefault())
//        try {
//            val listAddresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
//            var address = " "
//            if (listAddresses != null && listAddresses.size > 0) {
//                if (listAddresses[0].getThoroughfare() != null) {
//                    address = listAddresses[0].getThoroughfare() + " "
//                }
//                if (listAddresses[0].getLocality() != null) {
//                    address += listAddresses[0].getLocality() + " "
//                }
//                if (listAddresses[0].getPostalCode() != null) {
//                    address += listAddresses[0].getPostalCode() + " "
//                }
//                if (listAddresses[0].getAdminArea() != null) {
//                    address += listAddresses[0].getAdminArea()
//                }
//            }
//
//            edtLocation.setText(address)
//
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//    }

    // attachment
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
        } else {
            showBottomSheetDialogAttachments()
        }
    }

    private fun showBottomSheetDialogAttachments() {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.SheetDialog)
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
                if (EasyPermissions.hasPermissions(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
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
            intent.type = "application/pdf"
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
                when (view.id) {
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

                        showBottomSheetDialogRename(fullName, imageUri, 2)
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
                    if (!ImagePaths.isNullOrEmpty()) {
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

                val PassportPath =
                    Uri.fromFile(fileFromContentUri1("passport", applicationContext, sUri))
                var displayName = ""

                if (sUri.toString().startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor = getContentResolver().query(sUri, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                    } finally {
                        cursor!!.close()
                    }
                } else {
                    displayName = sPath!!
                }

                showBottomSheetDialogRename(displayName, PassportPath, 1)

//                arrayListAttachment!!.add(AttachmentModel(name = displayName!!,attachmentUri = PassportPath, attachmentType = 1))
//                adapter.notifyDataSetChanged()
            }
        }
    }

    // attachment
    fun fileFromContentUri1(name: String, context: Context, contentUri: Uri): File {
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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

    private fun showBottomSheetDialogRename(name: String, fileuri: Uri, attachmenttype: Int) {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_rename_dialog)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)

        val img = bottomSheetDialog.findViewById<CircleImageView>(R.id.img)
        val edtName = bottomSheetDialog.findViewById<EditText>(R.id.edtName)
        val txtButtonCancel = bottomSheetDialog.findViewById<TextView>(R.id.txtButtonCancel)
        val txtButtonSubmit = bottomSheetDialog.findViewById<TextView>(R.id.txtButtonSubmit)

        if (attachmenttype == 1) {
            img!!.setImageResource(R.drawable.pdficon)
        } else {
            img!!.setImageURI(fileuri)
        }

        edtName!!.setText(name)
        edtName!!.requestFocus()

        txtButtonSubmit!!.setOnClickListener {
            if (!edtName.text.toString().trim().equals("")) {
                adapter.addItem(edtName.text.toString(), fileuri, attachmenttype)
                bottomSheetDialog.dismiss()
            } else {
                edtName.setError("Enter Name", errortint(this))
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

        if (!arrayListAttachment.isNullOrEmpty()) {
            for (i in arrayListAttachment!!.indices) {
                if (arrayListAttachment!![i].AttachmentType == "Image") {
                    if (arrayListAttachment!![i].AttachmentURL != null) {
                        partsList.add(
                            CommonUtil.prepareFilePart(
                                this,
                                "image/*",
                                "AttachmentURL",
                                arrayListAttachment!![i].AttachmentURL!!.toUri()
                            )
                        )
                        AttachmentName.add(arrayListAttachment!![i].AttachmentName!!)

                    } else {
                        val attachmentEmpty: RequestBody =
                            RequestBody.create(MediaType.parse("text/plain"), "")
                        partsList.add(
                            MultipartBody.Part.createFormData(
                                "AttachmentURL",
                                "",
                                attachmentEmpty
                            )
                        )
                    }
                } else {
                    if (arrayListAttachment!![i].AttachmentURL != null) {
                        partsList.add(
                            CommonUtil.prepareFilePart(
                                this,
                                "application/*",
                                "AttachmentURL",
                                arrayListAttachment!![i].AttachmentURL!!.toUri()
                            )
                        )
                        AttachmentName.add(arrayListAttachment!![i].AttachmentName!!)
                    } else {
                        val attachmentEmpty: RequestBody =
                            RequestBody.create(MediaType.parse("text/plain"), "")
                        partsList.add(
                            MultipartBody.Part.createFormData(
                                "AttachmentURL",
                                "",
                                attachmentEmpty
                            )
                        )
                    }
                }
            }
        }

        val a = AttachmentName.toString().replace("[", "").replace("]", "")
        LogUtil.d(TAG, "111===> " + a)
        val mAttachmentName = CommonUtil.createPartFromString(a)
        val mreferenceGUID = CommonUtil.createPartFromString(referenceGUID.toString())
        val mAttachmentType = CommonUtil.createPartFromString(AppConstant.MEETING)
        var inquiryTypeID: RequestBody? = null
        var leadTypeID: RequestBody? = null

        if (Lead!!) {
            inquiryTypeID = null
            leadTypeID = CommonUtil.createPartFromString(ID.toString())
        } else {
            inquiryTypeID = CommonUtil.createPartFromString(ID.toString())
            leadTypeID = null
        }

        val call = ApiUtils.apiInterface.ManageAttachment(
            LeadID = LeadID,
            ReferenceGUID = mreferenceGUID,
            NBInquiryTypeID = inquiryTypeID,
            NBLeadTypeID = leadTypeID,
            AttachmentType = mAttachmentType,
            AttachmentName = mAttachmentName,
            attachment = partsList
        )
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {

                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
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