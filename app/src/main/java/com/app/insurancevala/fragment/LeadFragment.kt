package com.app.insurancevala.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.app.insurancevala.R
import kotlinx.android.synthetic.main.fragment_lead.view.*
import kotlinx.android.synthetic.main.fragment_lead.*
import com.app.insurancevala.activity.Lead.AddLeadActivity
import com.app.insurancevala.activity.Lead.LeadDashboardActivity
import com.app.insurancevala.adapter.LeadListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.LeadModel
import com.app.insurancevala.model.response.LeadResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeadFragment : BaseFragment(), View.OnClickListener, RecyclerClickListener {

    private var views: View? = null
    var arrayListLead: ArrayList<LeadModel>? = ArrayList()
    var arrayListLeadNew: ArrayList<LeadModel>? = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        views = inflater.inflate(R.layout.fragment_lead, container, false)
        initializeView()

        return views
    }

    override fun initializeView() {
        SetInitListner()
    }

    private fun SetInitListner() {

        views!!.imgAddLead.setOnClickListener(this)
        views!!.imgSortBy.setOnClickListener(this)

//        var manager = LinearLayoutManager(requireContext())
//        views!!.RvLeadList.layoutManager = manager

        if (isOnline(requireActivity())) {
            callManageLead()
        } else {
            internetErrordialog(requireActivity())
        }

        views!!.imgSearch.setOnClickListener {
            if (searchView.isSearchOpen) {
                searchView.closeSearch()
            } else {
                searchView.showSearch()
            }
        }

        views!!.searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val arrItemsFinal1: ArrayList<LeadModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListLead!!) {
                        try {
                            if (model.FirstName!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.LastName!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.EmailID!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.CompanyName!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.MobileNo!!.toLowerCase().contains(strSearch.toLowerCase())
                            ) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception) {
                            LogUtil.d(TAG,""+e)
                        }
                    }
                    arrayListLeadNew = arrItemsFinal1
                    val itemAdapter =
                        LeadListAdapter(activity, arrayListLeadNew!!, this@LeadFragment)
                    views!!.RvLeadList.adapter = itemAdapter
                } else {
                    arrayListLeadNew = arrayListLead
                    val itemAdapter =
                        LeadListAdapter(activity, arrayListLeadNew!!, this@LeadFragment)
                    views!!.RvLeadList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        views!!.searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListLeadNew = arrayListLead
                val itemAdapter = LeadListAdapter(activity, arrayListLeadNew!!, this@LeadFragment)
                views!!.RvLeadList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(activity, R.anim.searchview_close_anim)
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(activity, R.anim.searchview_open_anim)
            }

        })

        views!!.refreshLayout.setOnRefreshListener {
            hideKeyboard(requireContext(),refreshLayout)
            searchView.closeSearch()
            callManageLead()
            views!!.refreshLayout.isRefreshing = false
        }
    }

    override fun onClick(v: View?) {
        hideKeyboard(requireContext(), v)
        when (v?.id) {
            R.id.imgAddLead -> {
                preventTwoClick(v)
                val intent = Intent(getActivity(), AddLeadActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_ADD)
                requireActivity().startActivityForResult(intent, AppConstant.INTENT_1001)
            }

            R.id.imgSortBy -> {
                preventTwoClick(v)
                if (arrayListLeadNew!!.size > 0) {
                    arrayListLeadNew!!.reverse()
                    views!!.RvLeadList.adapter?.notifyDataSetChanged()
                }

            }
        }
    }

    private fun callManageLead() {

        showProgress()

        val call = ApiUtils.apiInterface.getLeadsFindAllActive()
        call.enqueue(object : Callback<LeadResponse> {
            override fun onResponse(call: Call<LeadResponse>, response: Response<LeadResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListLead?.clear()
                        arrayListLeadNew?.clear()
                        arrayListLead = response.body()?.Data!!
                        arrayListLeadNew = arrayListLead

                        if (arrayListLeadNew!!.size > 0) {
                            val myAdapter = LeadListAdapter(activity, arrayListLeadNew!!, this@LeadFragment)
                            views!!.RvLeadList.adapter = myAdapter
                            views!!.shimmer.stopShimmer()
                            views!!.shimmer.gone()

                        } else {
                            Snackbar.make(
                                layout,
                                response.body()?.Details.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                            views!!.shimmer.stopShimmer()
                            views!!.shimmer.gone()
                            views!!.FL.gone()
                            views!!.RLNoData.visible()
                        }
                    } else {
                        views!!.shimmer.stopShimmer()
                        views!!.shimmer.gone()
                        views!!.FL.gone()
                        views!!.RLNoData.visible()
                    }
                }
            }

            override fun onFailure(call: Call<LeadResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when (type) {
            101 -> {
                preventTwoClick(view)
                val intent = Intent(getActivity(), LeadDashboardActivity::class.java)
                intent.putExtra("LeadID", arrayListLeadNew!![position].ID)
                intent.putExtra("GUID", arrayListLeadNew!![position].LeadGUID)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }

            102 -> {
                preventTwoClick(view)
                val intent = Intent(getActivity(), AddLeadActivity::class.java)
                intent.putExtra(AppConstant.STATE, AppConstant.S_EDIT)
                intent.putExtra("LeadID", arrayListLeadNew!![position].ID)
                intent.putExtra("GUID", arrayListLeadNew!![position].LeadGUID)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }

            103 -> {
                preventTwoClick(view)
                if (!arrayListLeadNew!![position].LeadImage.isNullOrEmpty()) {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(arrayListLeadNew!![position].LeadImage)
                    )
                    startActivity(browserIntent)
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.INTENT_1001) {
            if (isOnline(requireActivity())) {
                callManageLead()
            } else {
                internetErrordialog(requireActivity())
            }
        }
    }

}