package com.app.insurancevala.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.FilePickerBuilder
import com.app.insurancevala.R
import com.app.insurancevala.adapter.BrochureMultipleAttachmentListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetCompanyListAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetPlanListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.pojo.DocumentsModel
import com.app.insurancevala.model.response.CompanyModel
import com.app.insurancevala.model.response.CompanyResponse
import com.app.insurancevala.model.response.PlanBrochureByIDResponse
import com.app.insurancevala.model.response.PlanBrochuresModel
import com.app.insurancevala.model.response.PlanModel
import com.app.insurancevala.model.response.PlanResponse
import com.app.insurancevala.model.response.RefIDResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.CommonUtil
import com.app.insurancevala.utils.LogUtil
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.TAG
import com.app.insurancevala.utils.errortint
import com.app.insurancevala.utils.getRequestJSONBody
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.hideKeyboard
import com.app.insurancevala.utils.internetErrordialog
import com.app.insurancevala.utils.isOnline
import com.app.insurancevala.utils.preventTwoClick
import com.app.insurancevala.utils.toast
import com.app.insurancevala.utils.visible
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.icon
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.position
import com.example.awesomedialog.title
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.activity_add_brochure.*
import kotlinx.android.synthetic.main.activity_add_brochure.view.*
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
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class AddBrochureActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener,
    EasyPermissions.PermissionCallbacks {

    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var PlanBrochureID: Int? = null

    var arrayListCompany: ArrayList<CompanyModel>? = ArrayList()
    var arrayListCompanyNew: ArrayList<CompanyModel>? = ArrayList()
    var mCompany: String = ""
    var mCompanyID: Int = 0

    var arrayListPlan: ArrayList<PlanModel>? = ArrayList()
    var mPlan: String = ""
    var mPlanID: Int = 0
    var mPlanPostion: Int = 0

    val RC_FILE_PICKER_PERM = 900
    var ImagePaths = ArrayList<String>()
    var arrayListAttachment: ArrayList<DocumentsModel>? = null
    lateinit var adapter: BrochureMultipleAttachmentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_brochure)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
        PlanBrochureID = intent.getIntExtra("PlanBrochureID", 0)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    override fun initializeView() {
        if (state.equals(AppConstant.S_ADD)) {
            txtHearderText.text = "Add Plan Brochure"
            setMasterData()
        } else if (state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update Plan Brochure"
            if (isOnline(this)) {
                callManagePlanBrochureID()
            } else {
                internetErrordialog(this@AddBrochureActivity)
            }
        }
        SetInitListner()
    }

    private fun setMasterData() {
        if (isOnline(this)) {

        } else {
            internetErrordialog(this@AddBrochureActivity)
        }
    }

    private fun SetInitListner() {
        // attachment
        rvAttachment.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvAttachment.isNestedScrollingEnabled = false

        arrayListAttachment = ArrayList()
        adapter = BrochureMultipleAttachmentListAdapter(this, arrayListAttachment, this)
        rvAttachment.adapter = adapter

        edtCompanyName.setOnClickListener(this)
        edtPlanName.setOnClickListener(this)
        imgBack.setOnClickListener(this)
        imgAddAttachment.setOnClickListener(this)
        txtSave.setOnClickListener(this)

        callCompanyName(0, false)
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.edtCompanyName -> {
                preventTwoClick(v)
                if (!arrayListCompany.isNullOrEmpty()) {
                    selectCompanyNameDialog()
                } else {
                    callCompanyName(1, true)
                }
            }

            R.id.edtPlanName -> {
                preventTwoClick(v)
                if (!arrayListPlan.isNullOrEmpty()) {
                    selectPlanDialog()
                } else {
                    callPlanName(1, 0)
                }
            }

            R.id.imgBack -> {
                preventTwoClick(v)
                onBackPressed()
            }

            R.id.txtSave -> {
                preventTwoClick(v)
                validation()
            }

            R.id.imgAddAttachment -> {
                preventTwoClick(v)
                showAttachmentBottomSheetDialog()
            }
        }
    }

    private fun validation() {
        when (isValidate()) {
            true -> {
                if (isOnline(this)) {
                    ManageCreatePlanBrochureAPI()
                } else {
                    internetErrordialog(this@AddBrochureActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isvalidate = true

        if (edtCompanyName.text.toString().trim().isEmpty()) {
            edtCompanyName.setError(getString(R.string.error_empty_company_name), errortint(this))
            isvalidate = false
        }

        if (edtPlanName.text.toString().trim().isEmpty()) {
            edtPlanName.setError(getString(R.string.error_empty_plan_name), errortint(this))
            isvalidate = false
        }
        return isvalidate
    }

    private fun callCompanyName(mode: Int, boolean: Boolean) {
        if (mode == 1) {
            showProgress()
        }
        val call = ApiUtils.apiInterface.getCompanyAllActive()
        call.enqueue(object : Callback<CompanyResponse> {
            override fun onResponse(
                call: Call<CompanyResponse>,
                response: Response<CompanyResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListCompany = response.body()?.Data!!

                        if (mode == 1 && boolean) {
                            selectCompanyNameDialog()
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

            override fun onFailure(call: Call<CompanyResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectCompanyNameDialog() {
        val dialogSelectCompany = Dialog(this)
        dialogSelectCompany.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectCompany.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectCompany.window!!.attributes)

        dialogSelectCompany.window!!.attributes = lp
        dialogSelectCompany.setCancelable(true)
        dialogSelectCompany.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectCompany.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectCompany.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectCompany.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectCompany.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectCompany.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectCompany.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectCompany.dismiss()
        }

        txtid.text = "Select Company Name"

        val itemAdapter = BottomSheetCompanyListAdapter(this, arrayListCompany!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(view: View, position: Int, type: Int) {

                itemAdapter.updateItem(position)
                mCompanyID = arrayListCompany!![position].ID!!
                mCompany = arrayListCompany!![position].CompanyName!!

                edtCompanyName.setText(arrayListCompany!![position].CompanyName)
                callPlanName(1, arrayListCompany!![position].ID!!)
                dialogSelectCompany.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListCompany!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<CompanyModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListCompany!!) {
                        if (model.CompanyName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }
                    arrayListCompanyNew = arrItemsFinal1
                    val itemAdapter = BottomSheetCompanyListAdapter(
                        this@AddBrochureActivity,
                        arrayListCompanyNew!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(view: View, position: Int, type: Int) {

                            mCompanyID = arrayListCompanyNew!![position].ID!!
                            mCompany = arrayListCompanyNew!![position].CompanyName!!

                            edtCompanyName.setText(arrayListCompanyNew!![position].CompanyName)
                            callPlanName(1, arrayListCompany!![position].ID!!)
                            dialogSelectCompany!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetCompanyListAdapter(this@AddBrochureActivity, arrayListCompany!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(view: View, position: Int, type: Int) {

                            mCompanyID = arrayListCompany!![position].ID!!
                            mCompany = arrayListCompany!![position].CompanyName!!

                            edtCompanyName.setText(arrayListCompany!![position].CompanyName)
                            callPlanName(1, arrayListCompany!![position].ID!!)
                            dialogSelectCompany!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectCompany!!.show()
    }

    private fun callPlanName(mode: Int, CompanyID: Int) {

        if (mode == 1) {
            showProgress()
        }

        var jsonObject = JSONObject()
        jsonObject.put("CompanyID", CompanyID)
        val call =
            ApiUtils.apiInterface.ManagePlanBrochure(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<PlanResponse> {
            override fun onResponse(
                call: Call<PlanResponse>, response: Response<PlanResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListPlan = response.body()?.Data!!

                        if (mode == 1) {
                            selectPlanDialog()
                        }
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<PlanResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectPlanDialog() {
        var dialogSelectPlan = Dialog(this)
        dialogSelectPlan.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectPlan.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectPlan.window!!.attributes)

        dialogSelectPlan.window!!.attributes = lp
        dialogSelectPlan.setCancelable(true)
        dialogSelectPlan.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectPlan.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectPlan.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectPlan.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectPlan.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectPlan.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectPlan.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectPlan.dismiss()
        }

        txtid.text = "Select Plan"

        val itemAdapter = BottomSheetPlanListAdapter(this, arrayListPlan!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(view: View, position: Int, type: Int) {

                itemAdapter.updateItem(position)
                mPlanID = arrayListPlan!![position].ID!!
                mPlan = arrayListPlan!![position].PlanName!!
                edtPlanName.setText(arrayListPlan!![position].PlanName)
                dialogSelectPlan!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListPlan!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<PlanModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListPlan!!) {
                        if (model.PlanName!!.toLowerCase()
                                .contains(strSearch.toLowerCase())
                        ) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetPlanListAdapter(this@AddBrochureActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(view: View, position: Int, type: Int) {

                            mPlanID = arrItemsFinal1!![position].ID!!
                            mPlan = arrItemsFinal1!![position].PlanName!!
                            edtPlanName.setText(arrayListPlan!![position].PlanName)
                            dialogSelectPlan!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetPlanListAdapter(
                        this@AddBrochureActivity, arrayListPlan!!
                    )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(view: View, position: Int, type: Int) {

                            mPlanID = arrayListPlan!![position].ID!!
                            mPlan = arrayListPlan!![position].PlanName!!
                            edtPlanName.setText(arrayListPlan!![position].PlanName)
                            dialogSelectPlan!!.dismiss()

                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectPlan!!.show()
    }

    private fun ManageCreatePlanBrochureAPI() {

        showProgress()

        val jsonObject = JSONObject()
        jsonObject.put("CompanyID", mCompanyID)
        jsonObject.put("PlanID", mPlanID)
        jsonObject.put("IsActive", true)

        if (state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("ID", PlanBrochureID)
        }

        if (state.equals(AppConstant.S_ADD)) {
            val call =
                ApiUtils.apiInterface.ManagePlanBrochureInsert(getRequestJSONBody(jsonObject.toString()))
            call.enqueue(object : Callback<RefIDResponse> {
                override fun onResponse(
                    call: Call<RefIDResponse>,
                    response: Response<RefIDResponse>
                ) {
                    hideProgress()
                    if (response.code() == 200) {
                        if (response.body()?.Status == 201) {

                            Snackbar.make(
                                layout,
                                response.body()?.Details.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()

                            PlanBrochureID = response.body()?.Data!!.PlanBrochureID

                            if (arrayListAttachment!!.size > 0) {
                                CallUploadDocuments(PlanBrochureID)
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

                override fun onFailure(call: Call<RefIDResponse>, t: Throwable) {
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
                ApiUtils.apiInterface.ManagePlanBrochureUpdate(getRequestJSONBody(jsonObject.toString()))
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

                            if (arrayListAttachment!!.size > 0) {
                                CallUploadDocuments(PlanBrochureID)
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

    private fun CallUploadDocuments(ID: Int?) {

        LogUtil.d(TAG, "" + ID)

        showProgress()

        val partsList: java.util.ArrayList<MultipartBody.Part> = java.util.ArrayList()
        val AttachmentName: java.util.ArrayList<String> = java.util.ArrayList()

        if (!arrayListAttachment.isNullOrEmpty()) {
            for (i in arrayListAttachment!!.indices) {
                if (arrayListAttachment!![i].AttachmentType == "Image") {
                    if (arrayListAttachment!![i].AttachmentURL != null && arrayListAttachment!![i].ID == null) {
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
                    if (arrayListAttachment!![i].AttachmentURL != null && arrayListAttachment!![i].ID == null) {
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

        val call = ApiUtils.apiInterface.ManageBrochureAttachments(
            PlanBrochureID = ID,
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

    private fun CallAttachmentDeleteAPI(ID: Int) {

        var jsonObject = JSONObject()
        jsonObject.put("ID", ID)

        val call =
            ApiUtils.apiInterface.ManageBrochureDelete(getRequestJSONBody(jsonObject.toString()))
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

    private fun callManagePlanBrochureID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("ID", PlanBrochureID)

        val call =
            ApiUtils.apiInterface.ManagePlanBrochureFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<PlanBrochureByIDResponse> {
            override fun onResponse(
                call: Call<PlanBrochureByIDResponse>,
                response: Response<PlanBrochureByIDResponse>
            ) {
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

            override fun onFailure(call: Call<PlanBrochureByIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setAPIData(model: PlanBrochuresModel) {

        if (model.CompanyName != null && model.CompanyName != "") {
            edtCompanyName.setText(model.CompanyName)
        }
        mCompanyID = model.CompanyID!!
        if (model.PlanName != null && model.PlanName != "") {
            edtPlanName.setText(model.PlanName)
        }
        mPlanID = model.PlanID!!

        callCompanyName(1, false)
        callPlanName(0, mCompanyID)

        if (!model.PlanBrochureAttachmentList.isNullOrEmpty()) {
            arrayListAttachment = java.util.ArrayList()
            arrayListAttachment = model.PlanBrochureAttachmentList
            adapter = BrochureMultipleAttachmentListAdapter(this, arrayListAttachment, this)

            rvAttachment.adapter = adapter
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

    private fun photopicker() {
        FilePickerBuilder.instance
            .setMaxCount(1)
            .setSelectedFiles(ImagePaths)
            .setActivityTheme(R.style.LibAppTheme)
            .pickPhoto(this, 20111)
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when (view.id) {
            R.id.imgRemove -> {
                removeAdapterItem(position, type)
            }
        }
    }

    // attachment
    private fun removeAdapterItem(position: Int, type: Int) {
        if (::adapter.isInitialized) {
            AwesomeDialog.build(this).title("Warning !!!")
                .body("Are you sure want to delete this Attachment?")
                .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                .onNegative("No") {

                }.onPositive("Yes") {
                    if (type == 1) {
                        CallAttachmentDeleteAPI(arrayListAttachment!![position].ID!!)
                        adapter.removeItem(position)
                    } else {
                        adapter.removeItem(position)
                    }
                }
        }
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

//                arrayListAttachment!!.add(AttachmentModel(name = displayName!!, attachmentUri = PassportPath, attachmentType = 1))
//                adapter.notifyDataSetChanged()
            }
        }
    }

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

    private fun getFileExtension(context: Context, uri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
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

        // Display appropriate icon based on attachment type
        if (attachmenttype == 1) {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileuri.toString())
            when {
                fileExtension != null && fileExtension.equals("xlsx", ignoreCase = true) -> {
                    img!!.setImageResource(R.drawable.excel_icon)
                }

                fileExtension != null && fileExtension.equals("pdf", ignoreCase = true) -> {
                    img!!.setImageResource(R.drawable.pdficon)
                }

                else -> {
                    img!!.setImageResource(R.drawable.file)
                }
            }
        } else {
            img!!.setImageURI(fileuri)
        }

        // Extract file name without extension and set it in EditText
        val fileName = name.substringBeforeLast('.')
        edtName!!.setText(fileName.replace(" ", "_")) // Replace spaces with underscores
        edtName.setSelection(fileName.length) // Set cursor position to end

        // Set focus on EditText
        edtName.requestFocus()

        txtButtonSubmit!!.setOnClickListener {
            if (!edtName.text.toString().trim().isEmpty()) {
                // Append the original extension to the new name
                val newName =
                    "${edtName.text.toString().trim()}.${fileuri.path?.substringAfterLast('.')}"
                val attachmentName = newName.replace(" ", "_")
                adapter.addItem(attachmentName, fileuri, attachmenttype)
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

}