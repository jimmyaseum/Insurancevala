package com.app.insurancevala.activity.DashBoard

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.app.insurancevala.R
import com.app.insurancevala.fragment.HomeFragment
import com.app.insurancevala.fragment.LeadFragment
import com.app.insurancevala.fragment.MoreFragment
import com.app.insurancevala.fragment.NBFragment
import com.app.insurancevala.utils.*
import com.example.awesomedialog.*
import io.ak1.BubbleTabBar
import io.ak1.OnBubbleClickListener
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        var isNavigationEnabled = true
        lateinit var homeMenu: View
        lateinit var leadMenu: View
        lateinit var nbMenu: View
        lateinit var moreMenu: View
    }

    private lateinit var fragmentManager: FragmentManager
    private var sharedPreference: SharedPreference? = null

    private val handler = Handler()

    private val PERMISSION_REQUEST_CODE = 1001
    private var permissionDeniedDialog: AlertDialog? = null // Dialog to show permission denied message

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)

        // Initialize shared preferences
        if (sharedPreference == null) {
            sharedPreference = SharedPreference(applicationContext)
        }
        AppConstant.TOKEN = sharedPreference?.getPreferenceString(PrefConstants.PREF_TOKEN).toString()

        SetInitListner()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RequestNotificationPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            permissionDeniedDialog?.dismiss()
        } else {
            permissionDeniedDialog?.show()
        }
    }

    private fun SetDefaultFragment() {
        fragmentManager = supportFragmentManager
        if (fragmentManager.findFragmentById(R.id.fragment_container) == null) {
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }

    private fun SetInitListner() {
        homeMenu = bubbleTabBar.findViewById(R.id.bottom_home)
        leadMenu = bubbleTabBar.findViewById(R.id.bottom_lead)
        nbMenu = bubbleTabBar.findViewById(R.id.bottom_nb)
        moreMenu = bubbleTabBar.findViewById(R.id.bottom_more)
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

        // Set default fragment
        SetDefaultFragment()
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun RequestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "POST_NOTIFICATIONS permission granted")
                permissionDeniedDialog?.dismiss()
            } else {
                Log.d("Permission", "POST_NOTIFICATIONS permission denied")
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        permissionDeniedDialog = AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Please enable notification permissions in Settings to receive notifications.")
            .setPositiveButton("Settings") { _, _ ->
                openAppSettings()
            }
            .setCancelable(false)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, PERMISSION_REQUEST_CODE)
    }
}
