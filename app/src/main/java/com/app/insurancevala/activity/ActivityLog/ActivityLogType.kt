package com.app.insurancevala.activity.ActivityLog

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.utils.*
import kotlinx.android.synthetic.main.activity_log_type.*

class ActivityLogType : BaseActivity(), View.OnClickListener {

    var sharedPreference: SharedPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_type)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {

    }

    override fun initializeView() {
        SetInitListner()
    }

    private fun SetInitListner() {

        imgBack.setOnClickListener(this)
        LLInquiryActivityLog.setOnClickListener(this)
        LLLeadActivityLog.setOnClickListener(this)

        imgAddInquiryType.gone()
        imgSearch.gone()
    }

    override fun onClick(v: View?) {
        hideKeyboard(this@ActivityLogType, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }
            R.id.LLInquiryActivityLog -> {
                preventTwoClick(v)
                val intent = Intent(this, ActivityLog::class.java)
                intent.putExtra("NBActivityType", "Inquiry")
                startActivity(intent)
            }
            R.id.LLLeadActivityLog -> {
                preventTwoClick(v)
                val intent = Intent(this, ActivityLog::class.java)
                intent.putExtra("NBActivityType", "Lead")
                startActivity(intent)
            }
        }
    }
}