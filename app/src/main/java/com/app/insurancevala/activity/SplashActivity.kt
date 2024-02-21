package com.app.insurancevala.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.app.insurancevala.R
import com.app.insurancevala.activity.DashBoard.HomeActivity
import com.app.insurancevala.activity.Login.LoginActivity
import com.app.insurancevala.activity.Login.WelcomeActivity
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.PrefConstants.PREF_IS_LOGIN
import com.app.insurancevala.utils.PrefConstants.PREF_IS_WELCOME
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.getRequestJSONBody
import com.app.insurancevala.utils.internetErrordialog
import com.app.insurancevala.utils.isOnline
import kotlinx.android.synthetic.main.activity_splash.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import com.app.insurancevala.model.api.AppVersion
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_splash.layout

class SplashActivity : AppCompatActivity() {

    var currentVersion = ""
    var LatestVersion = ""
    private var progressDialog: ProgressDialog? = null
    var sharedPreference: SharedPreference? = null
    var dialog: Dialog? = null

        // commit by jimmy jatin patel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        sharedPreference = SharedPreference(applicationContext)

        val topanim = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        val leftanim = AnimationUtils.loadAnimation(this, R.anim.left_anim)
        val rightanim = AnimationUtils.loadAnimation(this, R.anim.right_anim)

        imgSplash.animation = topanim
        imgSubBrand1.animation = leftanim
        imgSubBrand2.animation = rightanim

        getCurrentVersion()

        if (isOnline(this)) {
            getLatestVersion()
        } else {
            internetErrordialog(this@SplashActivity)
        }
    }

    private fun getLatestVersion() {

        showProgress()

        val jsonObject = JSONObject()
        jsonObject.put("SettingType", "Faculty")

        val call = ApiUtils.apiInterface.getAppVersion(getRequestJSONBody(jsonObject.toString()))

        call.enqueue(object : Callback<AppVersion> {
            override fun onResponse(
                call: Call<AppVersion>,
                response: Response<AppVersion>
            ) {

                if (response.code() == 200) {

                    if (response.body()?.Status == 200) {
                        hideProgress()
                        val version = response.body()?.Data!!
                        LatestVersion = version.Version

                        if(currentVersion == LatestVersion) {

                            if (isOnline(this@SplashActivity)) {
                                Handler().postDelayed({
                                    if (!isFinishing) {
                                        val sharedPreference = SharedPreference(applicationContext)
                                        if (sharedPreference.getPreferenceString(PREF_IS_WELCOME).equals("1")) {
                                            if (sharedPreference.getPreferenceString(PREF_IS_LOGIN).equals("1")) {
                                                val intent = Intent(applicationContext, HomeActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                val intent = Intent(applicationContext, LoginActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            }
                                        } else {
                                            val intent = Intent(applicationContext, WelcomeActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                                }, 3000)
                            } else {
                                internetErrordialog(this@SplashActivity)
                            }
                        } else {
                            sharedPreference?.clearSharedPreference()
                            showUpdateDialog()
                        }

                    }
                    else {
                        hideProgress()
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(call: Call<AppVersion>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun getCurrentVersion() {
        val pm = this.packageManager
        var pInfo: PackageInfo? = null
        try {
            pInfo = pm.getPackageInfo(this.packageName, 0)
        } catch (e1: PackageManager.NameNotFoundException) {
            // TODO Auto-generated catch block
            e1.printStackTrace()
        }
        currentVersion = pInfo!!.versionName

    }

    private fun showUpdateDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Please Update")
        builder.setMessage("We have launched a new and improved version. Please update the app to continue using the app.")
        builder.setPositiveButton("Update Now",
            DialogInterface.OnClickListener { dialog, which ->
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + packageName)
                    )
                )
                dialog.dismiss()
            })
        builder.setCancelable(false)
        dialog = builder.show()
    }

    fun hideProgress() {
        if (progressDialog != null)
            progressDialog?.let { if (it.isShowing) it.cancel() }
    }

    fun showProgress() {
        hideProgress()
        progressDialog = showLoadingDialog(this)
    }
    fun showLoadingDialog(context: Context?): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.let {
            it.show()
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setContentView(R.layout.dialog_layout)
            it.isIndeterminate = true
            it.setCancelable(false)
            it.setCanceledOnTouchOutside(false)
            return it
        }
    }

}
