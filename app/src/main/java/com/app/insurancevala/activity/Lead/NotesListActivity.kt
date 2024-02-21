package com.app.insurancevala.activity.Lead

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.NotesListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.NotesModel
import com.app.insurancevala.model.response.NotesResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_notes_list.*
import kotlinx.android.synthetic.main.activity_notes_list.layout
import kotlinx.android.synthetic.main.fragment_lead.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotesListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    lateinit var adapter : NotesListAdapter
    var arrayListNotes: ArrayList<NotesModel>? = ArrayList()
    var arrayListNotesNew: ArrayList<NotesModel>? = ArrayList()
    var LeadID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_list)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        LeadID = intent.getIntExtra("LeadID",0)
    }

    override fun initializeView() {
        if (isOnline(this@NotesListActivity)) {
            callManageNotes()
        } else {
            internetErrordialog(this@NotesListActivity)
        }
        SetInitListner()
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)

        var manager = LinearLayoutManager(this)
        RvNotesList.layoutManager = manager

        arrayListNotes = ArrayList()
        adapter = NotesListAdapter(this, arrayListNotes!!,this@NotesListActivity)

        imgSearch.setOnClickListener {
            if(searchView.isSearchOpen) {
                searchView.closeSearch()
            } else {
                searchView.showSearch()
            }
        }

        searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                val arrItemsFinal1: ArrayList<NotesModel> = ArrayList()
                if (newText.trim().isNotEmpty()) {
                    val strSearch = newText
                    for (model in arrayListNotes!!) {
                        try {
                            if (model.Title!!.toLowerCase().contains(strSearch.toLowerCase())) {
                                arrItemsFinal1.add(model)
                            }
                        } catch (e: Exception){
                        }
                    }
                    arrayListNotesNew = arrItemsFinal1
                    val itemAdapter = NotesListAdapter(this@NotesListActivity, arrayListNotesNew!!,this@NotesListActivity)
                    RvNotesList.adapter = itemAdapter
                } else {
                    arrayListNotesNew = arrayListNotes
                    val itemAdapter = NotesListAdapter( this@NotesListActivity, arrayListNotesNew!!, this@NotesListActivity)
                    RvNotesList.adapter = itemAdapter
                }
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                arrayListNotesNew = arrayListNotes
                val itemAdapter = NotesListAdapter( this@NotesListActivity, arrayListNotesNew!!, this@NotesListActivity)
                RvNotesList.adapter = itemAdapter
            }

            override fun onSearchViewClosedAnimation() {
                AnimationUtils.loadAnimation(this@NotesListActivity, R.anim.searchview_close_anim)
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
                AnimationUtils.loadAnimation(this@NotesListActivity, R.anim.searchview_open_anim)
            }

        })

        refreshLayout.setOnRefreshListener {
            callManageNotes()
            refreshLayout.isRefreshing = false
        }
    }
    override fun onClick(v: View?) {
        hideKeyboard(this@NotesListActivity, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }
        }
    }
    @Suppress("DEPRECATION")
    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when(type) {
            101 -> {
                preventTwoClick(view)
            }
            102 -> {
                preventTwoClick(view)
                val intent = Intent(this, AddNotesActivity::class.java)
                intent.putExtra(AppConstant.STATE,AppConstant.S_EDIT)
                intent.putExtra("LeadID",LeadID)
                intent.putExtra("NoteGUID",arrayListNotesNew!![position].NoteGUID)
                startActivityForResult(intent, AppConstant.INTENT_1001)
            }
        }
    }
    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.INTENT_1001) {
            if (isOnline(this)) {
                callManageNotes()
            } else {
                internetErrordialog(this)
            }
        }
    }
    private fun callManageNotes() {

        showProgress()

        var jsonObject = JSONObject()

        jsonObject.put("LeadID", LeadID)
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)

        val call = ApiUtils.apiInterface.ManageNotes(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<NotesResponse> {
            override fun onResponse(call: Call<NotesResponse>, response: Response<NotesResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListNotes?.clear()
                        arrayListNotesNew?.clear()
                        arrayListNotes = response.body()?.Data!!
                        arrayListNotesNew = arrayListNotes

                        if(arrayListNotesNew!!.size > 0) {
                            adapter = NotesListAdapter(this@NotesListActivity, arrayListNotesNew!!,this@NotesListActivity)
                            RvNotesList.adapter = adapter

                            shimmer.stopShimmer()
                            shimmer.gone()

                        } else {
                            Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                            shimmer.stopShimmer()
                            shimmer.gone()
                            FL.gone()
                            RLNoData.visible()
                        }
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        shimmer.stopShimmer()
                        shimmer.gone()
                        FL.gone()
                        RLNoData.visible()
                    }
                }
            }

            override fun onFailure(call: Call<NotesResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })

    }

}