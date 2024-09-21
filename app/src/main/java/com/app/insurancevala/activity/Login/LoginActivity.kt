package com.app.insurancevala.activity.Login

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.activity.DashBoard.HomeActivity
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.LoginModel
import com.app.insurancevala.model.response.LoginResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.app.insurancevala.utils.AppConstant.DEVICE_TYPE
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//commit by shubham
class LoginActivity : BaseActivity(), View.OnClickListener {

    var sharedPreference: SharedPreference? = null

    var fcmDeviceToken = ""

    var boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreference = SharedPreference(applicationContext)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        initializeView()
        getDeviceToken()

    }

    override fun initializeView() {
        boolean = intent.getBooleanExtra("Login", false)
        if (boolean == true) {
            CallLogoutAPI()
        }
        SetAnimationControl()
        SetInitListner()
    }

    private fun SetAnimationControl() {

        val topanim = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        val leftanim = AnimationUtils.loadAnimation(this, R.anim.left_anim)
        val rightanim = AnimationUtils.loadAnimation(this, R.anim.right_anim)
        val bottomanim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim)

        txtHeader.animation = topanim
        LLUserName.animation = leftanim
        LLPassword.animation = leftanim
        cbshowpassword.animation = leftanim
        txtForgotPassword.animation = rightanim
        LLLoginButton.animation = bottomanim
        LLDontHaveAcc.animation = bottomanim
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        txtSignUp.setOnClickListener(this)
        txtForgotPassword.setOnClickListener(this)
        cbshowpassword.setOnClickListener(this)

        txtButtonLogin.setOnClickListener {
            preventTwoClick(it)

            val flag = isValidate()
            if(flag) {
                if (isOnline(this)) {
                    callLoginAPI()
                } else {
                    internetErrordialog(this@LoginActivity)
                }
            }
        }

        edtPassword.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                txtButtonLogin.performClick()
            } else {
                false
            }
        }
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }
            R.id.txtSignUp -> {
                preventTwoClick(v)
                val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.txtForgotPassword -> {
                preventTwoClick(v)
                val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.cbshowpassword -> {
                if (cbshowpassword.isChecked) {
                    edtPassword.setTransformationMethod(null)
                } else {
                    edtPassword.setTransformationMethod(PasswordTransformationMethod())
                }
            }
        }
    }

    private fun CallLogoutAPI() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("UserName", sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_EMAIL)!!)

        val call = ApiUtils.apiInterface.ManageUserLogout(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        sharedPreference!!.clearSharedPreference()
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

    private fun getDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmDeviceToken = task.result
                LogUtil.d(TAG, "FCM DeviceToken: $fcmDeviceToken")
            } else {
                // Handle the case where fetching token failed
                val exception = task.exception
                if (exception != null) {
                    LogUtil.e(TAG, "Fetching FCM token failed: ${exception.message}")
                } else {
                    LogUtil.e(TAG, "Fetching FCM token failed with unknown exception")
                }
            }
        })
    }

    private fun isValidate(): Boolean {

        var isvalidate = true

        if (edtUserName.text.toString().trim().isEmpty()) {
            edtUserName.setError("Enter Username", errortint(this))
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

        return isvalidate
    }

    private fun callLoginAPI() {

        showProgress()

        val jsonObject = JSONObject()
        jsonObject.put("UserName", edtUserName.text.toString().trim())
        jsonObject.put("Password", edtPassword.text.toString().trim())
        jsonObject.put("DeviceType", DEVICE_TYPE)
        jsonObject.put("DeviceToken", fcmDeviceToken)

        val call = ApiUtils.apiInterface.login(getRequestJSONBody(jsonObject.toString()))

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                hideProgress()

                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        val userModel: LoginModel? = response.body()!!.Data!!
                        sharedPreference!!.setPreference(PrefConstants.PREF_IS_LOGIN, "1")
                        sharedPreference!!.setPreference(PrefConstants.PREF_TOKEN, userModel!!.Token.toString())
                        sharedPreference!!.setPreference(PrefConstants.PREF_USER_ID, userModel.ID.toString())
                        sharedPreference!!.setPreference(PrefConstants.PREF_USER_USER_NAME, userModel.UserName.toString())
                        sharedPreference!!.setPreference(PrefConstants.PREF_USER_EMAIL, userModel.EmailID.toString())
                        sharedPreference!!.setPreference(PrefConstants.PREF_USER_MOBILE, userModel.MobileNo.toString())
                        sharedPreference!!.setPreference(PrefConstants.PREF_USER_IMAGE, userModel.UserImage.toString())
                        sharedPreference!!.setPreference(PrefConstants.PREF_USER_GUID, userModel.UserGUID.toString())
                        sharedPreference!!.setPreference(PrefConstants.PREF_USER_TYPE_ID, userModel.UserTypeID.toString())
                        sharedPreference!!.setPreference(PrefConstants.PREF_USER_TYPE, userModel.UserType.toString())

                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()


                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }
}
