package com.app.insurancevala.activity.Lead

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.bottomsheetadapter.*
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.*
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_lead.*
import kotlinx.android.synthetic.main.activity_add_lead.imgBack
import kotlinx.android.synthetic.main.activity_add_lead.layout
import kotlinx.android.synthetic.main.activity_add_lead.txtSave
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLeadActivity : BaseActivity(), View.OnClickListener {

    var isShow: Int = 0
    var sharedPreference: SharedPreference? = null
    var state: String? = null
    var LeadID: Int? = null
    var LeadGUID: String? = null

    var arrayListinitial: ArrayList<InitialModel>? = ArrayList()
    var mInitial: String = ""
    var mInitialID: Int = 0

    var arrayListUsers: ArrayList<UserModel>? = ArrayList()
    var mUsers: String = ""
    var mUsersID: Int = 0

    var arrayListleadsource: ArrayList<LeadSourceModel>? = ArrayList()
    var mLeadsource: String = ""
    var mLeadsourceID: Int = 0

    var arrayListCity: ArrayList<CitiesModel>? = ArrayList()
    var mCity: String = ""
    var mCityID: Int = 0
    var mState: String = ""
    var mStateID: Int = 0
    var mCountry: String = ""
    var mCountryID: Int = 0

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
        if(state.equals(AppConstant.S_ADD)) {

            txtHearderText.text = "Create Client"
            if (isOnline(this)) {
                callManageInitial(0)
                callManageUsers(0)
                callManageLeadSource(0)
                callManageCity(0)
            } else {
                internetErrordialog(this@AddLeadActivity)
            }
        } else if(state.equals(AppConstant.S_EDIT)) {
            txtHearderText.text = "Update Client"
            if (isOnline(this)) {
                LeadID = intent.getIntExtra("LeadID",0)
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
        txtSave.setOnClickListener(this)

        edtInitial.setOnClickListener(this)
        edtLeadOwner.setOnClickListener(this)
        edtLeadSource.setOnClickListener(this)
        edtCity.setOnClickListener(this)
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
            R.id.edtInitial -> {
                preventTwoClick(v)
                if (arrayListinitial.isNullOrEmpty()) {
                    callManageInitial(1)
                } else {
                    selectInitialDialog()
                }
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

    private fun callManageInitial(mode: Int) {
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
                        arrayListinitial = response.body()?.Data!!

                        if(arrayListinitial!!.size > 0) {
                            for(i in 0 until arrayListinitial!!.size) {
                                if(state.equals(AppConstant.S_EDIT)) {
                                    if(arrayListinitial!![i].ID == mInitialID) {
                                        arrayListinitial!![i].IsSelected = true
                                        mInitialID = arrayListinitial!![i].ID!!
                                        mInitial = arrayListinitial!![i].Initial!!
                                        edtInitial.setText(mInitial)
                                        edtInitial.setError(null)
                                    }
                                }
                            }
                        }

                        if (mode == 1) {
                            selectInitialDialog()
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
    private fun selectInitialDialog() {
        var dialogSelectInitial = Dialog(this)
        dialogSelectInitial.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_select, null)
        dialogSelectInitial.setContentView(dialogView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSelectInitial.window!!.attributes)

        dialogSelectInitial.window!!.attributes = lp
        dialogSelectInitial.setCancelable(true)
        dialogSelectInitial.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSelectInitial.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectInitial.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectInitial.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectInitial.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectInitial.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectInitial.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectInitial.dismiss()
        }

        txtid.text = "Select Initial"

        val itemAdapter = BottomSheetInitialListAdapter(this, arrayListinitial!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mInitialID = arrayListinitial!![pos].ID!!
                mInitial = arrayListinitial!![pos].Initial!!
                edtInitial.setText(mInitial)
                edtInitial.setError(null)
                dialogSelectInitial!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if(arrayListinitial!!.size > 6) {
            edtSearchCustomer.visible()
        }
        else {
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
                    for (model in arrayListinitial!!) {
                        if (model.Initial!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetInitialListAdapter(this@AddLeadActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mInitialID = arrItemsFinal1!![pos].ID!!
                            mInitial = arrItemsFinal1!![pos].Initial!!
                            edtInitial.setText(mInitial)
                            edtInitial.setError(null)
                            dialogSelectInitial!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetInitialListAdapter(this@AddLeadActivity, arrayListinitial!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mInitialID = arrayListinitial!![pos].ID!!
                            mInitial = arrayListinitial!![pos].Initial!!
                            edtInitial.setText(mInitial)
                            edtInitial.setError(null)
                            dialogSelectInitial!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                }
            }
        })
        dialogSelectInitial!!.show()
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

                        if(arrayListUsers!!.size > 0) {
                            for(i in 0 until arrayListUsers!!.size) {
                                if(state.equals(AppConstant.S_ADD)) {
                                    if (arrayListUsers!![i].ID == Userid.toInt()) {
                                        arrayListUsers!![i].IsSelected = true
                                        mUsersID = arrayListUsers!![i].ID!!
                                        mUsers = arrayListUsers!![i].FirstName!! + " " + arrayListUsers!![i].LastName!!
                                        edtLeadOwner.setText(mUsers)
                                        edtLeadOwner.setError(null)
                                    }
                                }
                                else if(state.equals(AppConstant.S_EDIT)) {
                                    if(arrayListUsers!![i].ID == mUsersID) {
                                        arrayListUsers!![i].IsSelected = true
                                        mUsersID = arrayListUsers!![i].ID!!
                                        mUsers = arrayListUsers!![i].FirstName!! + " "+ arrayListUsers!![i].LastName!!
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

        dialogSelectUsers.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectUsers.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectUsers.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectUsers.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectUsers.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectUsers.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectUsers.dismiss()
        }

        txtid.text = "Select Lead Owner"

        val itemAdapter = BottomSheetUsersListAdapter(this, arrayListUsers!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mUsersID = arrayListUsers!![pos].ID!!
                mUsers = arrayListUsers!![pos].FirstName!! + " "+ arrayListUsers!![pos].LastName!!
                edtLeadOwner.setText(mUsers)
                edtLeadOwner.setError(null)
                dialogSelectUsers!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if(arrayListUsers!!.size > 6) {
            edtSearchCustomer.visible()
        }
        else {
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
                            model.LastName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetUsersListAdapter(this@AddLeadActivity, arrItemsFinal1)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mUsersID = arrItemsFinal1!![pos].ID!!
                            mUsers = arrItemsFinal1!![pos].FirstName!! + " "+ arrItemsFinal1!![pos].LastName!!
                            edtLeadOwner.setText(mUsers)
                            edtLeadOwner.setError(null)
                            dialogSelectUsers!!.dismiss()
                        }
                    })
                    rvDialogCustomer.adapter = itemAdapter
                } else {
                    val itemAdapter = BottomSheetUsersListAdapter(this@AddLeadActivity, arrayListUsers!!)
                    itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
                        override fun onItemClickEvent(v: View, pos: Int, flag: Int) {

                            itemAdapter.updateItem(pos)
                            mUsersID = arrayListUsers!![pos].ID!!
                            mUsers = arrayListUsers!![pos].FirstName!! + " "+ arrayListUsers!![pos].LastName!!
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

                        if(arrayListleadsource!!.size > 0) {
                            for(i in 0 until arrayListleadsource!!.size) {
                                if(state.equals(AppConstant.S_EDIT)) {
                                    if(arrayListleadsource!![i].ID == mLeadsourceID) {
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

        dialogSelectLeadSource.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectLeadSource.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectLeadSource.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectLeadSource.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectLeadSource.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectLeadSource.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectLeadSource.dismiss()
        }

        txtid.text = "Select Lead Source"

        val itemAdapter = BottomSheetLeadSourceListAdapter(this, arrayListleadsource!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {

                itemAdapter.updateItem(pos)
                mLeadsource = arrayListleadsource!![pos].LeadSource!!
                mLeadsourceID = arrayListleadsource!![pos].ID!!
                edtLeadSource.setText(mLeadsource)
                edtLeadSource.setError(null)
                dialogSelectLeadSource!!.dismiss()
            }
        })

        rvDialogCustomer.adapter = itemAdapter

        if(arrayListleadsource!!.size > 6) {
            edtSearchCustomer.visible()
        }
        else {
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

                    val itemAdapter = BottomSheetLeadSourceListAdapter(this@AddLeadActivity, arrItemsFinal1)
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
                    val itemAdapter = BottomSheetLeadSourceListAdapter(this@AddLeadActivity, arrayListleadsource!!)
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
        val call = ApiUtils.apiInterface.ManageCities(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CitiesResponse> {
            override fun onResponse(
                call: Call<CitiesResponse>,
                response: Response<CitiesResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        arrayListCity = response.body()?.Data!!

                        if(arrayListCity!!.size > 0) {
                            for(i in 0 until arrayListCity!!.size) {
                                if(state.equals(AppConstant.S_EDIT)) {
                                    if(arrayListCity!![i].ID == mCityID) {
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

            override fun onFailure(call: Call<CitiesResponse>, t: Throwable) {
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

        dialogSelectCity.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogSelectCity.window!!.setGravity(Gravity.CENTER)

        val rvDialogCustomer = dialogSelectCity.findViewById(R.id.rvDialogCustomer) as RecyclerView
        val edtSearchCustomer = dialogSelectCity.findViewById(R.id.edtSearchCustomer) as EditText
        val txtid =  dialogSelectCity.findViewById(R.id.txtid) as TextView
        val imgClear = dialogSelectCity.findViewById(R.id.imgClear) as ImageView

        imgClear.setOnClickListener {
            dialogSelectCity.dismiss()
        }

        txtid.text = "Select City"

        val itemAdapter = BottomSheetCitiesListAdapter(this, arrayListCity!!)
        itemAdapter.setRecyclerRowClick(object : RecyclerClickListener {
            override fun onItemClickEvent(v:View, pos: Int, flag: Int) {

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

        if(arrayListCity!!.size > 6) {
            edtSearchCustomer.visible()
        }
        else {
            edtSearchCustomer.gone()
        }

        edtSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val arrItemsFinal1: ArrayList<CitiesModel> = ArrayList()
                if (char.toString().trim().isNotEmpty()) {
                    val strSearch = char.toString()
                    for (model in arrayListCity!!) {
                        if (model.CityName!!.toLowerCase().contains(strSearch.toLowerCase())) {
                            arrItemsFinal1.add(model)
                        }
                    }

                    val itemAdapter = BottomSheetCitiesListAdapter(this@AddLeadActivity, arrItemsFinal1)
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
                    val itemAdapter = BottomSheetCitiesListAdapter(this@AddLeadActivity, arrayListCity!!)
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
            edtInitial.setError("Select Initial",errortint(this))
            isValidate = false
        }
        if (edtFirstName.text.isEmpty()) {
            edtFirstName.setError("Enter FirstName",errortint(this))
            isValidate = false
        }
        if (edtLastName.text.isEmpty()) {
            edtLastName.setError("Enter LastName",errortint(this))
            isValidate = false
        }
        if (edtMobileNo.text.isEmpty()) {
            edtMobileNo.setError("Enter MobileNo",errortint(this))
            isValidate = false
        }
        if (edtMobileNo.text.toString().trim().length < 10) {
            edtMobileNo.setError(getString(R.string.error_valid_mobile_number), errortint(this))
            isValidate =  false
        }
        if (edtEmailAddress.text.isEmpty()) {
            edtEmailAddress.setError("Enter Email Address",errortint(this))
            isValidate = false
        }
        if (!edtEmailAddress.text.toString().trim().isValidEmail()) {
            edtEmailAddress.setError(getString(R.string.error_valid_email), errortint(this))
            isValidate =  false
        }
        if (edtGroupCode.text.isEmpty()) {
            edtGroupCode.setError("Enter GroupCode",errortint(this))
            isValidate = false
        }

        return isValidate
    }

    private fun ManageCreateLeadAPI() {

        showProgress()

        var mLeadStage = 0
        if (rbForExisting.isChecked) {
            mLeadStage = 1
        } else if (rbForProspect.isChecked) {
            mLeadStage = 2
        }

        val jsonObject = JSONObject()
        jsonObject.put("LeadStage", mLeadStage)
        jsonObject.put("InitialID", mInitialID)
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
        jsonObject.put("IsActive", true)

        if(state.equals(AppConstant.S_ADD)) {
            jsonObject.put("OperationType", AppConstant.INSERT)
        } else if(state.equals(AppConstant.S_EDIT)) {
            jsonObject.put("LeadGUID", LeadGUID)
            jsonObject.put("OperationType", AppConstant.EDIT)
        }

        val call = ApiUtils.apiInterface.ManageLead(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<LeadResponse> {
            override fun onResponse(call: Call<LeadResponse>, response: Response<LeadResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 201) {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
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
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        })
    }

    private fun callManageLeadGUID() {

        showProgress()

        var jsonObject = JSONObject()
        jsonObject.put("OperationType", AppConstant.GETBYGUID)
        jsonObject.put("LeadGUID", LeadGUID)

        val call = ApiUtils.apiInterface.ManageLead(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<LeadResponse> {
            override fun onResponse(call: Call<LeadResponse>, response: Response<LeadResponse>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        val arrayListLead = response.body()?.Data!!
                        setAPIData(arrayListLead[0])
                    } else {
                        Snackbar.make(layout, response.body()?.Details.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<LeadResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(layout, getString(R.string.error_failed_to_connect), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun setAPIData(model: LeadModel) {

        if(model.LeadStage != null && model.LeadStage != 0) {
            if (model.LeadStage == 1) {
                rbForExisting.isChecked = true
            } else if (model.LeadStage == 2) {
                rbForProspect.isChecked = true
            }
            callManageUsers(0)
        }
        if(model.InitialID != null && model.InitialID != 0) {
            mInitialID = model.InitialID
            callManageInitial(0)
        }
        if(!model.FirstName.isNullOrEmpty()) {
            edtFirstName.setText(model.FirstName)
        }
        if(!model.LastName.isNullOrEmpty()) {
            edtLastName.setText(model.LastName)
        }
        if(!model.EmailID.isNullOrEmpty()) {
            edtEmailAddress.setText(model.EmailID)
        }
        if(!model.MobileNo.isNullOrEmpty()) {
            edtMobileNo.setText(model.MobileNo)
        }
        if(!model.GroupCode.isNullOrEmpty()) {
            edtGroupCode.setText(model.GroupCode)
        }
        if(model.LeadOwnerID != null && model.LeadOwnerID != "") {
            mUsersID = model.LeadOwnerID.toInt()
        }
        if(model.CompanyName != null && model.CompanyName != "") {
            edtCompanyName.setText(model.CompanyName)
        }
        if(model.Title != null && model.Title != "") {
            edtTitle.setText(model.Title)
        }
        if(model.PhoneNo != null && model.PhoneNo != "") {
            edtPhoneNo.setText(model.PhoneNo)
        }
        if(model.Website != null && model.Website != "") {
            edtWebsite.setText(model.Website)
        }
        if(model.LeadSourceID != null && model.LeadSourceID != 0) {
            mLeadsourceID = model.LeadSourceID
            callManageLeadSource(0)
        }
        if(model.RefName != null && model.RefName != "") {
            edtReferenceName.setText(model.RefName)
        }
        if(model.RefMobileNo != null && model.RefMobileNo != "") {
            edtReferenceMobileNo.setText(model.RefMobileNo)
        }
        if(model.Industry != null && model.Industry != "") {
            edtIndustry.setText(model.Industry)
        }
        if(model.NoofEmp != null && model.NoofEmp != "") {
            edtNoOfEmp.setText(model.NoofEmp)
        }
        if(model.AnnualRevenue != null && model.AnnualRevenue != "") {
            edtAnnualRevenue.setText(model.AnnualRevenue)
        }
        if(model.Address != null && model.Address != "") {
            edtAddress.setText(model.Address)
        }
        if(model.PinCode != null && model.PinCode != "") {
            edtZipcode.setText(model.PinCode)
        }
        if(model.Notes != null && model.Notes != "") {
            edtNotes.setText(model.Notes)
        }

        if(model.CityID != null && model.CityID != 0) {
            mCityID = model.CityID
            callManageCity(0)
        }
    }
}