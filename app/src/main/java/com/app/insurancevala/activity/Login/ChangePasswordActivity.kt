package com.app.insurancevala.activity.Login

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.AnimationUtils
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.PrefConstants
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.errortint
import com.app.insurancevala.utils.getRequestJSONBody
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.hideKeyboard
import com.app.insurancevala.utils.internetErrordialog
import com.app.insurancevala.utils.isOnline
import com.app.insurancevala.utils.preventTwoClick
import com.app.insurancevala.utils.toast
import com.app.insurancevala.utils.visible
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_change_password .*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : BaseActivity(), View.OnClickListener {

    var sharedPreference: SharedPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
    }

    override fun initializeView() {
        SetAnimationControl1()
        SetInitListner()
    }

    private fun SetAnimationControl1() {
        val topanim = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        val leftanim = AnimationUtils.loadAnimation(this, R.anim.left_anim)
        val rightanim = AnimationUtils.loadAnimation(this, R.anim.right_anim)
        val bottomanim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim)

        txtHeader.animation = topanim
        LLOldPassword.animation = rightanim
        LLNewPassword.animation = leftanim
        LLConfirmNewPassword.animation = rightanim
        cbshowpassword.animation = leftanim
        txtButtonSave.animation = bottomanim
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        txtButtonSave.setOnClickListener(this)
        txtButtonSetOkay.setOnClickListener(this)
        cbshowpassword.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }
            R.id.cbshowpassword -> {
                if (cbshowpassword.isChecked) {
                    edtOldPassword.setTransformationMethod(null)
                    edtNewPassword.setTransformationMethod(null)
                    edtConfirmNewPassword.setTransformationMethod(null)
                } else {
                    edtOldPassword.setTransformationMethod(PasswordTransformationMethod())
                    edtNewPassword.setTransformationMethod(PasswordTransformationMethod())
                    edtConfirmNewPassword.setTransformationMethod(PasswordTransformationMethod())
                }
            }
            R.id.txtButtonSave -> {
                preventTwoClick(v)
                validation()
            }
            R.id.txtButtonSetOkay -> {
                preventTwoClick(v)
                val intent = Intent(this@ChangePasswordActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun validation() {
        when (isValidate()) {
            true -> {
                if (isOnline(this)) {
                    ManagePasswordAPI()
                } else {
                    internetErrordialog(this@ChangePasswordActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isvalidate = true

        if (edtOldPassword.text.toString().trim().isEmpty()) {
            edtOldPassword.setError(getString(R.string.error_empty_old_password), errortint(this))
            isvalidate = false
        }
        if (edtNewPassword.text.toString().trim().isEmpty()) {
            edtNewPassword.setError(getString(R.string.error_empty_new_password), errortint(this))
            isvalidate = false
        }
        if (edtConfirmNewPassword.text.toString().trim().isEmpty()) {
            edtConfirmNewPassword.setError(getString(R.string.error_empty_confirm_password), errortint(this))
            isvalidate = false
        }
        if (edtNewPassword.text.length < 6 && edtNewPassword.text.isNotEmpty()) {
            toast(getString(R.string.error_valid_password), AppConstant.TOAST_SHORT)
            return false
        }
        if (!edtNewPassword.text.toString().equals(edtConfirmNewPassword.text.toString())) {
            toast(getString(R.string.error_mismatch_password), AppConstant.TOAST_SHORT)
            return false
        }
        if (edtOldPassword.text.toString().equals(edtNewPassword.text.toString())) {
            toast(getString(R.string.error_old_and_new_password_password), AppConstant.TOAST_SHORT)
            return false
        }

        return isvalidate
    }

    private fun ManagePasswordAPI() {

        showProgress()

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(this)
        }

        val jsonObject = JSONObject()
        jsonObject.put("ID", sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_ID)!!)
        jsonObject.put("OldPassword", edtOldPassword.text.toString().trim())
        jsonObject.put("Password", edtNewPassword.text.toString().trim())

        val call = ApiUtils.apiInterface.ManageUserChangePassword(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>, response: Response<CommonResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                        llSetNewPassword.gone()
                        llNewPasswordReset.visible()
                        imgBack.gone()
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                hideProgress()
            }
        })
    }

}