package com.app.insurancevala.activity.Login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.bottomsheetadapter.BottomSheetUserTypeListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.UserTypeModel
import com.app.insurancevala.model.response.UserTypeResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.activity_registration.LLPassword
import kotlinx.android.synthetic.main.activity_registration.cbshowpassword
import kotlinx.android.synthetic.main.activity_registration.edtPassword
import kotlinx.android.synthetic.main.activity_registration.imgBack
import kotlinx.android.synthetic.main.activity_registration.layout
import kotlinx.android.synthetic.main.activity_registration.txtHeader
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : BaseActivity(), View.OnClickListener {

    var arrayListUsertype : ArrayList<UserTypeModel>? = ArrayList()
    var mUsertype : String = ""
    var mUsertypeID : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        SetAnimationControl()

        initializeView()
    }

    override fun initializeView() {
        callManageUserType(0)
        SetInitListner()
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        txtLogin.setOnClickListener(this)
        cbshowpassword.setOnClickListener(this)
        edtUserType.setOnClickListener(this)
        txtButtonSignUp.setOnClickListener(this)

    }
    private fun SetAnimationControl() {

        val topanim = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        val leftanim = AnimationUtils.loadAnimation(this, R.anim.left_anim)
        val rightanim = AnimationUtils.loadAnimation(this, R.anim.right_anim)
        val bottomanim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim)

        txtHeader.animation = topanim
        LLFirstName.animation = leftanim
        LLLastName.animation = rightanim
        LLMobileNo.animation = leftanim
        LLAlternateMobileNo.animation = rightanim
        LLEmailAddress.animation = leftanim
        LLAlternateEmailAddress.animation = rightanim
        LLUserType.animation = leftanim
        LLPassword.animation = rightanim
        LLConfirmPassword.animation = leftanim
        cbshowpassword.animation = rightanim
        llBottom.animation = bottomanim
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }
            R.id.txtLogin -> {
                preventTwoClick(v)
                val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.cbshowpassword -> {
                if(cbshowpassword.isChecked) {
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
            R.id.txtButtonSignUp -> {
                preventTwoClick(v)
                validation()
            }
        }
    }

    private fun validation() {
        when (isValidate()) {
            true -> {
                if (isOnline(this)) {
                    ManageCreateUserAPI()
                } else {
                    internetErrordialog(this@RegistrationActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

    var isvalidate = true

    if (edtFirstName.text.toString().trim().isEmpty()) {
        edtFirstName.setError(getString(R.string.error_empty_first_name), errortint(this))
        isvalidate =  false
    }
    if (edtLastName.text.toString().trim().isEmpty()) {
        edtLastName.setError(getString(R.string.error_empty_last_name), errortint(this))
        isvalidate =  false
    }
    if (edtMobileNo.text.toString().trim().isEmpty()) {
        edtMobileNo.setError(getString(R.string.error_empty_mobile_number), errortint(this))
        isvalidate =  false
    }
    if (edtMobileNo.text.toString().trim().length < 10) {
        edtMobileNo.setError(getString(R.string.error_valid_mobile_number), errortint(this))
        isvalidate =  false
    }
    if (edtEmailAddress.text.toString().trim().isEmpty()) {
        edtEmailAddress.setError(getString(R.string.error_empty_email), errortint(this))
        isvalidate =  false
    }
    if (!edtEmailAddress.text.toString().trim().isValidEmail()) {
        edtEmailAddress.setError(getString(R.string.error_valid_email), errortint(this))
        isvalidate =  false
    }
    if (edtUserType.text.toString().trim().isEmpty()) {
        edtUserType.setError(getString(R.string.error_empty_usertype), errortint(this))
        isvalidate =  false
    }
    if (edtPassword.text.toString().trim().isEmpty()) {
        edtPassword.setError("Enter Password", errortint(this))
        isvalidate = false
    }
    if(edtPassword.text.toString().length < 6){
        edtPassword.setError("The password should be at least 6 characters.", errortint(this))
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
    if(edtConfirmPassword.text.toString().length < 6){
        edtConfirmPassword.setError("The confirm password should be at least 6 characters.", errortint(this))
        isvalidate = false
    }
//        if(!isValidPassword(edtConfirmPassword.text.toString().trim())) {
//            edtConfirmPassword.setError("The confirm password should contain at least one uppercase letter, one lowercase letter, and one number.", errortint(this))
//            isvalidate =  false
//        }
    if (!edtConfirmPassword.text.toString().trim().equals(edtPassword.text.toString().trim())) {
        edtConfirmPassword.setError(getString(R.string.error_mismatch_password), errortint(this))
        isvalidate =  false
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
        jsonObject.put("Password", edtPassword.text.toString().trim())
        jsonObject.put("UserTypeID", mUsertypeID)
        jsonObject.put("IsActive", true)
        jsonObject.put("OperationType", AppConstant.INSERT)

        val call = ApiUtils.apiInterface.ManageUserType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserTypeResponse> {
            override fun onResponse(call: Call<UserTypeResponse>, response: Response<UserTypeResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 201) {
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

            override fun onFailure(call: Call<UserTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        })

    }

    private fun callManageUserType(mode: Int) {
        if(mode == 1) {
            showProgress()
        }
        var jsonObject = JSONObject()
        jsonObject.put("OperationType",AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageUserType(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserTypeResponse> {
            override fun onResponse(call: Call<UserTypeResponse>, response: Response<UserTypeResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListUsertype = response.body()?.Data!!

                        if (mode == 1) {
                            selectUserTypeDialog()
                        }
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<UserTypeResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
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

        dialogSelectUserType.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectUserType.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectUserType.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectUserType.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectUserType.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectUserType.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectUserType.dismiss()
        }

        txtid.text = "Select User Type"

        val itemAdapter = BottomSheetUserTypeListAdapter(this, arrayListUsertype!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mUsertype = arrayListUsertype!![pos].UserType!!
                mUsertypeID = arrayListUsertype!![pos].ID!!
                edtUserType.setText(mUsertype)
                dialogSelectUserType!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if(arrayListUsertype!!.size > 6) {
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
                val arrItemsFinal1: ArrayList<UserTypeModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListUsertype!!) {
                        if (model.UserType!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetUserTypeListAdapter(this@RegistrationActivity, arrItemsFinal1)
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
                    val itemAdapter = BottomSheetUserTypeListAdapter(this@RegistrationActivity, arrayListUsertype!!)
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
}