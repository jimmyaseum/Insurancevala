package com.app.insurancevala.activity.Login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.app.insurancevala.R
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.hideKeyboard
import com.app.insurancevala.utils.preventTwoClick
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_forgot_password.imgBack

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        SetAnimationControl1()
        SetInitListner()
    }

    private fun SetAnimationControl1() {

        val topanim = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        val bottomanim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim)

        txtHeader.animation = topanim
        txtMsg.animation = topanim
        LLEmailAddress.animation = bottomanim
        txtButtonResetPassword.animation = bottomanim

    }

    private fun SetInitListner() {
    imgBack.setOnClickListener(this)
    txtButtonResetPassword.setOnClickListener(this)
    txtButtonSetNewPassword.setOnClickListener(this)
}

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }
            R.id.txtButtonResetPassword -> {
                preventTwoClick(v)
                llForgotPassword.gone()
                imgBack.gone()
                llPasswordReset.visible()
            }
            R.id.txtButtonSetNewPassword -> {
                preventTwoClick(v)
                val intent = Intent(this@ForgotPasswordActivity, ResetNewPasswordActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}