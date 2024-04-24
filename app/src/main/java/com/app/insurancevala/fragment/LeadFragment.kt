package com.app.insurancevala.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import kotlinx.android.synthetic.main.fragment_lead.view.*
import kotlinx.android.synthetic.main.fragment_lead.*
import com.app.insurancevala.activity.Lead.AddLeadActivity
import com.app.insurancevala.activity.Lead.LeadDashboardActivity
import com.app.insurancevala.adapter.LeadListAdapter
import com.app.insurancevala.helper.PaginationScrollListener
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.LeadModel
import com.app.insurancevala.model.response.LeadResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeadFragment : BaseFragment(), View.OnClickListener, RecyclerClickListener {

    private var views: View? = null
    var arrayListLead: ArrayList<LeadModel>? = ArrayList()
    var arrayListLeadNew: ArrayList<LeadModel>? = ArrayList()

    var skip = 0
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    var searchText: String = ""

    lateinit var adapter: LeadListAdapter

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

        val manager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        views!!.RvLeadList.layoutManager = manager
        views!!.RvLeadList.isNestedScrollingEnabled = false

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
                    callManageLead(searchText, false)
                } else if (newText.trim().isEmpty()) {
                    arrayListLead?.clear()
                    arrayListLeadNew?.clear()
                    searchText = ""
                    callAPIDefaultData(false,false)
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                searchText = ""
                skip = 0
                callManageLead(searchText, false)
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

        views!!.RvLeadList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        callManageLead("", progress)
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

    private fun callManageLead(LeadSearch: String, progress: Boolean) {

        if (progress) {
            showProgress()
        }

        var jsonObject = JSONObject()
        jsonObject.put("Limit", 10)
        jsonObject.put("Skip", skip)
        jsonObject.put("LeadSearch", LeadSearch)

        val call = ApiUtils.apiInterface.LeadsFindAllActive(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<LeadResponse> {
            override fun onResponse(call: Call<LeadResponse>, response: Response<LeadResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayList = response.body()?.Data!!

                        if (skip > 0) {
                            arrayListLeadNew!!.addAll(arrayList)
                        } else {
                            arrayListLead = ArrayList()
                            arrayListLeadNew = ArrayList()
                            arrayListLead?.clear()
                            arrayListLeadNew?.clear()
                            arrayListLead!!.addAll(arrayList)
                            arrayListLeadNew = arrayListLead
                            views!!.RLNoData.gone()
                            views!!.FL.visible()
                        }

                        if (arrayListLeadNew!!.size > 0) {
                            if (skip == 0) {
                                setData()
                                views!!.shimmer.stopShimmer()
                                views!!.shimmer.gone()

                                if (arrayListLeadNew.isNullOrEmpty().not()) {
                                    if (arrayListLeadNew!!.size < 10) {
                                        isLastPage = true
                                    }
                                }
                            } else {
                                addLoadMoreData(arrayListLeadNew!!)
                            }
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
                    } else if (response.body()!!.Status == 1010 ||  response.body()?.Status == 201) {
                        views!!.shimmer.stopShimmer()
                        views!!.shimmer.gone()
                        views!!.FL.gone()
                        views!!.RLNoData.visible()
                    } else if (response.body()!!.Status == 1013) {
                        isLastPage = true
                        context!!.toast(response.body()?.Message.toString(), AppConstant.TOAST_SHORT)
                    } else {
                        if (LeadSearch != "" && skip == 0) {
                            views!!.RLNoData.visible()
                            views!!.FL.gone()
                            views!!.shimmer.stopShimmer()
                            views!!.shimmer.gone()
                        } else if (arrayListLeadNew!!.size == 0) {
                            views!!.RLNoData.visible()
                            views!!.FL.gone()
                            views!!.shimmer.stopShimmer()
                            views!!.shimmer.gone()
                        }
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

    fun addLoadMoreData(arrayList: ArrayList<LeadModel>) {
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
            isLoading = false
        } else {
            setData()
            views!!.RvLeadList.adapter = adapter
            isLoading = false
        }
    }

    private fun setData() {
        adapter = LeadListAdapter(context, arrayListLeadNew!!, this)
        views!!.RvLeadList.adapter = adapter
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
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(arrayListLeadNew!![position].LeadImage))
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
                callAPIDefaultData(true, true)
            } else {
                internetErrordialog(requireActivity())
            }
        }
    }

    private fun loadMoreRecyclerView(layoutManager: LinearLayoutManager) {
        views!!.RvLeadList?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                skip = skip + 10
                callManageLead(searchText, true)
            }
        })
    }

}