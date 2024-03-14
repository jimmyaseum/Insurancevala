package com.app.insurancevala.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.insurancevala.R
import com.app.insurancevala.activity.DashBoard.HomeInnerListActivity
import com.app.insurancevala.activity.NBInquiry.InquiryListActivity
import com.app.insurancevala.adapter.dashboard.EmployeeWiseCountsAdapter
import com.app.insurancevala.adapter.dashboard.InquiryTypeWiseCountsAdapter
import com.app.insurancevala.adapter.dashboard.LeadStatusWiseCountsAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.DashboardModel
import com.app.insurancevala.model.response.DashboardResponse
import com.app.insurancevala.model.response.EmployeeWiseCountsModel
import com.app.insurancevala.model.response.InquiryTypeWiseCountsModel
import com.app.insurancevala.model.response.LeadStatusWiseCountModel
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.PrefConstants
import com.app.insurancevala.utils.SharedPreference
import com.app.insurancevala.utils.convertDateStringToString
import com.app.insurancevala.utils.getRequestJSONBody
import com.app.insurancevala.utils.hideKeyboard
import com.app.insurancevala.utils.preventTwoClick
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : BaseFragment(), View.OnClickListener, RecyclerClickListener {

    private var views: View? = null
    var sharedPreference: SharedPreference? = null

    // FromDate
    val CalenderFromDate = Calendar.getInstance()
    var FromDate: String = ""

    // ToDate
    val CalenderToDate = Calendar.getInstance()
    var ToDate: String = ""

    var arrayListEmployeeWiseCounts : ArrayList<EmployeeWiseCountsModel> = ArrayList()
    var arrayListInquiryTypeWiseCounts : ArrayList<InquiryTypeWiseCountsModel> = ArrayList()
    var arrayListLeadStatusWiseCount : ArrayList<LeadStatusWiseCountModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        views = inflater.inflate(R.layout.fragment_home, container, false)
        initializeView()
        return views
    }

    override fun initializeView() {

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(requireActivity())
        }
        val Username = sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_USER_NAME)!!
        val Mobile = sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_MOBILE)!!
        val Email = sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_EMAIL)!!
        val UserImage = sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_IMAGE)!!

        views!!.txtUserName.text = Username
        views!!.txtMobile.text = Mobile
        views!!.txtEmail.text = Email

        Glide.with(this)
            .load(UserImage)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .into(views!!.imgprofile)

        CallDashboardAPI()
        SetInitListner()
    }

    private fun SetInitListner() {
        views!!.edtFromDate.setOnClickListener(this)
        views!!.edtToDate.setOnClickListener(this)
        views!!.txtClear.setOnClickListener(this)

        views!!.llTotalLead.setOnClickListener(this)
        views!!.llMyLead.setOnClickListener(this)
        views!!.llOpenLead.setOnClickListener(this)

        views!!.refreshLayout.setOnRefreshListener {
            CallDashboardAPI()
            views!!.refreshLayout.isRefreshing = false
        }
    }

    override fun onClick(v: View?) {
        hideKeyboard(requireContext(), v)
        when (v?.id) {
            R.id.edtFromDate -> {
                preventTwoClick(v)
                val dpd = DatePickerDialog(
                    requireActivity(),
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        CalenderFromDate.set(Calendar.YEAR, year)
                        CalenderFromDate.set(Calendar.MONTH, monthOfYear)
                        CalenderFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        FromDate = SimpleDateFormat(AppConstant.yyyy_MM_dd_Dash, Locale.US).format(
                            CalenderFromDate.time
                        )

                        val selecteddate =
                            SimpleDateFormat(AppConstant.dd_MM_yyyy_HH_mm_ss, Locale.US).format(
                                CalenderFromDate.time
                            )
                        val mDate = convertDateStringToString(
                            selecteddate, AppConstant.dd_MM_yyyy_HH_mm_ss,
                            AppConstant.dd_LLL_yyyy
                        )
                        views!!.edtFromDate.setText(mDate)
                        CallDashboardAPI()
                    },
                    CalenderFromDate.get(Calendar.YEAR),
                    CalenderFromDate.get(Calendar.MONTH),
                    CalenderFromDate.get(Calendar.DAY_OF_MONTH)
                )
                dpd.show()
            }
            R.id.edtToDate -> {
                preventTwoClick(v)
                val dpd = DatePickerDialog(
                    requireActivity(),
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        CalenderToDate.set(Calendar.YEAR, year)
                        CalenderToDate.set(Calendar.MONTH, monthOfYear)
                        CalenderToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        ToDate = SimpleDateFormat(AppConstant.yyyy_MM_dd_Dash, Locale.US).format(
                            CalenderToDate.time
                        )

                        val selecteddate =
                            SimpleDateFormat(AppConstant.dd_MM_yyyy_HH_mm_ss, Locale.US).format(
                                CalenderToDate.time
                            )
                        val mDate = convertDateStringToString(
                            selecteddate, AppConstant.dd_MM_yyyy_HH_mm_ss,
                            AppConstant.dd_LLL_yyyy
                        )
                        views!!.edtToDate.setText(mDate)
                        CallDashboardAPI()
                    },
                    CalenderToDate.get(Calendar.YEAR),
                    CalenderToDate.get(Calendar.MONTH),
                    CalenderToDate.get(Calendar.DAY_OF_MONTH)
                )
                dpd.show()
            }
            R.id.txtClear -> {
                preventTwoClick(v)
                views!!.edtFromDate.setText("")
                views!!.edtToDate.setText("")
                FromDate = ""
                ToDate = ""
                CallDashboardAPI()
            }

            R.id.llTotalLead -> {
                preventTwoClick(v)
                val intent = Intent(requireActivity(), HomeInnerListActivity::class.java)
                intent.putExtra("Header", "Total Lead List")
                intent.putExtra("OperationType", AppConstant.TotalLead)
                startActivity(intent)
            }

            R.id.llOpenLead -> {
                preventTwoClick(v)
                val intent = Intent(requireActivity(), HomeInnerListActivity::class.java)
                intent.putExtra("Header", "Total Open Lead List")
                intent.putExtra("LeadStatusID", 2)
                intent.putExtra("OperationType", AppConstant.LeadStatus)
                intent.putExtra("FromDate", FromDate)
                intent.putExtra("ToDate", ToDate)
                startActivity(intent)
            }
            R.id.llMyLead -> {
                preventTwoClick(v)
                val intent = Intent(requireActivity(), HomeInnerListActivity::class.java)
                intent.putExtra("Header", "Own Lead List")
                intent.putExtra("OperationType", AppConstant.OwnLead)
                startActivity(intent)
            }
        }
    }

    private fun CallDashboardAPI() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("FromDate", FromDate)
        jsonObject.put("ToDate", ToDate)

        val call = ApiUtils.apiInterface.ManageDashboard(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListDashboard = response.body()?.Data!!
                        setAPIData(arrayListDashboard)
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun setAPIData(model: DashboardModel) {

        if(model.TotalInquiry != null) {
            views!!.txtTotalLead.setText(model.TotalInquiry.toString())
        }
        if(model.OpenNBInquiry != null) {
            views!!.txtOpenLead.setText(model.OpenNBInquiry.toString())
        }
        if(model.OwnInquiry != null) {
            views!!.txtMyLead.setText(model.OwnInquiry.toString())
        }

        if(!model.LeadStatusWiseCount.isNullOrEmpty() && model.LeadStatusWiseCount!!.size > 0) {
            arrayListLeadStatusWiseCount = model.LeadStatusWiseCount!!
            val adapter = LeadStatusWiseCountsAdapter(requireActivity(), arrayListLeadStatusWiseCount, this@HomeFragment)
            views!!.RvDashLeadStatusList.adapter = adapter
        }
        if(!model.InquiryTypeWiseCounts.isNullOrEmpty() && model.InquiryTypeWiseCounts!!.size > 0) {
            arrayListInquiryTypeWiseCounts = model.InquiryTypeWiseCounts!!
            val adapter = InquiryTypeWiseCountsAdapter(requireActivity(), arrayListInquiryTypeWiseCounts, this@HomeFragment)
            views!!.RvDashInquiryTypeList.adapter = adapter
        }
        if(!model.EmployeeWiseCounts.isNullOrEmpty() && model.EmployeeWiseCounts!!.size > 0) {
            arrayListEmployeeWiseCounts = model.EmployeeWiseCounts!!
            val adapter = EmployeeWiseCountsAdapter(requireActivity(), arrayListEmployeeWiseCounts, this@HomeFragment)
            views!!.RvDashEmpList.adapter = adapter
        }
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when(type) {
            101 -> {
                // Lead Status item Click
                preventTwoClick(view)
                val intent = Intent(requireActivity(), HomeInnerListActivity::class.java)
                intent.putExtra("Header", "Lead Status Wise List")
                intent.putExtra("LeadStatusID", arrayListLeadStatusWiseCount[position].LeadStatusID)
                intent.putExtra("OperationType", AppConstant.LeadStatus)
                intent.putExtra("FromDate", FromDate)
                intent.putExtra("ToDate", ToDate)
                startActivity(intent)

            }
            102 -> {
                // Inquiry Type item Click
                preventTwoClick(view)
                val intent = Intent(requireActivity(), HomeInnerListActivity::class.java)
                intent.putExtra("Header", "Inquiry Type Wise List")
                intent.putExtra("InquiryTypeID", arrayListInquiryTypeWiseCounts[position].InquiryTypeID)
                intent.putExtra("OperationType", AppConstant.InquiryType)
                startActivity(intent)

            }
            103 -> {
                preventTwoClick(view)
                val intent = Intent(requireActivity(), HomeInnerListActivity::class.java)
                intent.putExtra("Header", "Employee Wise List")
                intent.putExtra("UserID", arrayListEmployeeWiseCounts[position].UserID)
                intent.putExtra("OperationType", AppConstant.Employee)
                startActivity(intent)

            }
        }
    }

}
