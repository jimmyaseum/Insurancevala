package com.app.insurancevala.activity.Lead

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.AddMoreFamilyMemberAdapter
import com.app.insurancevala.adapter.bottomsheetadapter.*
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.interFase.RecyclerItemClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.api.LeadCountResponse
import com.app.insurancevala.model.pojo.FamilyMemberInfoModel
import com.app.insurancevala.model.pojo.InquiryInformationModel
import com.app.insurancevala.model.response.*
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.example.awesomedialog.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_lead.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddLeadActivity : BaseActivity(), View.OnClickListener, RecyclerItemClickListener {

    var isShow: Int = 0
    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var LeadID: Int? = null
    var LeadGUID: String? = null

    var arrayListInitial: ArrayList<InitialModel>? = ArrayList()
    var mInitial: String = ""
    var mInitialID: Int = 0
    var mInitialItem: String = ""
    var mInitialItemID: Int = 0
    var mInitialItemPostion: Int = 0

    var arrayListRelation: ArrayList<RelationModel>? = ArrayList()
    var mRelation: String = ""
    var mRelationID: Int = 0
    var mRelationItemPostion: Int = 0

    var arrayListFamilyInfo: ArrayList<FamilyMemberInfoModel>? = ArrayList()
    lateinit var adapter: AddMoreFamilyMemberAdapter

    var arrayListOccupation: ArrayList<OccupationModel>? = ArrayList()
    var mOccupationName: String = ""
    var mOccupationID: Int = 0

    var arrayListUsers: ArrayList<UserModel>? = ArrayList()
    var mUsers: String = ""
    var mUsersID: Int = 0

    var arrayListleadsource: ArrayList<LeadSourceModel>? = ArrayList()
    var mLeadsource: String = ""
    var mLeadsourceID: Int = 0

    var arrayListCity: ArrayList<CityModel>? = ArrayList()
    var mCity: String = ""
    var mCityID: Int = 0
    var mState: String = ""
    var mStateID: Int = 0
    var mCountry: String = ""
    var mCountryID: Int = 0

    var calendarNow: Calendar? = null
    var mDOBItemPostion: Int = 0
    var year = 0
    var month = 0
    var day = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lead)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        state = intent.getStringExtra(AppConstant.STATE)
    }

    override fun initializeView() {
        SetAnimationControl1()
        setMasterData()
        SetInitListner()
    }

    private fun SetAnimationControl1() {
        val topanim = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        val bottomanim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim)
    }

    private fun setMasterData() {
        if (state.equals(AppConstant.S_ADD)) {

            txtHearderText.text = "Create Client"
            if (isOnline(this)) {
                callManageLeadCount()
                callManageInitial(0, "Activity")
                callManageOccupation(0)
                callRelation(0)
                callManageUsers(0)
                callManageLeadSource(0)
                callManageCity(0)
            } else {
                internetErrordialog(this@AddLeadActivity)
            }
        } else if (state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update Client"
            if (isOnline(this)) {
                LeadID = intent.getIntExtra("LeadID", 0)
                LeadGUID = intent.getStringExtra("GUID")
                callManageLeadGUID()
            } else {
                internetErrordialog(this@AddLeadActivity)
            }
        }
    }

    private fun SetInitListner() {
        imgBack.setOnClickListener(this)
        txtShowAllFiels.setOnClickListener(this)
        tvAddMore.setOnClickListener(this)
        txtSave.setOnClickListener(this)

        edtInitial.setOnClickListener(this)
        edtOccupation.setOnClickListener(this)
        rbForExisting.setOnClickListener(this)
        rbForProspect.setOnClickListener(this)
        edtLeadOwner.setOnClickListener(this)
        edtLeadSource.setOnClickListener(this)
        edtDateOfBirth.setOnClickListener(this)
        edtMarriage.setOnClickListener(this)
        edtCity.setOnClickListener(this)

        rvFamilyMember.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        rvFamilyMember.isNestedScrollingEnabled = false
    }

    override fun onClick(v: View?) {
        hideKeyboard(this, v)
        when (v?.id) {
            R.id.imgBack -> {
                preventTwoClick(v)
                finish()
            }

            R.id.txtShowAllFiels -> {
                if (isShow % 2 == 0) {
                    txtShowAllFiels.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_up,
                        0
                    )
                    llMoreFields.setVisibility(View.VISIBLE)
                    txtShowAllFiels.text = "Swich to smart view"
                } else {
                    txtShowAllFiels.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_down,
                        0
                    )
                    llMoreFields.setVisibility(View.GONE)
                    txtShowAllFiels.text = "Show all fields"
                }
                isShow = isShow + 1
            }

            R.id.txtSave -> {
                preventTwoClick(v)
                validation()
            }

            R.id.tvAddMore -> {
                preventTwoClick(v)
                if (::adapter.isInitialized) {
                    if (adapter.getAdapterArrayList()!!.size > 0) {
                        adapter.addItem(FamilyMemberInfoModel(), 1)
                    } else {
                        setDefaultData()
                    }
                } else {
                    setDefaultData()
                }
                rvFamilyMember.scrollToPosition(adapter.itemCount - 1)

                // For scrolling the ScrollView to the bottom
                scrollview.post {
                    scrollview.fullScroll(View.FOCUS_DOWN)
                }
            }

            R.id.edtInitial -> {
                preventTwoClick(v)
                if (arrayListInitial.isNullOrEmpty()) {
                    callManageInitial(1, "Activity")
                } else {
                    selectInitialDialog(0)
                }
            }

            R.id.edtOccupation -> {
                preventTwoClick(v)
                if (arrayListOccupation.isNullOrEmpty()) {
                    callManageOccupation(1)
                } else {
                    selectOccupationDialog()
                }
            }

            R.id.rbForExisting -> {
                preventTwoClick(v)
                rating_bar.progressTintList = ColorStateList.valueOf(getColor(R.color.gold))
            }

            R.id.rbForProspect -> {
                preventTwoClick(v)
                rating_bar.progressTintList = ColorStateList.valueOf(getColor(R.color.silver))
            }


            R.id.edtLeadOwner -> {
                preventTwoClick(v)
                if (arrayListUsers.isNullOrEmpty()) {
                    callManageUsers(1)
                } else {
                    selectUsersDialog()
                }
            }

            R.id.edtLeadSource -> {
                preventTwoClick(v)
                if (arrayListleadsource.isNullOrEmpty()) {
                    callManageLeadSource(1)
                } else {
                    selectLeadSourceDialog()
                }
            }

            R.id.edtDateOfBirth -> {
                showDatePickerDialog(1, edtDateOfBirth.text.toString())
            }

            R.id.edtMarriage -> {
                showDatePickerDialog(2, edtMarriage.text.toString())
            }

            R.id.edtCity -> {
                preventTwoClick(v)
                if (arrayListCity.isNullOrEmpty()) {
                    callManageCity(1)
                } else {
                    selectCityDialog()
                }
            }
        }
    }

    private fun setDefaultData() {
        arrayListFamilyInfo = ArrayList()
        arrayListFamilyInfo?.add(FamilyMemberInfoModel())
        setAdapterData(arrayListFamilyInfo)
    }

    private fun setAdapterData(arrayList: ArrayList<FamilyMemberInfoModel>?) {
        adapter = AddMoreFamilyMemberAdapter(arrayList, this@AddLeadActivity)
        rvFamilyMember.adapter = adapter

        rvFamilyMember.smoothScrollToPosition(adapter.getItemCount() - 1)
    }

    private fun callManageInitial(mode: Int, flag: String) {
        if (mode == 1) {
            showProgress()
        }
        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageInitial(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<InitialResponse> {
            override fun onResponse(
                call: Call<InitialResponse>,
                response: Response<InitialResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListInitial = response.body()?.Data!!

                        if (arrayListInitial!!.size > 0) {
                            for (i in 0 until arrayListInitial!!.size) {
                                if (state.equals(AppConstant.S_EDIT)) {
                                    if (arrayListInitial!![i].ID == mInitialID) {
                                        arrayListInitial!![i].IsSelected = true
                                        mInitialID = arrayListInitial!![i].ID!!
                                        mInitial = arrayListInitial!![i].Initial!!
                                        edtInitial.setText(mInitial)
                                        edtInitial.setError(null)
                                    }
                                }
                            }
                        }

                        if (mode == 1 && flag == "Activity") {
                            selectInitialDialog(0)
                        } else if (mode == 1 && flag == "Adapter") {
                            selectInitialDialog(1)
                        }
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<InitialResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectInitialDialog(mode: Int) {
        val dialogSelectInitial = Dialog(this)
        dialogSelectInitial.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectInitial.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectInitial.window!!.attributes)

        dialogSelectInitial.window!!.attributes = lp
        dialogSelectInitial.setCancelable(true)
        dialogSelectInitial.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectInitial.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectInitial.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectInitial.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectInitial.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectInitial.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectInitial.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectInitial.dismiss()
        }

        txtid.text = "Select Initial"

        val itemAdapter = BottomSheetInitialListAdapter(this, arrayListInitial!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(view: View, position: Int, type: Int) {

                if (mode == 1) {
                    itemAdapter.updateItem(position)
                    mInitialItem = arrayListInitial!![position].Initial!!
                    mInitialItemID = arrayListInitial!![position].ID!!
                    adapter.updateInitialItem(mInitialItemPostion, mInitialItem, mInitialItemID)
                    dialogSelectInitial.dismiss()
                } else {
                    itemAdapter.updateItem(position)
                    mInitialID = arrayListInitial!![position].ID!!
                    mInitial = arrayListInitial!![position].Initial!!
                    edtInitial.setText(mInitial)
                    edtInitial.setError(null)
                    dialogSelectInitial.dismiss()
                }
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListInitial!!.size > 6) {
            edtSearchCustomer.visible()
        } else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<InitialModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListInitial!!) {
                        if (model.Initial!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetInitialListAdapter(this@AddLeadActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(view: View, position: Int, type: Int) {

                            mInitial = arrItemsFinal1[position].Initial!!
                            mInitialID = arrItemsFinal1[position].ID!!
                            adapter.updateInitialItem(mInitialItemPostion, mRelation, mRelationID)
                            dialogSelectInitial.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetInitialListAdapter(this@AddLeadActivity, arrayListInitial!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(view: View, position: Int, type: Int) {

                            mInitial = arrayListInitial!![position].Initial!!
                            mInitialID = arrayListInitial!![position].ID!!
                            adapter.updateInitialItem(mInitialItemPostion, mRelation, mRelationID)
                            dialogSelectInitial.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectInitial.show()
    }

    private fun callRelation(mode: Int) {
        if (mode == 1) {
            showProgress()
        }
        val call = ApiUtils.apiInterface.getRelationAllActive()
        call.enqueue(object : Callback<RelationResponse> {
            override fun onResponse(
                call: Call<RelationResponse>,
                response: Response<RelationResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListRelation = response.body()?.Data!!

                        if (mode == 1) {
                            selectRelationDialog()
                        }
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<RelationResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectRelationDialog() {
        val dialogSelectRelation = Dialog(this)
        dialogSelectRelation.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectRelation.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectRelation.window!!.attributes)

        dialogSelectRelation.window!!.attributes = lp
        dialogSelectRelation.setCancelable(true)
        dialogSelectRelation.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectRelation.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectRelation.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectRelation.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectRelation.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectRelation.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectRelation.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectRelation.dismiss()
        }

        txtid.text = "Select Relation"

        val itemAdapter = BottomSheetRelationListAdapter(this, arrayListRelation!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mRelation = arrayListRelation!![pos].RelationName!!
                mRelationID = arrayListRelation!![pos].ID!!
                adapter.updateRelationItem(mRelationItemPostion, mRelation, mRelationID)
                dialogSelectRelation!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListRelation!!.size > 6) {
            edtSearchCustomer.visible()
        } else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<RelationModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListRelation!!) {
                        if (model.RelationName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetRelationListAdapter(this@AddLeadActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mRelation = arrItemsFinal1!![pos].RelationName!!
                            mRelationID = arrItemsFinal1!![pos].ID!!
                            adapter.updateRelationItem(mRelationItemPostion, mRelation, mRelationID)
                            dialogSelectRelation!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetRelationListAdapter(this@AddLeadActivity, arrayListRelation!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            mRelation = arrayListRelation!![pos].RelationName!!
                            mRelationID = arrayListRelation!![pos].ID!!
                            adapter.updateRelationItem(mRelationItemPostion, mRelation, mRelationID)
                            dialogSelectRelation!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectRelation!!.show()
    }

    private fun callManageOccupation(mode: Int) {
        if (mode == 1) {
            showProgress()
        }
        val call = ApiUtils.apiInterface.getOccupationAllActive()
        call.enqueue(object : Callback<OccupationResponse> {
            override fun onResponse(
                call: Call<OccupationResponse>,
                response: Response<OccupationResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListOccupation = response.body()?.Data!!

                        if (arrayListOccupation!!.size > 0) {
                            for (i in 0 until arrayListOccupation!!.size) {
                                if (state.equals(AppConstant.S_EDIT)) {
                                    if (arrayListOccupation!![i].ID == mOccupationID) {
                                        arrayListOccupation!![i].IsSelected = true
                                        mOccupationID = arrayListOccupation!![i].ID!!
                                        mOccupationName = arrayListOccupation!![i].OccupationName!!
                                        edtOccupation.setText(mOccupationName)
                                        edtOccupation.setError(null)
                                    }
                                }
                            }
                        }

                        if (mode == 1) {
                            selectOccupationDialog()
                        }
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<OccupationResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectOccupationDialog() {
        var dialogSelectOccupation = Dialog(this)
        dialogSelectOccupation.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectOccupation.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectOccupation.window!!.attributes)

        dialogSelectOccupation.window!!.attributes = lp
        dialogSelectOccupation.setCancelable(true)
        dialogSelectOccupation.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectOccupation.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectOccupation.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectOccupation.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectOccupation.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectOccupation.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectOccupation.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectOccupation.dismiss()
        }

        txtid.text = "Select Occupation"

        val itemAdapter = BottomSheetOccupationListAdapter(this, arrayListOccupation!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mOccupationID = arrayListOccupation!![pos].ID!!
                mOccupationName = arrayListOccupation!![pos].OccupationName!!
                edtOccupation.setText(mOccupationName)
                edtOccupation.setError(null)
                dialogSelectOccupation!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListOccupation!!.size > 6) {
            edtSearchCustomer.visible()
        } else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<OccupationModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListOccupation!!) {
                        if (model.OccupationName!!.toLowerCase()
                                .contains(strSearch.toLowerCase())
                        ) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetOccupationListAdapter(this@AddLeadActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mOccupationID = arrItemsFinal1!![pos].ID!!
                            mOccupationName = arrItemsFinal1!![pos].OccupationName!!
                            edtOccupation.setText(mOccupationName)
                            edtOccupation.setError(null)
                            dialogSelectOccupation!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetOccupationListAdapter(
                            this@AddLeadActivity,
                            arrayListOccupation!!
                        )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mOccupationID = arrayListOccupation!![pos].ID!!
                            mOccupationName = arrayListOccupation!![pos].OccupationName!!
                            edtOccupation.setText(mOccupationName)
                            edtOccupation.setError(null)
                            dialogSelectOccupation!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectOccupation!!.show()
    }

    private fun callManageUsers(mode: Int) {
        if (mode == 1) {
            showProgress()
        }

        if (sharedPreference == null) {
            sharedPreference = SharedPreference(this)
        }
        val Userid = sharedPreference!!.getPreferenceString(PrefConstants.PREF_USER_ID)!!

        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageUsers(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListUsers = response.body()?.Data!!

                        if (arrayListUsers!!.size > 0) {
                            for (i in 0 until arrayListUsers!!.size) {
                                if (state.equals(AppConstant.S_ADD)) {
                                    if (arrayListUsers!![i].ID == Userid.toInt()) {
                                        arrayListUsers!![i].IsSelected = true
                                        mUsersID = arrayListUsers!![i].ID!!
                                        mUsers =
                                            arrayListUsers!![i].FirstName!! + " " + arrayListUsers!![i].LastName!!
                                        edtLeadOwner.setText(mUsers)
                                        edtLeadOwner.setError(null)
                                    }
                                } else if (state.equals(AppConstant.S_EDIT)) {
                                    if (arrayListUsers!![i].ID == mUsersID) {
                                        arrayListUsers!![i].IsSelected = true
                                        mUsersID = arrayListUsers!![i].ID!!
                                        mUsers =
                                            arrayListUsers!![i].FirstName!! + " " + arrayListUsers!![i].LastName!!
                                        edtLeadOwner.setText(mUsers)
                                        edtLeadOwner.setError(null)
                                    }
                                }
                            }
                        }

                        if (mode == 1) {
                            selectUsersDialog()
                        }
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectUsersDialog() {
        var dialogSelectUsers = Dialog(this)
        dialogSelectUsers.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectUsers.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectUsers.window!!.attributes)

        dialogSelectUsers.window!!.attributes = lp
        dialogSelectUsers.setCancelable(true)
        dialogSelectUsers.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectUsers.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectUsers.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectUsers.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectUsers.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectUsers.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectUsers.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectUsers.dismiss()
        }

        txtid.text = "Select Lead Owner"

        val itemAdapter = BottomSheetUsersListAdapter(this, arrayListUsers!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mUsersID = arrayListUsers!![pos].ID!!
                mUsers = arrayListUsers!![pos].FirstName!! + " " + arrayListUsers!![pos].LastName!!
                edtLeadOwner.setText(mUsers)
                edtLeadOwner.setError(null)
                dialogSelectUsers!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListUsers!!.size > 6) {
            edtSearchCustomer.visible()
        } else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<UserModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListUsers!!) {
                        if (model.FirstName!!.toLowerCase().contains(strSearch.toLowerCase()) ||
                            model.LastName!!.toLowerCase().contains(strSearch.toLowerCase())
                        ) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetUsersListAdapter(this@AddLeadActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mUsersID = arrItemsFinal1!![pos].ID!!
                            mUsers =
                                arrItemsFinal1!![pos].FirstName!! + " " + arrItemsFinal1!![pos].LastName!!
                            edtLeadOwner.setText(mUsers)
                            edtLeadOwner.setError(null)
                            dialogSelectUsers!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetUsersListAdapter(this@AddLeadActivity, arrayListUsers!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mUsersID = arrayListUsers!![pos].ID!!
                            mUsers =
                                arrayListUsers!![pos].FirstName!! + " " + arrayListUsers!![pos].LastName!!
                            edtLeadOwner.setText(mUsers)
                            edtLeadOwner.setError(null)
                            dialogSelectUsers!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectUsers!!.show()
    }

    private fun callManageLeadSource(mode: Int) {
        if (mode == 1) {
            showProgress()
        }
        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageLeadSource(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<LeadSourceResponse> {
            override fun onResponse(
                call: Call<LeadSourceResponse>,
                response: Response<LeadSourceResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListleadsource = response.body()?.Data!!

                        if (arrayListleadsource!!.size > 0) {
                            for (i in 0 until arrayListleadsource!!.size) {
                                if (state.equals(AppConstant.S_EDIT)) {
                                    if (arrayListleadsource!![i].ID == mLeadsourceID) {
                                        arrayListleadsource!![i].IsSelected = true
                                        mLeadsourceID = arrayListleadsource!![i].ID!!
                                        mLeadsource = arrayListleadsource!![i].LeadSource!!
                                        edtLeadSource.setText(mLeadsource)
                                        edtLeadSource.setError(null)
                                    }
                                }
                            }
                        }

                        if (mode == 1) {
                            selectLeadSourceDialog()
                        }
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<LeadSourceResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectLeadSourceDialog() {
        var dialogSelectLeadSource = Dialog(this)
        dialogSelectLeadSource.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectLeadSource.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectLeadSource.window!!.attributes)

        dialogSelectLeadSource.window!!.attributes = lp
        dialogSelectLeadSource.setCancelable(true)
        dialogSelectLeadSource.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectLeadSource.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectLeadSource.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer =
            dialogSelectLeadSource.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer =
            dialogSelectLeadSource.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectLeadSource.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectLeadSource.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectLeadSource.dismiss()
        }

        txtid.text = "Select Lead Source"

        val itemAdapter = BottomSheetLeadSourceListAdapter(this, arrayListleadsource!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mLeadsource = arrayListleadsource!![pos].LeadSource!!
                mLeadsourceID = arrayListleadsource!![pos].ID!!
                edtLeadSource.setText(mLeadsource)
                edtLeadSource.setError(null)
                dialogSelectLeadSource!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListleadsource!!.size > 6) {
            edtSearchCustomer.visible()
        } else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<LeadSourceModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListleadsource!!) {
                        if (model.LeadSource!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetLeadSourceListAdapter(this@AddLeadActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mLeadsource = arrItemsFinal1!![pos].LeadSource!!
                            mLeadsourceID = arrItemsFinal1!![pos].ID!!
                            edtLeadSource.setText(mLeadsource)
                            edtLeadSource.setError(null)
                            dialogSelectLeadSource!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetLeadSourceListAdapter(
                            this@AddLeadActivity,
                            arrayListleadsource!!
                        )
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mLeadsource = arrayListleadsource!![pos].LeadSource!!
                            mLeadsourceID = arrayListleadsource!![pos].ID!!
                            edtLeadSource.setText(mLeadsource)
                            edtLeadSource.setError(null)
                            dialogSelectLeadSource!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectLeadSource!!.show()
    }

    private fun callManageCity(mode: Int) {
        if (mode == 1) {
            showProgress()
        }
        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETALLACTIVEWITHFILTER)
        val call = ApiUtils.apiInterface.ManageCityFindAll(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CityResponse> {
            override fun onResponse(
                call: Call<CityResponse>,
                response: Response<CityResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListCity = response.body()?.Data!!

                        if (arrayListCity!!.size > 0) {
                            for (i in 0 until arrayListCity!!.size) {
                                if (state.equals(AppConstant.S_EDIT)) {
                                    if (arrayListCity!![i].ID == mCityID) {
                                        arrayListCity!![i].IsSelected = true

                                        mCityID = arrayListCity!![i].ID!!
                                        mCity = arrayListCity!![i].CityName!!
                                        mStateID = arrayListCity!![i].StateID!!
                                        mState = arrayListCity!![i].StateName!!
                                        mCountryID = arrayListCity!![i].CountryID!!
                                        mCountry = arrayListCity!![i].CountryName!!

                                        edtCity.setText(mCity)
                                        edtCity.setError(null)
                                        edtState.setText(mState)
                                        edtState.setError(null)
                                        edtCountry.setText(mCountry)
                                        edtCountry.setError(null)

                                    }
                                }
                            }
                        }

                        if (mode == 1) {
                            selectCityDialog()
                        }
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectCityDialog() {
        var dialogSelectCity = Dialog(this)
        dialogSelectCity.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectCity.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectCity.window!!.attributes)

        dialogSelectCity.window!!.attributes = lp
        dialogSelectCity.setCancelable(true)
        dialogSelectCity.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectCity.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogSelectCity.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectCity.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectCity.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid = dialogSelectCity.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectCity.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectCity.dismiss()
        }

        txtid.text = "Select City"

        val itemAdapter = BottomSheetCitiesListAdapter(this, arrayListCity!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mCity = arrayListCity!![pos].CityName!!
                mCityID = arrayListCity!![pos].ID!!
                mState = arrayListCity!![pos].StateName!!
                mStateID = arrayListCity!![pos].StateID!!
                mCountry = arrayListCity!![pos].CountryName!!
                mCountryID = arrayListCity!![pos].CountryID!!

                edtCity.setText(mCity)
                edtState.setText(mState)
                edtCountry.setText(mCountry)

                edtCity.setError(null)
                edtState.setError(null)
                edtCountry.setError(null)

                dialogSelectCity!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if (arrayListCity!!.size > 6) {
            edtSearchCustomer.visible()
        } else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<CityModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListCity!!) {
                        if (model.CityName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter =
                        BottomSheetCitiesListAdapter(this@AddLeadActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mCity = arrItemsFinal1!![pos].CityName!!
                            mCityID = arrItemsFinal1!![pos].ID!!
                            mState = arrItemsFinal1!![pos].StateName!!
                            mStateID = arrItemsFinal1!![pos].StateID!!
                            mCountry = arrItemsFinal1!![pos].CountryName!!
                            mCountryID = arrItemsFinal1!![pos].CountryID!!

                            edtCity.setText(mCity)
                            edtState.setText(mState)
                            edtCountry.setText(mCountry)

                            edtCity.setError(null)
                            edtState.setError(null)
                            edtCountry.setError(null)

                            dialogSelectCity!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter =
                        BottomSheetCitiesListAdapter(this@AddLeadActivity, arrayListCity!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {


                            itemAdapter.updateItem(pos)
                            mCity = arrayListCity!![pos].CityName!!
                            mCityID = arrayListCity!![pos].ID!!
                            mState = arrayListCity!![pos].StateName!!
                            mStateID = arrayListCity!![pos].StateID!!
                            mCountry = arrayListCity!![pos].CountryName!!
                            mCountryID = arrayListCity!![pos].CountryID!!

                            edtCity.setText(mCity)
                            edtState.setText(mState)
                            edtCountry.setText(mCountry)

                            edtCity.setError(null)
                            edtState.setError(null)
                            edtCountry.setError(null)

                            dialogSelectCity!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectCity!!.show()
    }

    private fun callDeleteFamilyMember(ID: Int, position: Int) {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("ID", ID)
        val call = ApiUtils.apiInterface.deleteFamilyDetail(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        adapter.remove(position)
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun showDatePickerDialog(mode: Int, strDate: String) {

        if (year == 0 || month == 0 || day == 0 || strDate.isNullOrEmpty()) {
            calendarNow = Calendar.getInstance()
            year = calendarNow!!.get(Calendar.YEAR)
            month = calendarNow!!.get(Calendar.MONTH)
            day = calendarNow!!.get(Calendar.DAY_OF_MONTH)
        } else {
            val parts = strDate.split("/")
            day = parts[0].toInt()
            month = parts[1].toInt() - 1
            year = parts[2].toInt()
        }

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                val selectDate = convertDateStringToString(
                    "$dayOfMonth/${monthOfYear + 1}/$year",
                    AppConstant.DATE_INPUT_FORMAT
                )!!

                /*  Date of Birth  */
                if (mode == 1) {
                    edtDateOfBirth.text = selectDate.toEditable()

                    /*  Marriage Date  */
                } else if (mode == 2) {
                    edtMarriage.text = selectDate.toEditable()
                }


                this.year = year
                this.month = monthOfYear
                this.day = dayOfMonth

            },
            year,
            month,
            day
        )
        dpd.datePicker.maxDate = System.currentTimeMillis()
        dpd.show()
    }

    private fun showDatePickerDialog() {

        calendarNow = Calendar.getInstance()

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calendarNow!!.set(Calendar.YEAR, year)
                calendarNow!!.set(Calendar.MONTH, monthOfYear)
                calendarNow!!.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val mdate = SimpleDateFormat(
                    AppConstant.yyyy_MM_dd_Dash,
                    Locale.US
                ).format(calendarNow!!.time)

                val selecteddate = SimpleDateFormat(
                    AppConstant.dd_MM_yyyy_HH_mm_ss,
                    Locale.US
                ).format(calendarNow!!.time)
                val date = convertDateStringToString(
                    selecteddate,
                    AppConstant.dd_MM_yyyy_HH_mm_ss,
                    AppConstant.dd_LLL_yyyy
                )

                adapter.updateDOBItem(mDOBItemPostion, date!!, mdate)
            },
            calendarNow!!.get(Calendar.YEAR),
            calendarNow!!.get(Calendar.MONTH),
            calendarNow!!.get(Calendar.DAY_OF_MONTH)
        )
//                dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.datePicker.maxDate = System.currentTimeMillis()
        dpd.show()
    }

    private fun validation() {

        when (isValidate()) {
            true -> {
                if (isOnline(this)) {
                    ManageCreateLeadAPI()
                } else {
                    internetErrordialog(this@AddLeadActivity)
                }
            }
        }
    }

    private fun isValidate(): Boolean {

        var isValidate = true

        if (edtInitial.text.isEmpty()) {
            edtInitial.setError("Select Initial", errortint(this))
            isValidate = false
        }
        if (edtFirstName.text.isEmpty()) {
            edtFirstName.setError("Enter FirstName", errortint(this))
            isValidate = false
        }
        if (edtLastName.text.isEmpty()) {
            edtLastName.setError("Enter LastName", errortint(this))
            isValidate = false
        }
        if (::adapter.isInitialized) {
            if (!adapter.isValidateItem()) {
                isValidate = false
            }
        }

        return isValidate
    }

    private fun ManageCreateLeadAPI() {

        showProgress()

        val birDate = convertDateStringToString(
            edtDateOfBirth.text.toString().trim(),
            AppConstant.DATE_INPUT_FORMAT,
            AppConstant.DEFAULT_DATE_FORMAT
        )
        val mrgDate = convertDateStringToString(
            edtMarriage.text.toString().trim(),
            AppConstant.DATE_INPUT_FORMAT,
            AppConstant.DEFAULT_DATE_FORMAT
        )

        var mLeadStage = 0
        if (rbForExisting.isChecked) {
            mLeadStage = 1
        } else if (rbForProspect.isChecked) {
            mLeadStage = 2
        }

        val jsonArrayFamilyMember = JSONArray()
        if (::adapter.isInitialized) {
            val arrayList = adapter.getAdapterArrayList()
            if (!arrayList.isNullOrEmpty()) {
                for (i in 0 until arrayList!!.size) {
                    val jsonObjectFamilyMember = JSONObject()
                    jsonObjectFamilyMember.put("ID", arrayList[i].ID)
                    jsonObjectFamilyMember.put("InitialID", arrayList[i].InitialID)
                    jsonObjectFamilyMember.put("RelationID", arrayList[i].RelationId)
                    jsonObjectFamilyMember.put("FirstName", arrayList[i].FirstName)
                    jsonObjectFamilyMember.put("LastName", arrayList[i].LastName)
                    jsonObjectFamilyMember.put("MobileNo", arrayList[i].MobileNo)
                    if (arrayList[i].mDateOfBirth != "") {
                        jsonObjectFamilyMember.put("BirthDate", arrayList[i].mDateOfBirth)
                    }
                    jsonArrayFamilyMember.put(jsonObjectFamilyMember)
                }
            }
        }

        val jsonObject = JSONObject()
        jsonObject.put("LeadStage", mLeadStage)
        jsonObject.put("InitialID", mInitialID)
        jsonObject.put("CategoryID", rating_bar.rating.toInt())
        jsonObject.put("OccupationID", mOccupationID)
        jsonObject.put("FirstName", edtFirstName.text.toString().trim())
        jsonObject.put("LastName", edtLastName.text.toString().trim())
        jsonObject.put("MobileNo", edtMobileNo.text.toString().trim())
        jsonObject.put("EmailID", edtEmailAddress.text.toString().trim())
        jsonObject.put("GroupCode", edtGroupCode.text.toString().trim())
        jsonObject.put("LeadOwnerID", mUsersID.toString())
        jsonObject.put("CompanyName", edtCompanyName.text.toString().trim())
        jsonObject.put("Title", edtTitle.text.toString().trim())
        jsonObject.put("PhoneNo", edtPhoneNo.text.toString().trim())
        jsonObject.put("Website", edtWebsite.text.toString().trim())
        jsonObject.put("LeadSourceID", mLeadsourceID)
        jsonObject.put("RefName", edtReferenceName.text.toString().trim())
        jsonObject.put("RefMobileNo", edtReferenceMobileNo.text.toString().trim())
        jsonObject.put("Industry", edtIndustry.text.toString().trim())
        jsonObject.put("NoofEmp", edtNoOfEmp.text.toString().trim())
        jsonObject.put("AnnualRevenue", edtAnnualRevenue.text.toString().trim())
        jsonObject.put("Address", edtAddress.text.toString().trim())
        jsonObject.put("CityID", mCityID)
        jsonObject.put("StateID", mStateID)
        jsonObject.put("CountryID", mCountryID)
        jsonObject.put("PinCode", edtZipcode.text.toString().trim())
        jsonObject.put("Notes", edtNotes.text.toString().trim())
        if (birDate != "") {
            jsonObject.put("BirthDate", birDate)
        }
        if (mrgDate != "") {
            jsonObject.put("MarriageDate", mrgDate)
        }
        jsonObject.put("IsActive", true)
        jsonObject.put("FamilysDetails", jsonArrayFamilyMember)

        if (state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("LeadGUID", LeadGUID)
        }

        if (state.equals(AppConstant.S_ADD)) {
            val call = ApiUtils.apiInterface.ManageLeadsInsert(getRequestJSONBody(jsonObject.toString()))
            call.enqueue(object : Callback<RefGUIDResponse> {
                override fun onResponse(call: Call<RefGUIDResponse>, response: Response<RefGUIDResponse>) {
                    hideProgress()
                    if (response.code() == 200) {
                        if (response.body()?.Status == 201) {
                            Snackbar.make(
                                layout,
                                response.body()?.Details.toString(),
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                            val intent = Intent()
                            setResult(RESULT_OK, intent)
                            finish()
                        } else {
                            Snackbar.make(
                                layout,
                                response.body()?.Details.toString(),
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                            val intent = Intent()
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<RefGUIDResponse>, t: Throwable) {
                    hideProgress()
                    Snackbar.make(
                        layout,
                        getString(R.string.error_failed_to_connect),
                        Snackbar.LENGTH_LONG
                    ).show()
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                }
            })
        } else {
            val call = ApiUtils.apiInterface.ManageLeadsUpdate(getRequestJSONBody(jsonObject.toString()))
            call.enqueue(object : Callback<CommonResponse> {
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    hideProgress()
                    if (response.code() == 200) {
                        if (response.body()?.Status == 201) {
                            Snackbar.make(
                                layout,
                                response.body()?.Details.toString(),
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                            val intent = Intent()
                            setResult(RESULT_OK, intent)
                            finish()
                        } else {
                            Snackbar.make(
                                layout,
                                response.body()?.Details.toString(),
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                            val intent = Intent()
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    hideProgress()
                    Snackbar.make(
                        layout,
                        getString(R.string.error_failed_to_connect),
                        Snackbar.LENGTH_LONG
                    ).show()
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                }
            })
        }
    }

    private fun callManageLeadGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("LeadGUID", LeadGUID)

        val call = ApiUtils.apiInterface.ManageLeadsFindByID(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<LeadByGUIDResponse> {
            override fun onResponse(call: Call<LeadByGUIDResponse>, response: Response<LeadByGUIDResponse>) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListLead = response.body()?.Data!!
                        setAPIData(arrayListLead)
                        hideProgress()
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<LeadByGUIDResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
        })
    }

    private fun setAPIData(model: LeadModel) {

        if (model.LeadStage != null && model.LeadStage != 0) {
            if (model.LeadStage == 1) {
                rbForExisting.isChecked = true
                rating_bar.progressTintList = ColorStateList.valueOf(getColor(R.color.gold))
            } else if (model.LeadStage == 2) {
                rbForProspect.isChecked = true
                rating_bar.progressTintList = ColorStateList.valueOf(getColor(R.color.silver))
            }
            callManageUsers(0)
        }
        if (model.InitialID != null && model.InitialID != 0) {
            mInitialID = model.InitialID
            callManageInitial(0, "Activity")
        }
        if (model.CategoryID != null && model.CategoryID != 0) {
            rating_bar.rating = model.CategoryID.toFloat()
        }
        if (model.OccupationID != null && model.OccupationID != 0) {
            mOccupationID = model.OccupationID
            callManageOccupation(0)
        }
        if (!model.FirstName.isNullOrEmpty()) {
            edtFirstName.setText(model.FirstName)
        }
        if (!model.LastName.isNullOrEmpty()) {
            edtLastName.setText(model.LastName)
        }
        if (!model.EmailID.isNullOrEmpty()) {
            edtEmailAddress.setText(model.EmailID)
        }
        if (!model.MobileNo.isNullOrEmpty()) {
            edtMobileNo.setText(model.MobileNo)
        }
        if (!model.GroupCode.isNullOrEmpty()) {
            edtGroupCode.setText(model.GroupCode)
        }
        if (model.LeadOwnerID != null && model.LeadOwnerID != 0) {
            mUsersID = model.LeadOwnerID.toInt()
        }
        if (model.CompanyName != null && model.CompanyName != "") {
            edtCompanyName.setText(model.CompanyName)
        }
        if (model.Title != null && model.Title != "") {
            edtTitle.setText(model.Title)
        }
        if (model.PhoneNo != null && model.PhoneNo != "") {
            edtPhoneNo.setText(model.PhoneNo)
        }
        if (model.Website != null && model.Website != "") {
            edtWebsite.setText(model.Website)
        }
        if (model.LeadSourceID != null && model.LeadSourceID != 0) {
            mLeadsourceID = model.LeadSourceID
            callManageLeadSource(0)
        }
        if (model.RefName != null && model.RefName != "") {
            edtReferenceName.setText(model.RefName)
        }
        if (model.RefMobileNo != null && model.RefMobileNo != "") {
            edtReferenceMobileNo.setText(model.RefMobileNo)
        }
        if (model.Industry != null && model.Industry != "") {
            edtIndustry.setText(model.Industry)
        }
        if (model.NoofEmp != null && model.NoofEmp != "") {
            edtNoOfEmp.setText(model.NoofEmp)
        }
        if (model.AnnualRevenue != null && model.AnnualRevenue != "") {
            edtAnnualRevenue.setText(model.AnnualRevenue)
        }
        if (model.Address != null && model.Address != "") {
            edtAddress.setText(model.Address)
        }
        if (model.PinCode != null && model.PinCode != "") {
            edtZipcode.setText(model.PinCode)
        }
        if (model.Notes != null && model.Notes != "") {
            edtNotes.setText(model.Notes)
        }
        if (!model.BirthDate.isNullOrEmpty()) {
            edtDateOfBirth.setText(model.BirthDate)
        }
        if (!model.MarriageDate.isNullOrEmpty()) {
            edtMarriage.setText(model.MarriageDate)
        }
        if (model.CityID != null && model.CityID != 0) {
            mCityID = model.CityID
            callManageCity(0)
        }

        if(!model.FamilyDetails!!.isNullOrEmpty()) {
            arrayListFamilyInfo = ArrayList()

            for(i in 0 until model.FamilyDetails.size) {

                val mDate = convertDateStringToString(model.FamilyDetails[i].BirthDate!! , AppConstant.DATE_INPUT_FORMAT,AppConstant.DEFAULT_DATE_FORMAT)

                arrayListFamilyInfo?.add(
                    FamilyMemberInfoModel(
                        ID = model.FamilyDetails[i].ID!!,
                        RelationId = model.FamilyDetails[i].RelationID!!,
                        Relation = model.FamilyDetails[i].RelationName!!,
                        InitialID = model.FamilyDetails[i].InitialID!!,
                        Initial = model.FamilyDetails[i].InitialName.toString(),
                        FirstName = model.FamilyDetails[i].FirstName!!,
                        LastName = model.FamilyDetails[i].LastName!!,
                        MobileNo = model.FamilyDetails[i].MobileNo!!,
                        DateOfBirth = model.FamilyDetails[i].BirthDate!!,
                        mDateOfBirth = mDate!!
                    )
                )
            }
            setAdapterData(arrayListFamilyInfo)
        }
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int, name: String) {
        hideKeyboard(applicationContext, view)
        when (view.id) {

            R.id.edtRelation -> {
                preventTwoClick(view)
                mRelationItemPostion = position
                if (isOnline(this)) {
                    if (arrayListRelation.isNullOrEmpty()) {
                        callRelation(1)
                    } else {
                        selectRelationDialog()
                    }
                } else {
                    internetErrordialog(this@AddLeadActivity)
                }
            }

            R.id.edtInitialItem -> {
                preventTwoClick(view)
                mInitialItemPostion = position
                if (isOnline(this)) {
                    if (arrayListInitial.isNullOrEmpty()) {
                        callManageInitial(1, "Adapter")
                    } else {
                        selectInitialDialog(1)
                    }
                } else {
                    internetErrordialog(this@AddLeadActivity)
                }
            }

            R.id.edtInquiryDate -> {
                preventTwoClick(view)
            }

            R.id.edtDOB -> {
                preventTwoClick(view)
                mDOBItemPostion = position
                showDatePickerDialog()
            }

            R.id.imgDelete -> {
                if (::adapter.isInitialized) {
                    AwesomeDialog.build(this).title("Warning !!!")
                        .body("Are you sure want to delete this Family Member?")
                        .icon(R.drawable.ic_delete).position(AwesomeDialog.POSITIONS.CENTER)
                        .onNegative("No") {

                        }.onPositive("Yes") {
                            if (adapter.arrayList!!.get(position).ID == 0) {
                                adapter.remove(position)
                            } else{
                                callDeleteFamilyMember(adapter.arrayList!!.get(position).ID, position)
                            }
                        }
                }
            }

        }
    }

    private fun callManageLeadCount() {

        val call = ApiUtils.apiInterface.ManageLeadCount()
        call.enqueue(object : Callback<LeadCountResponse> {
            override fun onResponse(call: Call<LeadCountResponse>, response: Response<LeadCountResponse>) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListLead = response.body()?.Data!!
                        if(arrayListLead.LeadCount != 0) {
                            txtClientNo.setText("Client No : "+ arrayListLead.LeadCount)
                            txtClientNo.visible()
                        }
                    } else {
                        Snackbar.make(
                            layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<LeadCountResponse>, t: Throwable) {
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

}