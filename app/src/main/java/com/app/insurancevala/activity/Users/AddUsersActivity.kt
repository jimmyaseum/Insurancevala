package com.app.insurancevala.activity.Users

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.FilePickerBuilder
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.activity.DashBoard.HomeActivity
import com.app.insurancevala.activity.Login.LoginActivity
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetUserTypeListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.UserImageResponse
import com.app.insurancevala.model.response.UserModel
import com.app.insurancevala.model.response.UserResponse
import com.app.insurancevala.model.response.UserTypeModel
import com.app.insurancevala.model.response.UserTypeResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.CommonUtil
import com.app.insurancevala.utils.PrefConstants
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.errortint
import com.app.insurancevala.utils.getRequestJSONBody
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.hideKeyboard
import com.app.insurancevala.utils.internetErrordialog
import com.app.insurancevala.utils.isOnline
import com.app.insurancevala.utils.isValidEmail
import com.app.insurancevala.utils.preventTwoClick
import com.app.insurancevala.utils.toast
import com.app.insurancevala.utils.visible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.activity_add_user.*
import kotlinx.android.synthetic.main.activity_add_user.layout
import kotlinx.android.synthetic.main.activity_add_user.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddUsersActivity : BaseActivity(), View.OnClickListener, EasyPermissions.PermissionCallbacks {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var UserGUID: String? = null
    var IsFrom: String? = null

    var arrayListUsertype: ArrayList<UserTypeModel>? = ArrayList()
    var mUsertype: String = ""
    var mUsertypeID: Int = 0

    val RC_FILE_PICKER_PERM = 900
    var ImagePaths = ArrayList<String>()
    var imageURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
        UserGUID = intent.getStringExtra("UserGUID")
        IsFrom = intent.getStringExtra("IsFrom")

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    override fun initializeView() {
        if (state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Add User"
            setMasterData()
        } else if (state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update User"

            LLPassword.gone()
            LLConfirmPassword.gone()
            cbshowpassword.gone()

            if (isOnline(this)) {
                callManageUsersGUID()
            } else {
                internetErrordialog(this@AddUsersActivity)
            }
        }
        SetInitListner()
    }

    private fun setMasterData() {
        if (isOnline(this)) {
            callManageUserType(0)
        } else {
            internetErrordialog(this@AddUsersActivity)
        }
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        cbshowpassword.setOnClickListener(this)
        imgProfilePic.setOnClickListener(this)

        edtUserType.setOnClickListener(this)
        txtSave.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                onBackPressed()
            }

            R.id.cbshowpassword -> {
                if (cbshowpassword.isChecked) {
                    edtPassword.setTransformationMethod(null)
                    edtConfirmPassword.setTransformationMethod(null)
                } else {
                    edtPassword.setTransformationMethod(PasswordTransformationMethod())
                    edtConfirmPassword.setTransformationMethod(PasswordTransformationMethod())
                }
            }

            R.id.edtUserType -> {
                preventTwoClick(v)
                if (arrayListUsertype.isNullOrEmpty()) {
                    callManageUserType(1)
                } else {
                    selectUserTypeDialog()
                }
            }

            R.id.imgProfilePic -> {
                preventTwoClick(v)
                showAttachmentBottomSheetDialog()
            }

            R.id.txtSave -> {
                preventTwoClick(v)
                validation()
            }
        }
    }

    private fun callManageUserType(mode: Int) {
        if (mode == 1) {
            showProgress()
        }
        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageUserType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserTypeResponse> {
            override fun onResponse(
                call: Call<UserTypeResponse>,
                response: Response<UserTypeResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListUsertype = response.body()?.Data!!

                        if (arrayListUsertype!!.size > 0) {
                            for (i in 0 until arrayListUsertype!!.size) {
                                if (state.equals(AppConstant.S_EDIT)) {
                                    if (arrayListUsertype!![i].ID == mUsertypeID) {
                                        arrayListUsertype!![i].IsSelected = true
                                        mUsertypeID = arrayListUsertype!![i].ID!!
                                        mUsertype = arrayListUsertype!![i].UserType!!
                                        edtUserType.setText(mUsertype)
                                        edtUserType.setError(null)
                                    }
                                }
                            }
                        }

                        if (mode == 1) {
                            selectUserTypeDialog()
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

            override fun onFailure(call: Call<UserTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }

    private fun selectUserTypeDialog() {
        var dialogSelectUserType = Dialog(this)
        dialogSelectUserType.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectUserType.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectUserType.window!!.attributes)

        dialogSelectUserType.window!!.attributes = lp
        dialogSelectUserType.setCancelable(true)
        dialogSelectUserType.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectUserType.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectUserType.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectUserType.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectUserType.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectUserType.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectUserType.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectUserType.dismiss()
        }

        txtid.text = "Select User Type"

        val itemAdapter = BottomSheetUserTypeListAdapter(this, arrayListUsertype!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mUsertype = arrayListUsertype!![pos].UserType!!
                mUsertypeID = arrayListUsertype!![pos].ID!!
                edtUserType.setText(mUsertype)
                dialogSelectUserType!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListUsertype!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<UserTypeModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListUsertype!!) {
                        if (model.UserType!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetUserTypeListAdapter(this@AddUsersActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mUsertype = arrItemsFinal1!![pos].UserType!!
                            mUsertypeID = arrItemsFinal1!![pos].ID!!
                            edtUserType.setText(mUsertype)
                            dialogSelectUserType!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetUserTypeListAdapter(this@AddUsersActivity, arrayListUsertype!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mUsertype = arrayListUsertype!![pos].UserType!!
                            mUsertypeID = arrayListUsertype!![pos].ID!!
                            edtUserType.setText(mUsertype)
                            dialogSelectUserType!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectUserType!!.show()
    }

    private fun validation() {
        when (isValidate()) {
            true -> {
                if (isOnline(this)) {
                    ManageCreateUserAPI()
                } else {
                    internetErrordialog(this@AddUsersActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isvalidate = true

        if (edtFirstName.text.toString().trim().isEmpty()) {
            edtFirstName.setError(getString(R.string.error_empty_first_name), errortint(this))
            isvalidate = false
        }
        if (edtLastName.text.toString().trim().isEmpty()) {
            edtLastName.setError(getString(R.string.error_empty_last_name), errortint(this))
            isvalidate = false
        }
        if (edtMobileNo.text.toString().trim().isEmpty()) {
            edtMobileNo.setError(getString(R.string.error_empty_mobile_number), errortint(this))
            isvalidate = false
        }
        if (edtMobileNo.text.toString().trim().length < 10) {
            edtMobileNo.setError(getString(R.string.error_valid_mobile_number), errortint(this))
            isvalidate = false
        }
        if (edtEmailAddress.text.toString().trim().isEmpty()) {
            edtEmailAddress.setError(getString(R.string.error_empty_email), errortint(this))
            isvalidate = false
        }
        if (!edtEmailAddress.text.toString().trim().isValidEmail()) {
            edtEmailAddress.setError(getString(R.string.error_valid_email), errortint(this))
            isvalidate = false
        }
        if (edtUserType.text.toString().trim().isEmpty()) {
            edtUserType.setError(getString(R.string.error_empty_usertype), errortint(this))
            isvalidate = false
        }

        if (state.equals(AppConstant.S_ADD)) {
            if (edtPassword.text.toString().trim().isEmpty()) {
                edtPassword.setError("Enter Password", errortint(this))
                isvalidate = false
            }
            if (edtPassword.text.toString().length < 6) {
                edtPassword.setError(
                    "The password should be at least 6 characters.",
                    errortint(this)
                )
                isvalidate = false
            }
//        if(!isValidPassword(edtPassword.text.toString().trim())) {
//            edtPassword.setError("The password should contain at least one uppercase letter, one lowercase letter, and one number.", errortint(this))
//            isvalidate =  false
//        }
            if (edtConfirmPassword.text.toString().trim().isEmpty()) {
                edtConfirmPassword.setError("Enter confirm password", errortint(this))
                isvalidate = false
            }
            if (edtConfirmPassword.text.toString().length < 6) {
                edtConfirmPassword.setError(
                    "The confirm password should be at least 6 characters.",
                    errortint(this)
                )
                isvalidate = false
            }
//        if(!isValidPassword(edtConfirmPassword.text.toString().trim())) {
//            edtConfirmPassword.setError("The confirm password should contain at least one uppercase letter, one lowercase letter, and one number.", errortint(this))
//            isvalidate =  false
//        }
            if (!edtConfirmPassword.text.toString().trim()
                    .equals(edtPassword.text.toString().trim())
            ) {
                edtConfirmPassword.setError(
                    getString(R.string.error_mismatch_password),
                    errortint(this)
                )
                isvalidate = false
            }
        }
        return isvalidate
    }

    private fun ManageCreateUserAPI() {

        showProgress()

        val jsonObject = JSONObject()
        jsonObject.put("FirstName", edtFirstName.text.toString().trim())
        jsonObject.put("LastName", edtLastName.text.toString().trim())
        jsonObject.put("MobileNo", edtMobileNo.text.toString().trim())
        jsonObject.put("AlternateMobileNo", edtAlternateMobileNo.text.toString().trim())
        jsonObject.put("EmailID", edtEmailAddress.text.toString().trim())
        jsonObject.put("AlternateEmailID", edtAlternateMobileNo.text.toString().trim())
        jsonObject.put("UserTypeID", mUsertypeID)
        jsonObject.put("IsActive", true)

        if (state.equals(AppConstant.S_ADD)) {
            jsonObject.put("Password", edtPassword.text.toString().trim())
            jsonObject.put("OperationType", AppConstant.INSERT)
        } else if (state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("UserGUID", UserGUID)
            jsonObject.put("OperationType", AppConstant.EDIT)
        }

        val call = ApiUtils.apiInterface.ManageUsers(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 201) {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()

                        var ReferenceGUID = response.body()?.Data!![0]!!.ReferenceGUID.toString()

                        if (imageURI != null) {
                            CallUploadImage(ReferenceGUID)
                        } else {
                            val intent = Intent()
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    } else if (response.body()?.Status == 200) {
                        var ReferenceGUID = response.body()?.Data!![0]!!.ReferenceGUID.toString()

                        if (imageURI != null) {
                            CallUploadImage(ReferenceGUID)
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

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
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

    private fun CallUploadImage(referenceGUID: String?) {

        showProgress()

        val partsList: java.util.ArrayList<MultipartBody.Part> = java.util.ArrayList()

        if (imageURI != null) {
            partsList.add(CommonUtil.prepareFilePart(this, "image/*", "UserImage", imageURI!!))
        } else {
            val attachmentEmpty: RequestBody = RequestBody.create(MediaType.parse("text/plain"), "")
            partsList.add(MultipartBody.Part.createFormData("UserImage", "", attachmentEmpty))
        }

        var mreferenceGUID = CommonUtil.createPartFromString(referenceGUID.toString())

        val call = ApiUtils.apiInterface.ManageUserImage(
            ReferenceGUID = mreferenceGUID,
            attachment = partsList
        )
        call.enqueue(object : Callback<UserImageResponse> {
            override fun onResponse(
                call: Call<UserImageResponse>,
                response: Response<UserImageResponse>
            ) {

                if (response.code() == 200) {

                    if (response.body()?.Status == 200) {

                        hideProgress()

                        /*val userimage = response.body()?.Data!!.UserImage

                        if (userimage != null && userimage != "") {
                            val sharedPreference = SharedPreference(this@AddUsersActivity)
                            sharedPreference.setPreference(PrefConstants.PREF_USER_IMAGE, userimage)
                        }*/

                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        hideProgress()

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

            override fun onFailure(call: Call<UserImageResponse>, t: Throwable) {

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

    private fun callManageUsersGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETBYGUID)
        jsonObject.put("UserGUID", UserGUID)

        val call = ApiUtils.apiInterface.ManageUsers(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListLead = response.body()?.Data!!
                        setAPIData(arrayListLead[0])
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

    private fun setAPIData(model: UserModel) {

        if (model.FirstName != null && model.FirstName != "") {
            edtFirstName.setText(model.FirstName)
        }
        if (model.LastName != null && model.LastName != "") {
            edtLastName.setText(model.LastName)
        }
        if (model.MobileNo != null && model.MobileNo != "") {
            edtMobileNo.setText(model.MobileNo)
        }
        if (model.AlternateMobileNo != null && model.AlternateMobileNo != "") {
            edtAlternateMobileNo.setText(model.AlternateMobileNo)
        }
        if (model.EmailID != null && model.EmailID != "") {
            edtEmailAddress.setText(model.EmailID)
        }
        if (model.AlternateEmailID != null && model.AlternateEmailID != "") {
            edtAlternateEmailAddress.setText(model.AlternateEmailID)
        }
        if (model.UserTypeID != null && model.UserTypeID != 0) {
            edtUserType.setText(model.UserType)
            mUsertype = model.UserType!!
            mUsertypeID = model.UserTypeID
        }

        if (model.UserImage != null && model.UserImage != "") {
            Glide.with(this)
                .load(model.UserImage)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(imgProfilePic)
        }

        callManageUserType(0)
    }

    private fun showAttachmentBottomSheetDialog() {
        if (Build.VERSION.SDK_INT <= 32) {
            if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (EasyPermissions.hasPermissions(
                            this,
                            Manifest.permission.READ_MEDIA_IMAGES
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

            } else {
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.msg_permission_storage),
                    RC_FILE_PICKER_PERM,
                    FilePickerConst.PERMISSIONS_FILE_PICKER
                )
            }
        } else {
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

        }
    }

    private fun photopicker() {
        FilePickerBuilder.instance
            .setMaxCount(1)
            .setSelectedFiles(ImagePaths)
            .setActivityTheme(R.style.LibAppTheme)
            .pickPhoto(this, 20111)
    }

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

                        imgProfilePic.setImageURI(imageUri)
                        imageURI = imageUri

                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val result: CropImage.ActivityResult = CropImage.getActivityResult(data)

                    val error = result.error
                    toast("No apps can perform this action.", Toast.LENGTH_LONG)
                }
            }

            20111 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {

                    ImagePaths = java.util.ArrayList()
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


}