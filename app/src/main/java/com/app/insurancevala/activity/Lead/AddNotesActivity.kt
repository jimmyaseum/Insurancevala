package com.app.insurancevala.activity.Lead

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.FilePickerBuilder
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.MultipleAttachmentListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.pojo.DocumentsModel
import com.app.insurancevala.model.response.NoteByGUIDResponse
import com.app.insurancevala.model.response.NoteModel
import com.app.insurancevala.model.response.RefGUIDResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.activity_add_notes_logs.*
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
import java.util.*

class AddNotesActivity : BaseActivity(), View.OnClickListener,RecyclerClickListener, EasyPermissions.PermissionCallbacks {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var ID: Int? = null
    var LeadID: Int? = null
    var Lead: Boolean? = false
    var NoteGUID: String? = null
    var ReferenceGUID: String? = null

    // attachments
    var arrayListAttachment: ArrayList<DocumentsModel>? = null
    lateinit var adapter: MultipleAttachmentListAdapter
    val RC_FILE_PICKER_PERM = 900
    var ImagePaths = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes_logs)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        if (intent.hasExtra("Lead")) {
            Lead = intent.getBooleanExtra("Lead", false)
        }
        state = intent.getStringExtra(AppConstant.STATE)
        ID = intent.getIntExtra("ID",0)
        LeadID = intent.getIntExtra("LeadID",0)
        NoteGUID = intent.getStringExtra("NoteGUID")
    }

    override fun initializeView() {
        if(state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Create Note"
        } else if(state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update Note"
            if (isOnline(this)) {
                callManageNotesGUID()
            } else {
                internetErrordialog(this@AddNotesActivity)
            }
        }
        SetInitListner()
    }

    private fun SetInitListner() {

        // attachment
        rvAttachment.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvAttachment.isNestedScrollingEnabled = false

        arrayListAttachment = ArrayList()
        adapter = MultipleAttachmentListAdapter(this,true, arrayListAttachment, this)
        rvAttachment.adapter = adapter

        imgBack.setOnClickListener(this)
        txtSave.setOnClickListener(this)

        edtAttachments.setOnClickListener(this)

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
            R.id.edtAttachments -> {
                preventTwoClick(v)
                showAttachmentBottomSheetDialog()
            }
        }
    }

    private fun validation() {
        when (isValidate()) {
            true -> {
                if (isOnline(this)) {
                    ManageCreateNotesAPI()
                } else {
                    internetErrordialog(this@AddNotesActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isValidate = true

        if (edtTitle.text.isEmpty()) {
            edtTitle.setError("Enter Title",errortint(this))
            isValidate = false
        }
        if (edtDescription.text.isEmpty()) {
            edtDescription.setError("Enter Description",errortint(this))
            isValidate = false
        }

        return isValidate
    }

    private fun ManageCreateNotesAPI() {

        showProgress()

        val jsonObject = JSONObject()
        if (Lead!!) {
            jsonObject.put("NBLeadTypeID", ID)
            jsonObject.put("NBInquiryTypeID", null)
        } else {
            jsonObject.put("NBLeadTypeID", null)
            jsonObject.put("NBInquiryTypeID", ID)
        }
        jsonObject.put("Title", edtTitle.text.toString().trim())
        jsonObject.put("Description", edtDescription.text.toString().trim())
        jsonObject.put("LeadID", LeadID)
        jsonObject.put("IsActive", true)

        if(state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("NoteGUID", NoteGUID)
        }

        if(state.equals(AppConstant.S_ADD)) {
            val call = ApiUtils.apiInterface.ManageNoteInsert(getRequestJSONBody(jsonObject.toString()))
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
            val call = ApiUtils.apiInterface.ManageNoteUpdate(getRequestJSONBody(jsonObject.toString()))
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

//                arrayListAttachment!!.add(AttachmentModel(name = displayName!!, attachmentUri = PassportPath, attachmentType = 1))
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
        val mAttachmentType = CommonUtil.createPartFromString(AppConstant.NOTE)
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

    private fun callManageNotesGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("NoteGUID", NoteGUID)

        val call = ApiUtils.apiInterface.ManageNoteFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<NoteByGUIDResponse> {
            override fun onResponse(call: Call<NoteByGUIDResponse>, response: Response<NoteByGUIDResponse>) {
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

            override fun onFailure(call: Call<NoteByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun setAPIData(model: NoteModel) {

        if(model.Title != null && model.Title != "") {
            edtTitle.setText(model.Title)
        }
        if(model.Description != null && model.Description != "") {
            edtDescription.setText(model.Description)
        }
        txtAttachments.gone()
        edtAttachments.gone()
        viewAttachments.gone()
        if(!model.NotesAttachmentList.isNullOrEmpty()) {
            arrayListAttachment = ArrayList()
            arrayListAttachment = model.NotesAttachmentList
            adapter = MultipleAttachmentListAdapter(this,false, arrayListAttachment, this)

            rvAttachment.adapter = adapter
            txtAttachments.visible()
        }
    }
}