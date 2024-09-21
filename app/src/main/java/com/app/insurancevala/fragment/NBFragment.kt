package com.app.insurancevala.fragment

import android.content.ClipData.Item
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.app.insurancevala.R
import kotlinx.android.synthetic.main.fragment_nb.view.*
import kotlinx.android.synthetic.main.fragment_nb.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.activity.DashBoard.HomeActivity
import com.app.insurancevala.activity.NBInquiry.AddNBActivity
import com.app.insurancevala.activity.NBInquiry.InquiryListActivity
import com.app.insurancevala.activity.NBInquiry.InquiryTypeListActivity
import com.app.insurancevala.adapter.NBListAdapter
import com.app.insurancevala.helper.PaginationScrollListener
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.NBResponse
import com.app.insurancevala.model.response.NewNBModel
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
    var arrayListNB : ArrayList<NewNBModel>? = ArrayList()
    var arrayListNBNew : ArrayList<NewNBModel>? = ArrayList()

    var skip = 0
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    var searchText: String = ""

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

        val manager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        views!!.RvNBList.layoutManager = manager
        views!!.RvNBList.isNestedScrollingEnabled = false

        loadMoreRecyclerView(manager)

        if (isOnline(requireActivity())) {
            callAPIDefaultData(false, true)
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
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.trim().isNotEmpty() && newText.trim().length > 2) {
                    searchText = newText
                    skip = 0
                    isLastPage = false
                    isLoading = false
                    callManageNB(searchText, false)
                } else if (newText.trim().isEmpty()) {
                    arrayListNB?.clear()
                    arrayListNBNew?.clear()
                    searchText = ""
                    callAPIDefaultData(false,false)
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                searchText = ""
                skip = 0
                callManageNB(searchText, false)
                return false
            }
        })

        views!!.searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(activity, R.anim.searchview_close_anim)
                searchText = ""
                callAPIDefaultData(false, true)
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(activity, R.anim.searchview_open_anim)
            }

        })

        views!!.refreshLayout.setOnRefreshListener {
            hideKeyboard(requireContext(),refreshLayout)
            callAPIDefaultData(true, true)
            views!!.refreshLayout.isRefreshing = false
        }

        views!!.RvNBList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var position = adapter.getAdapterPosition().toString()

                var visibleItem = manager.findFirstCompletelyVisibleItemPosition()


                if (dy > 0) { // Scrolling down
                    LogUtil.e("AdapterPosition VisibleItem", "$visibleItem")
                    var position = adapter.getAdapterPosition().toString()
                    if (("0").contains(position)) {
                        views!!.refreshLayout.isEnabled = true
                    } else {
                        views!!.refreshLayout.isEnabled = false
                    }
                } else { // Scrolling up
                    if (visibleItem == 0) {
                        views!!.refreshLayout.isEnabled = true
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    private fun callAPIDefaultData(search: Boolean, progress: Boolean) {
        skip = 0
        isLoading = false
        isLastPage = false

        if (search) {
            searchView.closeSearch()
        }

        callManageNB("", progress)
    }
    override fun onClick(v: View?) {
        hideKeyboard(requireContext(), v)
        when (v?.id) {
            R.id.imgAddNBInquiry -> {
                preventTwoClick(v)
                val intent = Intent(getActivity(), AddNBActivity::class.java)
                intent.putExtra(AppConstant.STATE,AppConstant.S_ADD)
                requireActivity().startActivityForResult(intent, AppConstant.INTENT_1001)
            }
        }
    }
    private fun callManageNB(InquirySearch: String, progress: Boolean) {

        HomeActivity.isNavigationEnabled = false
        HomeActivity.homeMenu.isEnabled = false
        HomeActivity.leadMenu.isEnabled = false
        HomeActivity.moreMenu.isEnabled = false

        if (progress) {
            showProgress()
        }

        var jsonObject = JSONObject()
        jsonObject.put("Limit", 50)
        jsonObject.put("Skip", skip)
        jsonObject.put("InquirySearch", InquirySearch)
//        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)

        val call = ApiUtils.apiInterface.ManageNBInquiryFindAllActive(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<NBResponse> {
            override fun onResponse(call: Call<NBResponse>, response: Response<NBResponse>) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayList = response.body()?.Data!!

                        if (skip > 0) {
                            arrayListNBNew!!.addAll(arrayList)
                        } else {
                            arrayListNB = ArrayList()
                            arrayListNBNew = ArrayList()
                            arrayListNB?.clear()
                            arrayListNBNew?.clear()
                            arrayListNB!!.addAll(arrayList)
                            arrayListNBNew = arrayListNB
                            views!!.RLNoData.gone()
                            views!!.FL.visible()
                        }

                        if (arrayListNBNew!!.size > 0) {
                            if (skip == 0) {
                                setData()
                                views!!.shimmer.stopShimmer()
                                views!!.shimmer.gone()

                                if (arrayListNBNew.isNullOrEmpty().not()) {
                                    if (arrayListNBNew!!.size < 50) {
                                        isLastPage = true
                                    }
                                }
                            } else {
                                addLoadMoreData(arrayListNBNew!!)
                            }
                        } else {
                            Snackbar.make(
                                layout,
                                response.body()?.Details.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                            hideProgress()
                            views!!.shimmer.stopShimmer()
                            views!!.shimmer.gone()
                            views!!.FL.gone()
                            views!!.RLNoData.visible()
                            HomeActivity.isNavigationEnabled = true
                            HomeActivity.homeMenu.isEnabled = true
                            HomeActivity.leadMenu.isEnabled = true
                            HomeActivity.moreMenu.isEnabled = true
                        }
                    } else if (response.body()!!.Status == 1010 ||  response.body()?.Status == 201) {
                        hideProgress()
                        views!!.shimmer.stopShimmer()
                        views!!.shimmer.gone()
                        views!!.FL.gone()
                        views!!.RLNoData.visible()
                        HomeActivity.isNavigationEnabled = true
                        HomeActivity.homeMenu.isEnabled = true
                        HomeActivity.leadMenu.isEnabled = true
                        HomeActivity.moreMenu.isEnabled = true
                    } else if (response.body()!!.Status == 1013) {
                        hideProgress()
                        isLastPage = true
                        HomeActivity.isNavigationEnabled = true
                        HomeActivity.homeMenu.isEnabled = true
                        HomeActivity.leadMenu.isEnabled = true
                        HomeActivity.moreMenu.isEnabled = true
                        context!!.toast(response.body()?.Message.toString(), AppConstant.TOAST_SHORT)
                    } else {
                        hideProgress()
                        HomeActivity.isNavigationEnabled = true
                        HomeActivity.homeMenu.isEnabled = true
                        HomeActivity.leadMenu.isEnabled = true
                        HomeActivity.moreMenu.isEnabled = true
                        if (searchText != "" && skip == 0) {
                            views!!.RLNoData.visible()
                            views!!.FL.gone()
                            views!!.shimmer.stopShimmer()
                            views!!.shimmer.gone()
                        } else if (arrayListNBNew!!.size == 0) {
                            views!!.RLNoData.visible()
                            views!!.FL.gone()
                            views!!.shimmer.stopShimmer()
                            views!!.shimmer.gone()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<NBResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
                HomeActivity.isNavigationEnabled = true
                HomeActivity.homeMenu.isEnabled = true
                HomeActivity.leadMenu.isEnabled = true
                HomeActivity.moreMenu.isEnabled = true
            }
        })
    }

    fun addLoadMoreData(arrayList: ArrayList<NewNBModel>) {
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
            isLoading = false

            HomeActivity.isNavigationEnabled = true
            HomeActivity.homeMenu.isEnabled = true
            HomeActivity.leadMenu.isEnabled = true
            HomeActivity.moreMenu.isEnabled = true

            hideProgress()
        } else {
            setData()
            views!!.RvNBList.adapter = adapter
            isLoading = false
        }
    }

    private fun setData() {
        adapter = NBListAdapter(context, arrayListNBNew!!, this)
        views!!.RvNBList.adapter = adapter

        Handler().postDelayed({
            HomeActivity.isNavigationEnabled = true
            HomeActivity.homeMenu.isEnabled = true
            HomeActivity.leadMenu.isEnabled = true
            HomeActivity.moreMenu.isEnabled = true
            hideProgress()
        }, 1000)
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

            103 -> {
                preventTwoClick(view)
                val intent = Intent(getActivity(), InquiryListActivity::class.java)
                intent.putExtra("LeadID",arrayListNBNew!![position].ID)
                intent.putExtra("GUID",arrayListNBNew!![position].LeadGUID)
                startActivity(intent)

            }

            104 -> {
                preventTwoClick(view)
                val intent = Intent(getActivity(), InquiryTypeListActivity::class.java)
                intent.putExtra("LeadID",arrayListNBNew!![position].LeadID)
                intent.putExtra("GUID",arrayListNBNew!![position].LeadGUID)
                intent.putExtra("InquiryStatus","Open")
                startActivity(intent)

            }

            105 -> {
                preventTwoClick(view)
                val intent = Intent(getActivity(), InquiryTypeListActivity::class.java)
                intent.putExtra("LeadID",arrayListNBNew!![position].LeadID)
                intent.putExtra("GUID",arrayListNBNew!![position].LeadGUID)
                intent.putExtra("InquiryStatus","Closed")
                startActivity(intent)

            }
        }
    }
    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e("333","==>")
        if (requestCode == AppConstant.INTENT_1001) {
            if (isOnline(requireActivity())) {
                callAPIDefaultData(true, true)
            } else {
                internetErrordialog(requireActivity())
            }
        }
    }

    private fun loadMoreRecyclerView(layoutManager: LinearLayoutManager) {
        views!!.RvNBList?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                skip = skip + 50
                callManageNB(searchText, true)
            }
        })
    }

}