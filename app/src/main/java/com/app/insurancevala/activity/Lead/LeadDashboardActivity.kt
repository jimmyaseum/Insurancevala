package com.app.insurancevala.activity.Lead

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.core.content.ContextCompat
import com.app.insurancevala.FilePickerBuilder
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.activity.LeadList.AddLeadListActivity
import com.app.insurancevala.activity.LeadList.LeadListActivity
import com.app.insurancevala.activity.NBInquiry.AddNBActivity
import com.app.insurancevala.activity.NBInquiry.InquiryListActivity
import com.app.insurancevala.adapter.FamilyMemberAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.FamilyDetailsModel
import com.app.insurancevala.model.response.LeadByGUIDResponse
import com.app.insurancevala.model.response.LeadImageResponse
import com.app.insurancevala.model.response.LeadModel
import com.app.insurancevala.model.response.UserModel
import com.app.insurancevala.model.response.UserResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.activity_lead_dashboard.*
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

class LeadDashboardActivity : BaseActivity() , View.OnClickListener, RecyclerClickListener, EasyPermissions.PermissionCallbacks{

    // attachments
    val RC_FILE_PICKER_PERM = 900
    var ImagePaths = ArrayList<String>()
    var imageURI: Uri? = null

    var LeadID: Int? = null
    var LeadGUID: String? = null

    var LeadOwnerID: Int? = null
    var LeadOwnerName: String? = null

    var arrayListLead: LeadModel? = null

    var arrayListUsers: ArrayList<UserModel>? = ArrayList()
    var mUsersName: String = ""

    var arrayListFamilyMember : ArrayList<FamilyDetailsModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_dashboard)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)

        LeadID = intent.getIntExtra("LeadID",0)
        LeadGUID = intent.getStringExtra("GUID")

        initializeView()
    }

    override fun initializeView() {
        if (isOnline(this)) {
            callManageLeadGUID()
        } else {
            internetErrordialog(this)
        }
        SetInitListner()
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        imgEdit.setOnClickListener(this)
        imgProfilePic.setOnClickListener(this)
        imgPdfDownload.setOnClickListener(this)

        imgInquiry.setOnClickListener(this)
        imgLead.setOnClickListener(this)

        txtNotes.setOnClickListener(this)
        imgNotes.setOnClickListener(this)

        txtAttachments.setOnClickListener(this)
        imgAttachments.setOnClickListener(this)

        txtOpenCalls.setOnClickListener(this)
        txtClosedCalls.setOnClickListener(this)
        imgOpenCalls.setOnClickListener(this)

        txtOpenMeetings.setOnClickListener(this)
        txtClosedMeetings.setOnClickListener(this)
        imgOpenMeetings.setOnClickListener(this)

        txtOpenTasks.setOnClickListener(this)
        txtClosedTasks.setOnClickListener(this)
        imgOpenTasks.setOnClickListener(this)

        txtInquiry.setOnClickListener(this)
        txtLead.setOnClickListener(this)

        LLActivityLog.setOnClickListener(this)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                onBackPressed()
            }
            R.id.imgEdit -> {
                preventTwoClick(v)
                val intent = Intent(this@LeadDashboardActivity, AddLeadActivity::class.java)
                intent.putExtra(AppConstant.STATE,AppConstant.S_EDIT)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("GUID",LeadGUID)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
            R.id.imgProfilePic -> {
                preventTwoClick(v)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
                            photopickerPassport()
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
                            photopickerPassport()
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
            }
            R.id.imgPdfDownload -> {
                preventTwoClick(v)
                callLeadPdfDownload()
            }
            R.id.imgInquiry -> {
                preventTwoClick(v)
                val intent = Intent(this@LeadDashboardActivity, AddNBActivity::class.java)
                intent.putExtra(AppConstant.STATE,AppConstant.S_ADD)
                intent.putExtra("LeadID", LeadID)
                intent.putExtra("LeadGUID", LeadGUID)
                intent.putExtra("LeadName", txtName.text.toString())
                intent.putExtra("LeadType", arrayListLead!!.LeadStage)
                intent.putExtra("AddMore",false)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
            R.id.imgLead -> {
                preventTwoClick(v)
                val intent = Intent(this@LeadDashboardActivity, AddLeadListActivity::class.java)
                intent.putExtra("LeadID", LeadID)
                intent.putExtra("LeadGUID", LeadGUID)
                intent.putExtra("LeadName", txtName.text.toString())
                intent.putExtra("LeadType", arrayListLead!!.LeadStage)
                intent.putExtra("LeadOwnerID", LeadOwnerID)
                intent.putExtra("LeadOwnerName", LeadOwnerName)
                intent.putExtra("AddMore",false)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
            R.id.txtNotes -> {
                preventTwoClick(v)
                val intent = Intent(this, NotesListActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                startActivity(intent)
            }
            R.id.imgNotes -> {
                preventTwoClick(v)
                val intent = Intent(this, AddNotesActivity::class.java)
                intent.putExtra(AppConstant.STATE,AppConstant.S_ADD)
                intent.putExtra("LeadID",LeadID)
                startActivityForResult(intent, AppConstant.INTENT_1001)

            }
            R.id.txtAttachments -> {
                preventTwoClick(v)
                val intent = Intent(this, AttachmentsListActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                startActivity(intent)
            }
            R.id.imgAttachments -> {
                preventTwoClick(v)
                val intent = Intent(this, AddAttachmentsActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                startActivity(intent)
            }
            R.id.txtOpenCalls -> {
                preventTwoClick(v)
                val intent = Intent(this, CallsListActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                startActivity(intent)
            }
            R.id.imgOpenCalls -> {
                preventTwoClick(v)
                showBottomSheetDialogAddCall()
            }
            R.id.txtClosedCalls -> {
                preventTwoClick(v)
                val intent = Intent(this, CallsListActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("ClosedCall","Completed")
                startActivity(intent)
            }
            R.id.txtOpenMeetings -> {
                preventTwoClick(v)
                val intent = Intent(this, MeetingsListActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                startActivity(intent)
            }
            R.id.txtClosedMeetings -> {
                preventTwoClick(v)
                val intent = Intent(this, MeetingsListActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("ClosedMeeting",4)
                startActivity(intent)
            }
            R.id.imgOpenMeetings -> {
                preventTwoClick(v)
                val intent = Intent(this, AddMeetingsActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra(AppConstant.STATE,AppConstant.S_ADD)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
            R.id.imgOpenTasks -> {
                preventTwoClick(v)
                val intent = Intent(this, AddTaskLogsActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra(AppConstant.STATE,AppConstant.S_ADD)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
            R.id.txtOpenTasks -> {
                preventTwoClick(v)
                val intent = Intent(this, TasksListActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                startActivity(intent)
            }
            R.id.txtClosedTasks -> {
                preventTwoClick(v)
                val intent = Intent(this, TasksListActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("ClosedTask","Completed")
                startActivity(intent)
            }
            R.id.txtInquiry -> {
                preventTwoClick(v)
                val intent = Intent(this, InquiryListActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("GUID",LeadGUID)
                startActivity(intent)
            }
            R.id.txtLead -> {
                preventTwoClick(v)
                val intent = Intent(this, LeadListActivity::class.java)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("GUID",LeadGUID)
                startActivity(intent)
            }
            R.id.LLActivityLog -> {
                preventTwoClick(v)
                val intent = Intent(this, LeadActivityLog::class.java)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("GUID",LeadGUID)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        callManageLeadGUID()
    }
    // attachment
    private fun photopickerPassport() {
        FilePickerBuilder.instance
            .setMaxCount(1)
            .setSelectedFiles(ImagePaths)
            .setActivityTheme(R.style.LibAppTheme)
            .pickPhoto(this, 20111)
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

                        LogUtil.d(TAG,"==>Hello "+imageUri)
                        imgProfilePic.setImageURI(imageUri)
                        imageURI = imageUri
                        callUploadImage(LeadGUID)
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
            AppConstant.INTENT_1001 -> {
                if (isOnline(this)) {
                    callManageLeadGUID()
                } else {
                    internetErrordialog(this)
                }
            }
        }
    }

    private fun callManageLeadGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("LeadGUID", LeadGUID)

        val call = ApiUtils.apiInterface.ManageLeadsFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<LeadByGUIDResponse> {
            override fun onResponse(call: Call<LeadByGUIDResponse>, response: Response<LeadByGUIDResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListLead = response.body()?.Data!!
                        setAPIData(arrayListLead!!)
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<LeadByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun callLeadPdfDownload() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("LeadGUID", LeadGUID)

        val call = ApiUtils.apiInterface.ManageLeadReportPDF(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val pdfURL = response.body()?.Data!!
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(pdfURL)
                        }
                        startActivity(intent)

                        /*val pdfURL = response.body()?.Data ?: return
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(pdfURL)
                            type = "application/pdf"
                            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                        }
                        val chooser = Intent.createChooser(intent, "Open PDF")
                        startActivity(chooser)*/
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun setAPIData(model: LeadModel) {
        if(!model.FirstName.isNullOrEmpty()) {
            txtName.text = model.FirstName +" "+ model.LastName
        }
        if(!model.EmailID.isNullOrEmpty()) {
            txtEmail.text = model.EmailID
            txtEmail.visible()
        } else {
            txtEmail.gone()
        }
        if(!model.MobileNo.isNullOrEmpty()) {
            txtMobile.text = model.MobileNo
        }
        if(model.LeadOwnerID != 0 && model.LeadOwnerID != null) {
            LeadOwnerID = model.LeadOwnerID
            callManageUsers(LeadOwnerID!!)
        }
        if(!model.LeadOwnerName.isNullOrEmpty()) {
            LeadOwnerName = model.LeadOwnerName
        }
        if(model.TotalNote != null && model.TotalNote != 0) {
            imgNotesCounts.text = " ("+model.TotalNote.toString()+") "
        }
        if(model.TotalMeeting != null && model.TotalMeeting != 0) {
            imgMeetingsCounts.text = " ("+model.TotalMeeting.toString()+") "
        }
        if(model.TotalTask != null && model.TotalTask != 0) {
            imgTasksCounts.text = " ("+model.TotalTask.toString()+") "
        }
        if(model.TotalCall != null && model.TotalCall != 0) {
            imgCallsCounts.text = " ("+model.TotalCall.toString()+") "
        }
        if(model.TotalAttachment != null && model.TotalAttachment != 0) {
            imgAttachmentsCounts.text = " ("+model.TotalAttachment.toString()+") "
        }
        if (model.LeadImage != null && model.LeadImage != "") {
            imgProfilePic.loadUrl(model.LeadImage, R.drawable.ic_camera)
        }
        if (model.CategoryID != null && model.CategoryID != 0) {
            rating_bar.rating = model.CategoryID!!.toFloat()
            if (model.LeadStage != null && model.LeadStage != 0){
                if (model.LeadStage == 1) {
                    rating_bar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gold))
                } else if (model.LeadStage == 2) {
                    rating_bar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.silver))
                }
            }
        }
        if(!model.FamilyDetails.isNullOrEmpty() && model.FamilyDetails!!.size > 0) {
            arrayListFamilyMember = model.FamilyDetails!!
            val adapter = FamilyMemberAdapter(this, arrayListFamilyMember, this@LeadDashboardActivity)
            rvFamilyMember.adapter = adapter
            LLFamilyMember.visible()
        } else {
            LLFamilyMember.gone()
        }
    }

    private fun callUploadImage(referenceGUID: String?) {

        val partsList: java.util.ArrayList<MultipartBody.Part> = java.util.ArrayList()

        if (imageURI != null) {
            partsList.add(CommonUtil.prepareFilePart(this, "image/*", "LeadImage", imageURI!!))
        } else {
            val attachmentEmpty: RequestBody = RequestBody.create(MediaType.parse("text/plain"), "")
            partsList.add(MultipartBody.Part.createFormData("LeadImage", "", attachmentEmpty))
        }

        var mreferenceGUID = CommonUtil.createPartFromString(referenceGUID.toString())

        val call = ApiUtils.apiInterface.ManageLeadImage(
            ReferenceGUID = mreferenceGUID,
            attachment = partsList
        )
        call.enqueue(object : Callback<LeadImageResponse> {
            override fun onResponse(
                call: Call<LeadImageResponse>,
                response: Response<LeadImageResponse>
            ) {

                if (response.code() == 200) {

                    if (response.body()?.Status == 200) {

                        val sharedPreference = SharedPreference(this@LeadDashboardActivity)

                        if (sharedPreference.getPreferenceString(PrefConstants.PREF_USER_GUID)!! == LeadGUID) {
                            val leadImage = response.body()?.Data!!.LeadImage

                            if (leadImage != null && leadImage != "") {
                                sharedPreference.setPreference(
                                    PrefConstants.PREF_USER_IMAGE,
                                    leadImage
                                )
                            }
                        }

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

            override fun onFailure(call: Call<LeadImageResponse>, t: Throwable) {
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
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    //EasyPermissions.PermissionCallbacks
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    private fun showBottomSheetDialogAddCall() {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.SheetDialog)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_add_calls)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        val txtScheduleCall = bottomSheetDialog.findViewById<TextView>(R.id.txtScheduleCall)
        val txtLogCall = bottomSheetDialog.findViewById<TextView>(R.id.txtLogCall)
        val txtCancel = bottomSheetDialog.findViewById<TextView>(R.id.txtCancel)

        txtCancel!!.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        txtScheduleCall!!.setOnClickListener {
            bottomSheetDialog.dismiss()
            val intent = Intent(this, AddCallLogsActivity::class.java)
            intent.putExtra(AppConstant.STATE,AppConstant.S_ADD)
            intent.putExtra("LeadID",LeadID)
            intent.putExtra("CALLTYPE","SCHEDULE")
            startActivityForResult(intent, AppConstant.INTENT_1001)

        }

        txtLogCall!!.setOnClickListener {
            bottomSheetDialog.dismiss()
            val intent = Intent(this, AddCallLogsActivity::class.java)
            intent.putExtra(AppConstant.STATE,AppConstant.S_ADD)
            intent.putExtra("LeadID",LeadID)
            intent.putExtra("CALLTYPE","LOGCALL")
            startActivityForResult(intent, AppConstant.INTENT_1001)

        }
        bottomSheetDialog.show()
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
            bottomSheetDialog.dismiss()
        }

        txtButtonCancel!!.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun callManageUsers(leadownerID: Int) {

        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageUsers(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListUsers = response.body()?.Data!!

                        if(arrayListUsers!!.size > 0) {
                            for(i in 0 until arrayListUsers!!.size) {
                                if(arrayListUsers!![i].ID == leadownerID) {
                                    arrayListUsers!![i].IsSelected = true

                                    mUsersName = arrayListUsers!![i].FirstName!! + " "+ arrayListUsers!![i].LastName!!
                                    txtLeadOwerName.setText(mUsersName + " (Owner)")
                                }
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

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {

                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        TODO("Not yet implemented")
    }
}