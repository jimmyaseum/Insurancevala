package com.app.insurancevala.activity.DashBoard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import com.example.awesomedialog.*
import io.ak1.OnBubbleClickListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.layout

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var fragmentManager: FragmentManager
    private var sharedPreference: SharedPreference? = null
    private var isNavigationEnabled = true
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(applicationContext)
        }
        AppConstant.TOKEN =
            sharedPreference?.getPreferenceString(PrefConstants.PREF_TOKEN).toString()

        SetDefaultFragment()
        SetInitListener()
    }

    private fun SetDefaultFragment() {
        fragmentManager = supportFragmentManager
        if (fragmentManager.findFragmentById(R.id.fragment_container) == null) {
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }

    private fun SetInitListener() {
        bubbleTabBar.addBubbleListener(object : OnBubbleClickListener {
            override fun onBubbleClick(id: Int) {
                if (!isNavigationEnabled) {
                    return  // Ignore clicks when navigation is disabled
                }

                var fragment: Fragment? = null
                var tag: String? = null // Tag to identify the fragment

                // Determine which fragment to show based on the clicked tab
                when (id) {
                    R.id.bottom_home -> {
                        fragment = HomeFragment()
                        tag = HomeFragment::class.java.simpleName
                    }

                    R.id.bottom_lead -> {
                        fragment = LeadFragment()
                        tag = LeadFragment::class.java.simpleName
                    }

                    R.id.bottom_nb -> {
                        fragment = NBFragment()
                        tag = NBFragment::class.java.simpleName
                    }
//            R.id.bottom_quote -> {
////                fragment = QuoteFragment()
////                tag = QuoteFragment::class.java.simpleName
//            }
                    R.id.bottom_more -> {
                        fragment = MoreFragment()
                        tag = MoreFragment::class.java.simpleName
                    }
                }

                if (fragment != null && tag != null) {
                    val currentFragment = fragmentManager.findFragmentByTag(tag)

                    // Check if the clicked fragment is already visible, if not then switch
                    if (currentFragment == null || !currentFragment.isVisible) {
                        // Disable navigation temporarily
                        isNavigationEnabled = false

                        val transaction = fragmentManager.beginTransaction()
                        transaction.replace(R.id.fragment_container, fragment, tag)
                        transaction.commit()

                        // Enable navigation after a delay
                        handler.postDelayed({
                            isNavigationEnabled = true
                        }, 500)
                    }
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
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
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
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                AppConstant.INTENT_1001 -> {
                    Log.e("AAA", "LeadFragment")
                    // LeadFragment
                    for (fragment in supportFragmentManager.fragments) {
                        fragment.onActivityResult(requestCode, resultCode, data)
                    }
                }

                AppConstant.INTENT_1002 -> {
                    Log.e("AAA", "NBInquiry")
                    // NBInquiry Fragment
                    for (fragment in supportFragmentManager.fragments) {
                        fragment.onActivityResult(requestCode, resultCode, data)
                    }
                }

                AppConstant.INTENT_1003 -> {
                    Log.e("AAA", "MoreFragment")
                    // More Fragment
                    for (fragment in supportFragmentManager.fragments) {
                        fragment.onActivityResult(requestCode, resultCode, data)
                    }
                }
            }
        }
    }
}
