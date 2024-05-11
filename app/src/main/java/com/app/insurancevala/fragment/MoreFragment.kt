package com.app.insurancevala.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.app.insurancevala.R
import com.app.insurancevala.activity.ActivityLog.ActivityLog
import com.app.insurancevala.activity.BrochureListActivity
import com.app.insurancevala.activity.Login.LoginActivity
import com.app.insurancevala.activity.Login.ChangePasswordActivity
import com.app.insurancevala.activity.Users.AddUsersActivity
import com.app.insurancevala.activity.Users.UsersListActivity
import com.app.insurancevala.master.MastersListActivity
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.PrefConstants
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.getRequestJSONBody
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.preventTwoClick
import com.app.insurancevala.utils.visible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_more_new.view.*
import kotlinx.android.synthetic.main.fragment_more_new.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoreFragment : BaseFragment() {

    private var views: View? = null
    var sharedPreference: SharedPreference? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        views = inflater.inflate(R.layout.fragment_more_new, container, false)
        initializeView()
        return views
    }

    override fun initializeView() {

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(requireActivity())
        }

        val Username = sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_USER_NAME)!!
        val Email = sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_EMAIL)!!
        val Mobile = sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_MOBILE)!!
        val UserImage = sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_IMAGE)!!
        val UserGUID = sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_GUID)!!
        val UserTypeID = sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_TYPE_ID)!!

        if (UserTypeID.equals("1")) {
            views!!.LLManageUser.visible()
            views!!.LLActivityLog.visible()

            views!!.V1.visible()
            views!!.V2.visible()
        } else {
            views!!.LLManageUser.gone()
            views!!.LLActivityLog.gone()

            views!!.V1.gone()
            views!!.V2.gone()
        }
        views!!.tvUserName.text = Username
        views!!.txtMobileNo.text = Mobile
        views!!.txtEmial.text = Email

        Glide.with(this)
            .load(UserImage)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .into(views!!.imgprofileimage)

        views!!.imgNotification.setOnClickListener {
            preventTwoClick(it)

        }

        views!!.LLManageUser.setOnClickListener {
            preventTwoClick(it)
            val intent = Intent(requireActivity(), UsersListActivity::class.java)
            startActivity(intent)

        }

        views!!.LLActivityLog.setOnClickListener {
            preventTwoClick(it)
            val intent = Intent(requireActivity(), ActivityLog::class.java)
            startActivity(intent)

        }

        views!!.LLMaster.setOnClickListener {
            preventTwoClick(it)
            val intent = Intent(requireActivity(), MastersListActivity::class.java)
            startActivity(intent)

        }

        views!!.LLChangePassword.setOnClickListener {
            preventTwoClick(it)
            val intent = Intent(requireActivity(), ChangePasswordActivity::class.java)
            startActivity(intent)

        }

        views!!.LLBrochure.setOnClickListener {
            preventTwoClick(it)
            val intent = Intent(requireActivity(), BrochureListActivity::class.java)
            startActivity(intent)

        }

        views!!.txtLogout.setOnClickListener {
            preventTwoClick(it)
            val sharedPreference = SharedPreference(requireContext())
            if (sharedPreference.getPreferenceString(PrefConstants.PREF_IS_LOGIN).equals("1")) {
                CallLogoutAPI()
                sharedPreference.clearSharedPreference()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finishAffinity()
            }
        }

        views!!.llEditProfile.setOnClickListener {
            preventTwoClick(it)
            val intent = Intent(requireActivity(), AddUsersActivity::class.java)
            intent.putExtra("IsFrom", "MoreFragment")
            intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
            intent.putExtra("UserGUID", UserGUID)
            startActivityForResult(intent, AppConstant.INTENT_1003)
        }
    }

    private fun CallLogoutAPI() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("UserName", sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_EMAIL)!!)

        val call = ApiUtils.apiInterface.ManageUserLogout(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.INTENT_1003 && resultCode == AppCompatActivity.RESULT_OK) {

            val sharedPreference = SharedPreference(requireContext())
            if (sharedPreference.getPreferenceString(PrefConstants.PREF_IS_LOGIN).equals("1")) {
                CallLogoutAPI()
                sharedPreference.clearSharedPreference()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finishAffinity()
            }
        }
    }
}
