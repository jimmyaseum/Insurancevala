package com.app.insurancevala.activity.Lead

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.app.insurancevala.FilePickerBuilder
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.AllAttachmentListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.pojo.DocumentsModel
import com.app.insurancevala.model.pojo.DocumentsResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.example.awesomedialog.*
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.activity_attachment_list.*
import kotlinx.android.synthetic.main.activity_attachment_list.view.*
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

class AttachmentsListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener,
    EasyPermissions.PermissionCallbacks {

    lateinit var adapter: AllAttachmentListAdapter
    var arrayListAttachment: ArrayList<DocumentsModel>? = ArrayList()
    var arrayListAttachmentNew: ArrayList<DocumentsModel>? = ArrayList()
    var ID: Int? = null
    var LeadID: Int? = null
    var Lead: Boolean? = false

    val RC_FILE_PICKER_PERM = 900
    var ImagePaths = ArrayList<String>()

    var AttachmentURL: Uri? = null
    var AttachmentName: String? = null
    var DocumentType: Int? = null

    //    var ReferenceGUID: String? = null
    var AttachmentID: Int? = null
    var NBInquiryTypeID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attachment_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        if (intent.hasExtra("Lead")) {
            Lead = intent.getBooleanExtra("Lead", false)
        }
        ID = intent.getIntExtra("ID", 0)
        LeadID = intent.getIntExtra("LeadID", 0)
    }

    override fun initializeView() {
        if (isOnline(this@AttachmentsListActivity)) {
            callManageAttachment()
        } else {
            internetErrordialog(this@AttachmentsListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        imgAddAttachment.setOnClickListener(this)

        arrayListAttachment = ArrayList()
        adapter =
            AllAttachmentListAdapter(this, arrayListAttachment!!, this@AttachmentsListActivity)

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
                val arrItemsFinal1: ArrayList<DocumentsModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListAttachment!!) {
                        try {
                            if (model.AttachmentType!!.toLowerCase()
                                    .contains(strSearch.toLowerCase()) ||
                                model.AttachmentName!!.toLowerCase()
                                    .contains(strSearch.toLowerCase())
                            ) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                        }
                    }
                    arrayListAttachmentNew = arrItemsFinal1
                    val itemAdapter = AllAttachmentListAdapter(
                        this@AttachmentsListActivity,
                        arrayListAttachmentNew!!,
                        this@AttachmentsListActivity
                    )
                    RvAttachmentsList.adapter = itemAdapter
                } else {
                    arrayListAttachmentNew = arrayListAttachment
                    val itemAdapter = AllAttachmentListAdapter(
                        this@AttachmentsListActivity,
                        arrayListAttachmentNew!!,
                        this@AttachmentsListActivity
                    )
                    RvAttachmentsList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListAttachmentNew = arrayListAttachment
                val itemAdapter = AllAttachmentListAdapter(
                    this@AttachmentsListActivity,
                    arrayListAttachmentNew!!,
                    this@AttachmentsListActivity
                )
                RvAttachmentsList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(
                    this@AttachmentsListActivity,
                    R.anim.searchview_close_anim
                )
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(
                    this@AttachmentsListActivity,
                    R.anim.searchview_open_anim
                )
            }
        })

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@AttachmentsListActivity, refreshLayout)
            searchView.closeSearch()
            callManageAttachment()
            refreshLayout.isRefreshing = false
        }
    }

    override fun onClick(v: View?) {
        hideKeyboard(this@AttachmentsListActivity, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }
            R.id.imgAddAttachment -> {
                preventTwoClick(v)
                val intent = Intent(this, AddAttachmentsActivity::class.java)
                intent.putExtra("ID",ID)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("Lead", Lead)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when (type) {
            101 -> {
                preventTwoClick(view)
//                ReferenceGUID = arrayListAttachmentNew!![position].ReferenceGUID!!
                AttachmentID = arrayListAttachmentNew!![position].ID!!
                NBInquiryTypeID = arrayListAttachmentNew!![position].NBInquiryTypeID!!
                showAttachmentBottomSheetDialog()
            }

            102 -> {
                preventTwoClick(view)
                AwesomeDialog.build(this)
                    .title("Warning !!!")
                    .body("Are you sure want to delete this File?")
                    .icon(R.drawable.ic_delete)
                    .position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }
                    .onPositive("Yes") {
                        CallAttachmentDeleteAPI(arrayListAttachmentNew!![position].ID!!)
                    }
            }
        }
    }

    private fun callManageAttachment() {

        showProgress()

        var jsonObject = JSONObject()
        if (Lead!!) {
            jsonObject.put("NBLeadTypeID", ID)
            jsonObject.put("NBInquiryTypeID", null)
        } else {
            jsonObject.put("NBLeadTypeID", null)
            jsonObject.put("NBInquiryTypeID", ID)
        }
        jsonObject.put("LeadID", LeadID)
        jsonObject.put("AttachmentType", "")

        val call =
            ApiUtils.apiInterface.ManageAttachmentList(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<DocumentsResponse> {
            override fun onResponse(
                call: Call<DocumentsResponse>,
                response: Response<DocumentsResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListAttachment?.clear()
                        arrayListAttachmentNew?.clear()
                        arrayListAttachment = response.body()?.Data!!
                        arrayListAttachmentNew = arrayListAttachment

                        if (arrayListAttachmentNew!!.size > 0) {
                            adapter = AllAttachmentListAdapter(
                                this@AttachmentsListActivity,
                                arrayListAttachmentNew!!,
                                this@AttachmentsListActivity
                            )
                            RvAttachmentsList.adapter = adapter

                            shimmer.stopShimmer()
                            shimmer.gone()
                            FL.visible()
                            RLNoData.gone()

                        } else {
                            Snackbar.make(
                                layout,
                                response.body()?.Details.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                            shimmer.stopShimmer()
                            shimmer.gone()
                            FL.gone()
                            RLNoData.visible()
                        }
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        shimmer.stopShimmer()
                        shimmer.gone()
                        FL.gone()
                        RLNoData.visible()
                    }
                }
            }

            override fun onFailure(call: Call<DocumentsResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun CallAttachmentDeleteAPI(ID: Int) {

        var jsonObject = JSONObject()
        jsonObject.put("ID", ID)

        val call =
            ApiUtils.apiInterface.ManageAttachmentDelete(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        callManageAttachment()
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

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

    // attachment
    private fun photopicker() {
        FilePickerBuilder.instance
            .setMaxCount(1)
            .setSelectedFiles(ImagePaths)
            .setActivityTheme(R.style.LibAppTheme)
            .pickPhoto(this, 20111)
    }

    // attachment
    @SuppressLint("Range")
    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
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
                        cursor = contentResolver.query(sUri, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                    } finally {
                        cursor!!.close()
                    }
                } else {
                    displayName = sPath!!
                }

                showBottomSheetDialogRename(displayName, PassportPath, 1)

            }

            AppConstant.INTENT_1001 -> {
                if (isOnline(this)) {
                    callManageAttachment()
                } else {
                    internetErrordialog(this)
                }
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

                AttachmentURL = fileuri
                DocumentType = attachmenttype
                AttachmentName = edtName.text.toString()
                bottomSheetDialog.dismiss()

                callManageAttachmentUpdate()

            } else {
                edtName.setError("Enter Name", errortint(this))
            }
        }

        txtButtonCancel!!.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun callManageAttachmentUpdate() {

        showProgress()

        val partsList: ArrayList<MultipartBody.Part> = ArrayList()

        if (DocumentType == 2) {
            if (AttachmentURL != null) {
                partsList.add(CommonUtil.prepareFilePart(this, "image/*", "AttachmentURL", AttachmentURL!!))
            } else {
                val attachmentEmpty: RequestBody = RequestBody.create(MediaType.parse("text/plain"), "")
                partsList.add(MultipartBody.Part.createFormData("AttachmentURL", "", attachmentEmpty))
            }
        } else {
            if (AttachmentURL != null) {
                partsList.add(CommonUtil.prepareFilePart(this, "application/*", "AttachmentURL", AttachmentURL!!))
            } else {
                val attachmentEmpty: RequestBody = RequestBody.create(MediaType.parse("text/plain"), "")
                partsList.add(MultipartBody.Part.createFormData("AttachmentURL", "", attachmentEmpty))
            }
        }

        val mAttachmentName = CommonUtil.createPartFromString(AttachmentName!!)
//        val mreferenceGUID = CommonUtil.createPartFromString(ReferenceGUID!!)

        val call = ApiUtils.apiInterface.ManageAttachmentUpdate(
            ID = AttachmentID,
            NBInquiryTypeID = NBInquiryTypeID,
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
                        callManageAttachment()
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
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
            }
        })
    }
}