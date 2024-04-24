package com.app.insurancevala.master

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.hideKeyboard
import com.app.insurancevala.utils.preventTwoClick
import kotlinx.android.synthetic.main.activity_masters_list.*

class MastersListActivity : BaseActivity(), View.OnClickListener {

    var sharedPreference: SharedPreference? = null
    var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masters_list)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        sharedPreference = SharedPreference(applicationContext)
        initializeView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun initializeView() {
        imgBack.setOnClickListener(this)
        LLInquiryType.setOnClickListener(this)
        LLInquirySubType.setOnClickListener(this)
        LLLeadType.setOnClickListener(this)
        LLLeadStatus.setOnClickListener(this)
        LLUserType.setOnClickListener(this)
        LLInitialType.setOnClickListener(this)
        LLLeadSource.setOnClickListener(this)
        LLCategoryType.setOnClickListener(this)
        LLCompany.setOnClickListener(this)
        LLOccupation.setOnClickListener(this)
        LLRelation.setOnClickListener(this)
        LLCountry.setOnClickListener(this)
        LLState.setOnClickListener(this)
        LLCity.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                onBackPressed()
            }

            R.id.LLInquiryType -> {
                preventTwoClick(v)
                val intent = Intent(this, InquiryTypeListActivity::class.java)
                startActivity(intent)
            }

            R.id.LLInquirySubType -> {
                preventTwoClick(v)
                val intent = Intent(this, InquirySubTypeListActivity::class.java)
                startActivity(intent)
            }

            R.id.LLLeadType -> {
                preventTwoClick(v)
                val intent = Intent(this, LeadTypeListActivity::class.java)
                startActivity(intent)
            }

            R.id.LLLeadStatus -> {
                preventTwoClick(v)
                val intent = Intent(this, LeadStatusListActivity::class.java)
                startActivity(intent)

            }

            R.id.LLUserType -> {
                preventTwoClick(v)
                val intent = Intent(this, UserTypeListActivity::class.java)
                startActivity(intent)

            }

            R.id.LLInitialType -> {
                preventTwoClick(v)
                val intent = Intent(this, InitialTypeListActivity::class.java)
                startActivity(intent)

            }

            R.id.LLLeadSource -> {
                preventTwoClick(v)
                val intent = Intent(this, LeadSourceListActivity::class.java)
                startActivity(intent)

            }

            R.id.LLCategoryType -> {
                preventTwoClick(v)
                val intent = Intent(this, CategoryTypeListActivity::class.java)
                startActivity(intent)

            }

            R.id.LLCompany -> {
                preventTwoClick(v)
                val intent = Intent(this, CompanyListActivity::class.java)
                startActivity(intent)

            }

            R.id.LLOccupation -> {
                preventTwoClick(v)
                val intent = Intent(this, OccupationTypeListActivity::class.java)
                startActivity(intent)

            }

            R.id.LLRelation -> {
                preventTwoClick(v)
                val intent = Intent(this, RelationTypeListActivity::class.java)
                startActivity(intent)

            }

            R.id.LLCountry -> {
                preventTwoClick(v)
                val intent = Intent(this, CountryListActivity::class.java)
                startActivity(intent)

            }

            R.id.LLState -> {
                preventTwoClick(v)
                val intent = Intent(this, StateListActivity::class.java)
                startActivity(intent)

            }

            R.id.LLCity -> {
                preventTwoClick(v)
                val intent = Intent(this, CityListActivity::class.java)
                startActivity(intent)

            }
        }
    }
}
