package com.app.insurancevala.activity

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import com.app.insurancevala.R

abstract class BaseActivity : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null

    abstract fun initializeView()

    fun hideProgress() {
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