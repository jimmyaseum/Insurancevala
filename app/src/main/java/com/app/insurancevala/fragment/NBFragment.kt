package com.app.insurancevala.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.app.insurancevala.R
import kotlinx.android.synthetic.main.fragment_nb.view.*
import kotlinx.android.synthetic.main.fragment_nb.*
import kotlinx.android.synthetic.main.fragment_nb.layout
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.activity.NBInquiry.AddNBActivity
import com.app.insurancevala.adapter.NBListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.NBModel
import com.app.insurancevala.model.response.NBResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NBFragment : BaseFragment(),  View.OnClickListener, RecyclerClickListener {

    private var views: View? = null
    var arrayListNB : ArrayList<NBModel>? = ArrayList()
    var arrayListNBNew : ArrayList<NBModel>? = ArrayList()

    lateinit var adapter : NBListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        views = inflater.inflate(R.layout.fragment_nb, container, false)
        initializeView()
        return views
    }
    override fun initializeView() {
        SetInitListner()
    }
    private fun SetInitListner() {

        views!!.imgAddNBInquiry.setOnClickListener(this)

        arrayListNB = ArrayList()
        arrayListNBNew = ArrayList()

        val layoutManager = LinearLayoutManager(activity)
        views!!.RvNBList.layoutManager = layoutManager

        adapter = NBListAdapter(activity, arrayListNB!!,this@NBFragment)
        views!!.RvNBList.adapter = adapter

        if (isOnline(requireActivity())) {
            callManageNB()
        } else {
            internetErrordialog(requireActivity())
        }

        views!!.imgSearch.setOnClickListener {
            if(searchView.isSearchOpen) {
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
                val arrItemsFinal1: ArrayList<NBModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListNB!!) {
                        try {
                            if (model.LeadID.toString()!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.NBInquiryGUID!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                                model.NBInquiryGUID!!.toLowerCase().contains(strSearch.toLowerCase()) ) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception){
                        }
                    }
                    arrayListNBNew = arrItemsFinal1
                    adapter =  NBListAdapter(activity, arrayListNBNew!!,this@NBFragment)
                    views!!.RvNBList.adapter = adapter
                } else {
                    arrayListNBNew = arrayListNB
                    adapter = NBListAdapter(activity, arrayListNBNew!!, this@NBFragment)
                    views!!.RvNBList.adapter = adapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        views!!.searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListNBNew = arrayListNB
                adapter = NBListAdapter(activity, arrayListNBNew!!, this@NBFragment)
                views!!.RvNBList.adapter = adapter

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
            callManageNB()
            views!!.refreshLayout.isRefreshing = false
        }
    }
    override fun onClick(v: View?) {
        hideKeyboard(requireContext(), v)
        when (v?.id) {
            R.id.imgAddNBInquiry -> {
                preventTwoClick(v)
                val intent = Intent(getActivity(), AddNBActivity::class.java)
                intent.putExtra(AppConstant.STATE,AppConstant.S_ADD)
                requireActivity().startActivityForResult(intent, AppConstant.INTENT_1002)
            }
        }
    }
    private fun callManageNB() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("LeadID", 0)
//        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)

        val call = ApiUtils.apiInterface.ManageNBInquiryFindAllActive(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<NBResponse> {
            override fun onResponse(call: Call<NBResponse>, response: Response<NBResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListNB?.clear()
                        arrayListNBNew?.clear()
                        arrayListNB = response.body()?.Data!!
                        arrayListNBNew = arrayListNB

                        Log.e("arrayListNBNew","===>"+arrayListNBNew)
                        if(arrayListNBNew!!.size > 0) {

                        adapter = NBListAdapter(activity, arrayListNBNew!!,this@NBFragment)
                        views!!.RvNBList.adapter = adapter

                        views!!.shimmer.stopShimmer()
                        views!!.shimmer.gone()

                        } else {
                            Log.e("AAA"," ==> 222")
                            Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
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
            override fun onFailure(call: Call<NBResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }
    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when(type) {
            102 -> {
                preventTwoClick(view)
                val intent = Intent(getActivity(), AddNBActivity::class.java)
                intent.putExtra(AppConstant.STATE,AppConstant.S_EDIT)
                intent.putExtra("NBID",arrayListNBNew!![position].ID)
                intent.putExtra("NBInquiryGUID",arrayListNBNew!![position].NBInquiryGUID)
                startActivityForResult(intent, AppConstant.INTENT_1001)

            }
        }
    }
    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e("333","==>")
        if (requestCode == AppConstant.INTENT_1002) {
            if (isOnline(requireActivity())) {
                callManageNB()
            } else {
                internetErrordialog(requireActivity())
            }
        }
    }

}