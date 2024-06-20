package com.app.insurancevala.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.activity.DashBoard.HomeActivity
import kotlinx.android.synthetic.main.fragment_lead.view.*
import kotlinx.android.synthetic.main.fragment_lead.*
import com.app.insurancevala.activity.Lead.AddLeadActivity
import com.app.insurancevala.activity.Lead.LeadDashboardActivity
import com.app.insurancevala.adapter.ClientListAdapter
import com.app.insurancevala.helper.PaginationScrollListener
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.LeadModel
import com.app.insurancevala.model.response.LeadResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.icon
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.position
import com.example.awesomedialog.title
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

    var tabPosition = 1

    var skip = 0
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    var searchText: String = ""

    lateinit var adapter: ClientListAdapter

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

        views!!.llExisting.setOnClickListener(this)
        views!!.llProspect.setOnClickListener(this)

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

    private fun changeTabColor(position: Int) {
        tabPosition = position
        when (position) {

            1 -> {
                llExisting.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_upcoming)
                tvExisting.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                llProspect.background = null
                tvProspect.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

                callAPIDefaultData(true, true)
            }

            2 -> {

                llExisting. background = null
                tvExisting.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

                llProspect.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_upcoming)
                tvProspect.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                callAPIDefaultData(true, true)
            }
        }
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

            R.id.llExisting -> {
                if (tabPosition != 1) {
                    tabPosition = 1
                    changeTabColor(tabPosition)
                }
            }

            R.id.llProspect -> {
                if (tabPosition != 2) {
                    tabPosition = 2
                    changeTabColor(tabPosition)
                }
            }
        }
    }

    private fun callManageLead(LeadSearch: String, progress: Boolean) {

        HomeActivity.isNavigationEnabled = false
        HomeActivity.homeMenu.isEnabled = false
        HomeActivity.nbMenu.isEnabled = false
        HomeActivity.moreMenu.isEnabled = false

        if (progress) {
            showProgress()
        }

        var jsonObject = JSONObject()
        jsonObject.put("Limit", 50)
        jsonObject.put("Skip", skip)
        jsonObject.put("LeadStage", tabPosition)
        jsonObject.put("LeadSearch", LeadSearch)

        val call = ApiUtils.apiInterface.LeadsFindAllActive(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<LeadResponse> {
            override fun onResponse(call: Call<LeadResponse>, response: Response<LeadResponse>) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        if (LeadSearch != "" && tabPosition == 1) {
                            tvExisting.setText("Existing  (" + response.body()?.Data!!.get(0).SearchCount + ")")
                        } else if (LeadSearch != "" && tabPosition == 2){
                            tvProspect.setText("Prospect  (" + response.body()?.Data!!.get(0).SearchCount + ")")
                        } else if (LeadSearch == "") {
                            if (response.body()?.Data!!.get(0).ExistingCount != null && response.body()?.Data!!.get(0).ExistingCount != 0) {
                                tvExisting.setText("Existing  (" + response.body()?.Data!!.get(0).ExistingCount + ")")
                            }

                            if (response.body()?.Data!!.get(0).ProspectCount != null && response.body()?.Data!!.get(0).ProspectCount != 0) {
                                tvProspect.setText("Prospect  (" + response.body()?.Data!!.get(0).ProspectCount + ")")
                            }
                        }

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
                                    if (arrayListLeadNew!!.size < 50) {
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
                        HomeActivity.nbMenu.isEnabled = true
                        HomeActivity.moreMenu.isEnabled = true
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
                HomeActivity.isNavigationEnabled = true
                HomeActivity.homeMenu.isEnabled = true
                HomeActivity.nbMenu.isEnabled = true
                HomeActivity.moreMenu.isEnabled = true
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
        adapter = ClientListAdapter(context, arrayListLeadNew!!, tabPosition, this)
        views!!.RvLeadList.adapter = adapter

        Handler().postDelayed({
            HomeActivity.isNavigationEnabled = true
            HomeActivity.homeMenu.isEnabled = true
            HomeActivity.nbMenu.isEnabled = true
            HomeActivity.moreMenu.isEnabled = true
            hideProgress()
        }, 1000)
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

            104 -> {
                preventTwoClick(view)
                AwesomeDialog.build(requireActivity()).title("Warning !!!")
                    .body("Are you sure want to delete this Client?")
                    .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                    .onNegative("No") {

                    }.onPositive("Yes") {
                        CallDeleteAPI(arrayListLeadNew!![position].LeadGUID!!)
                    }
            }
        }
    }

    private fun CallDeleteAPI(GUID: String) {

        var jsonObject = JSONObject()
        jsonObject.put("LeadGUID", GUID)

        val call = ApiUtils.apiInterface.ManageLeadsDelete(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>, response: Response<CommonResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        callAPIDefaultData(true, true)
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
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
                skip = skip + 50
                callManageLead(searchText, true)
            }
        })
    }

}