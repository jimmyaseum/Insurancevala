package com.app.insurancevala.activity.Login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.insurancevala.R
import com.app.insurancevala.utils.PrefConstants
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.hideKeyboard
import com.app.insurancevala.utils.preventTwoClick
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.activity_welcome.layout

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary))

        overridePendingTransition(R.anim.fadein, R.anim.fadeout)

        txtLogin.setOnClickListener(this)
        txtSignUp.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when(v?.id) {
            R.id.txtLogin -> {
                preventTwoClick(v)

                val sharedPreference = SharedPreference(this@WelcomeActivity)
                sharedPreference.setPreference(PrefConstants.PREF_IS_WELCOME, "1")

                val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.txtSignUp -> {
                preventTwoClick(v)

                val sharedPreference = SharedPreference(this@WelcomeActivity)
                sharedPreference.setPreference(PrefConstants.PREF_IS_WELCOME, "1")

                val intent = Intent(this@WelcomeActivity, RegistrationActivity::class.java)
                startActivity(intent)
            }
        }
    }
}