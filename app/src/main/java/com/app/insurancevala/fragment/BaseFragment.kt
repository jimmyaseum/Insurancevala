package com.app.insurancevala.fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.Fragment
import com.app.insurancevala.R

abstract class BaseFragment: Fragment() {
    var progressDialog: Dialog? = null

    abstract fun initializeView()

    fun Context.showLoadingDialog() {
        progressDialog = Dialog(this)
        progressDialog?.setContentView(R.layout.dialog_layout)
        progressDialog?.setCancelable(false)
        progressDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()
    }

    fun Fragment.showProgress() {
        hideProgress()
        this.activity?.showLoadingDialog()
    }

    fun hideProgress() {
        if (progressDialog != null)
            if(progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }

    }
}
