package com.app.insurancevala.activity.DashBoard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.app.insurancevala.R
import com.app.insurancevala.fragment.*
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.PrefConstants
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.hideKeyboard
import com.app.insurancevala.utils.preventTwoClick
import com.example.awesomedialog.*
import io.ak1.OnBubbleClickListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.layout

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var fragmentManager : FragmentManager
    var sharedPreference: SharedPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(applicationContext)
        }
        AppConstant.TOKEN = sharedPreference?.getPreferenceString(PrefConstants.PREF_TOKEN).toString()

        SetDefaultFragment()
        SetInitListner()
    }

    private fun SetDefaultFragment() {
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
    }

    private fun SetInitListner() {

        bubbleTabBar.addBubbleListener(object : OnBubbleClickListener {
            override fun onBubbleClick(id: Int) {
                var fragment : Fragment? = null
                when(id) {
                    R.id.bottom_home -> {
                        preventTwoClick(bubbleTabBar.rootView)
                        fragment = HomeFragment()
                    }
                    R.id.bottom_lead -> {
                        preventTwoClick(bubbleTabBar.rootView)
                        fragment = LeadFragment()
                    }
                    R.id.bottom_nb -> {
                        preventTwoClick(bubbleTabBar.rootView)
                        fragment = NBFragment()
                    }
//                    R.id.bottom_quote -> {
////                        fragment = QuoteFragment()
////                    }
                    R.id.bottom_more -> {
                        preventTwoClick(bubbleTabBar.rootView)
                        fragment = MoreFragment()
                    }
                }

                if(fragment != null) {
                    fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.fragment_container , fragment).commit()
                }
            }
        })
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {

        }
    }

    override fun onBackPressed() {
        AwesomeDialog.build(this)
            .title("Exit!")
            .body("Are you sure want to exit from app?")
            .icon(R.drawable.ic_logout_black_24dp)
            .position(AwesomeDialog.POSITIONS.CENTER)
            .onNegative("No") {

            }
            .onPositive("Yes") {
                finishAffinity()
            }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                AppConstant.INTENT_1001 -> {
                    Log.e("AAA","LeadFragment")
                    // LeadFragment
                    for (fragment in supportFragmentManager.fragments) {
                        fragment.onActivityResult(requestCode, resultCode, data)
                    }
                }
                AppConstant.INTENT_1002 -> {
                    Log.e("AAA","NBInquiry")
                // NBInquiry Fragment
                    for (fragment in supportFragmentManager.fragments) {
                        fragment.onActivityResult(requestCode, resultCode, data)
                    }
                }
                AppConstant.INTENT_1003 -> {
                    Log.e("AAA","MoreFragment")
                // More Fragment
                    for (fragment in supportFragmentManager.fragments) {
                        fragment.onActivityResult(requestCode, resultCode, data)
                    }
                }
            }
        }
    }

}