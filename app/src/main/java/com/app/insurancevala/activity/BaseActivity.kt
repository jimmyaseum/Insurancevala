package com.app.insurancevala.activity

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
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

    fun enableDisableViewGroup(v: ViewGroup, isEnable: Boolean) {

        for (i in 0 until v.childCount) {
            if (v.getChildAt(i) is TextView) {
                (v.getChildAt(i) as? TextView)?.isEnabled = isEnable
            } else if (v.getChildAt(i) is EditText) {
                (v.getChildAt(i) as? EditText)?.isEnabled = isEnable
//            } else if (v.getChildAt(i) is TextInputLayout) { //commented due to light gray place holder displayed
//                (v.getChildAt(i) as? TextInputLayout)?.isEnabled = isEnable
            } else if (v.getChildAt(i) is RadioButton) {
                (v.getChildAt(i) as? RadioButton)?.isEnabled = isEnable
            } else if (v.getChildAt(i) is CheckBox) {
                (v.getChildAt(i) as? CheckBox)?.isEnabled = isEnable
            } else if (v.getChildAt(i) is ImageView) {
                (v.getChildAt(i) as? ImageView)?.isEnabled = isEnable
            } else if (v.getChildAt(i) is RecyclerView) {
                (v.getChildAt(i) as? RecyclerView)?.isEnabled = isEnable
            } else if (v.getChildAt(i) is ViewGroup) {
                enableDisableViewGroup((v.getChildAt(i) as? ViewGroup)!!, isEnable)
            }
        }
    }
}