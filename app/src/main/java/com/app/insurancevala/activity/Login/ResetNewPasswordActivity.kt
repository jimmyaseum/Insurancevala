package com.app.insurancevala.activity.Login

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.app.insurancevala.R
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.hideKeyboard
import com.app.insurancevala.utils.preventTwoClick
import com.app.insurancevala.utils.visible

import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.activity_reset_password.layout
import kotlinx.android.synthetic.main.activity_reset_password.cbshowpassword
import kotlinx.android.synthetic.main.activity_reset_password.imgBack
import kotlinx.android.synthetic.main.activity_reset_password.txtHeader

class ResetNewPasswordActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
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
                llSetNewPassword.gone()
                llNewPasswordReset.visible()
                imgBack.gone()
            }
            R.id.txtButtonSetOkay -> {
                preventTwoClick(v)
                val intent = Intent(this@ResetNewPasswordActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}