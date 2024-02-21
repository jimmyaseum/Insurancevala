package com.app.insurancevala.activity

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
import com.app.insurancevala.utils.PrefConstants.PREF_IS_LOGIN
import com.app.insurancevala.utils.PrefConstants.PREF_IS_WELCOME
import com.app.insurancevala.utils.SharedPreference
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.activity_splash.layout

class SplashActivity : AppCompatActivity() {

    // COMMIT BU SHUBHAM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)


        val topanim = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        val leftanim = AnimationUtils.loadAnimation(this, R.anim.left_anim)
        val rightanim = AnimationUtils.loadAnimation(this, R.anim.right_anim)

        imgSplash.animation = topanim
        imgSubBrand1.animation = leftanim
        imgSubBrand2.animation = rightanim

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
    }

}
