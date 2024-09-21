package com.app.insurancevala.activity.Lead

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.app.insurancevala.R
import com.app.insurancevala.activity.BaseActivity
import com.app.insurancevala.adapter.AllRecordingsListAdapter
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.response.RecordingsModel
import com.app.insurancevala.model.response.RecordingsResponse
import com.app.insurancevala.retrofit.ApiUtils
import com.app.insurancevala.utils.LogUtil
import com.app.insurancevala.utils.TAG
import com.app.insurancevala.utils.errortint
import com.app.insurancevala.utils.getRequestJSONBody
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.hideKeyboard
import com.app.insurancevala.utils.preventTwoClick
import com.app.insurancevala.utils.visible
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.icon
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.position
import com.example.awesomedialog.title
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_recording_list.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder

class RecordingsListActivity : BaseActivity(), View.OnClickListener, RecyclerClickListener {

    private lateinit var adapter: AllRecordingsListAdapter
    private var arrayListRecording: ArrayList<RecordingsModel> = ArrayList()
    private var ID: Int? = null
    private var RecordingID: Int? = null
    private var state: String? = null
    var Lead: Boolean? = false

    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingPosition: Int = -1

    private val REQUEST_PICK_AUDIO = 123

    private var audioPositin = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording_list)
        getIntentData()
        initializeView()
    }

    private fun getIntentData() {
        if (intent.hasExtra("Lead")) {
            Lead = intent.getBooleanExtra("Lead", false)
        }
        ID = intent.getIntExtra("ID", 0)
    }

    override fun initializeView() {
        callManageRecording()

        imgBack.setOnClickListener(this)
        imgAddRecording.setOnClickListener(this)

        arrayListRecording = ArrayList()
        adapter = AllRecordingsListAdapter(this, arrayListRecording, this)
        RvRecordingsList.adapter = adapter

        prepareMediaPlayer()

        refreshLayout.setOnRefreshListener {
            hideKeyboard(this@RecordingsListActivity, refreshLayout)
            adapter.stopPlayback()
            callManageRecording()
            refreshLayout.isRefreshing = false
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> {
                finish()
            }

            R.id.imgAddRecording -> {
                state = "Add"
                showRecordingBottomSheetDialog()
            }
        }
    }

    override fun onItemClickEvent(view: View, position: Int, type: Int) {
        when (type) {
            100 -> { // Play Audio
//                adapter.updatePlayback(position)
                if (!arrayListRecording!![position].RecodingFiles.isNullOrEmpty()){
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(arrayListRecording!![position].RecodingFiles!!))
                    startActivity(browserIntent)
                }
            }

            101 -> { // Update Recording
                RecordingID = arrayListRecording[position].ID
                state = "Update"
                showRecordingBottomSheetDialog()
            }

            102 -> { // Delete Recording
                showDeleteConfirmationDialog(arrayListRecording[position].ID!!)
            }
        }
    }

    private fun showDeleteConfirmationDialog(recordingID: Int) {
        AwesomeDialog.build(this)
            .title("Warning !!!")
            .body("Are you sure you want to delete this file?")
            .icon(R.drawable.ic_delete)
            .position(AwesomeDialog.POSITIONS.CENTER)
            .onPositive("Yes") {
                callRecordingDeleteAPI(recordingID)
            }
            .onNegative("No") { }
            .show()
    }

    private fun callManageRecording() {
        showProgress()

        val jsonObject = JSONObject()
        if (Lead!!) {
            jsonObject.put("NBLeadTypeID", ID)
            jsonObject.put("NBInquiryTypeID", null)
        } else {
            jsonObject.put("NBLeadTypeID", null)
            jsonObject.put("NBInquiryTypeID", ID)
        }

        val call =
            ApiUtils.apiInterface.ManageRecordingFindAll(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<RecordingsResponse> {
            override fun onResponse(
                call: Call<RecordingsResponse>,
                response: Response<RecordingsResponse>
            ) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {

                        arrayListRecording.clear()
                        arrayListRecording.addAll(response.body()?.Data ?: emptyList())

                        if (arrayListRecording.isNotEmpty()) {
                            adapter.notifyDataSetChanged()
                            shimmer.stopShimmer()
                            shimmer.gone()
                            FL.visible()
                            RLNoData.gone()
                        } else {
                            Snackbar.make(
                                layout,
                                response.body()?.Details.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                            shimmer.stopShimmer()
                            shimmer.gone()
                            FL.gone()
                            RLNoData.visible()
                        }
                    } else {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        shimmer.stopShimmer()
                        shimmer.gone()
                        FL.gone()
                        RLNoData.visible()
                    }
                }
            }

            override fun onFailure(call: Call<RecordingsResponse>, t: Throwable) {
                hideProgress()
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun prepareMediaPlayer() {
        adapter.prepareMediaPlayer()
    }

    private fun callRecordingDeleteAPI(ID: Int) {
        showProgress()
        val jsonObject = JSONObject()
        jsonObject.put("ID", ID)

        val call =
            ApiUtils.apiInterface.ManageRecordingDelete(getRequestJSONBody(jsonObject.toString()))
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        adapter.stopPlayback()
                        callManageRecording()
                    } else {
                        hideProgress()
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

    private fun showRecordingBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_audio_select, null)
        bottomSheetDialog.setContentView(view)

        val selectAudio = view.findViewById<LinearLayout>(R.id.Select_Audio)

        selectAudio.setOnClickListener {
            showAudioPicker()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun showAudioPicker() {
        val audioPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
        audioPickerIntent.type = "audio/*"
        startActivityForResult(audioPickerIntent, REQUEST_PICK_AUDIO)
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_PICK_AUDIO -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val audioUri = data.data
                    if (audioUri != null) {
                        showBottomSheetDialogRename(audioUri)
                    } else {
                        // Handle error, audioUri is null
                        Toast.makeText(this, "Failed to get audio URI", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showBottomSheetDialogRename(fileUri: Uri) {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_rename_dialog)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)

        val img = bottomSheetDialog.findViewById<CircleImageView>(R.id.img)
        val edtName = bottomSheetDialog.findViewById<EditText>(R.id.edtName)
        val txtButtonCancel = bottomSheetDialog.findViewById<TextView>(R.id.txtButtonCancel)
        val txtButtonSubmit = bottomSheetDialog.findViewById<TextView>(R.id.txtButtonSubmit)

        img?.setImageResource(R.drawable.ic_profile)
        edtName?.requestFocus()

        val fileName = getFileNameFromUri(fileUri)
        edtName?.setText(fileName)

        txtButtonSubmit?.setOnClickListener {
            if (!edtName?.text.toString().trim().isEmpty()) {
                bottomSheetDialog.dismiss()
                callRecordingInsertUpdate(fileUri, edtName?.text.toString())
            } else {
                edtName?.setError("Enter Name", errortint(this))
            }
        }

        txtButtonCancel?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun callRecordingInsertUpdate(audioUri: Uri, audioName: String) {
        showProgress()

        val partsList: ArrayList<MultipartBody.Part> = ArrayList()

        val inputStream = contentResolver.openInputStream(audioUri)
        val audioRequestBody = inputStream?.let {
            RequestBody.create(MediaType.parse("audio/*"), it.readBytes())
        }

        audioRequestBody?.let {
            // URL encode the audioName
            val encodedAudioName = URLEncoder.encode(audioName, "UTF-8")

            partsList.add(MultipartBody.Part.createFormData("RecodingFiles", encodedAudioName, it))
        }

        val call = if (state.equals("Add")) {
            if (Lead!!) {
                ApiUtils.apiInterface.ManageRecordingInsert(null, ID, audioName, partsList)
            } else {
                ApiUtils.apiInterface.ManageRecordingInsert(ID, null, audioName, partsList)
            }
        } else {
            if (Lead!!) {
                ApiUtils.apiInterface.ManageRecordingUpdate(RecordingID, null, ID, audioName, partsList)
            } else {
                ApiUtils.apiInterface.ManageRecordingUpdate(RecordingID, ID, null, audioName, partsList)
            }
        }

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.Status == 200) {
                        Snackbar.make(
                            layout,
                            response.body()?.Details.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        adapter.stopPlayback()
                        callManageRecording()
                    } else {
                        hideProgress()
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
                LogUtil.d(TAG, "" + t.toString())
                Snackbar.make(
                    layout,
                    getString(R.string.error_failed_to_connect),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }


    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = ""
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = it.getString(displayNameIndex)
                } else {
                    // If DISPLAY_NAME column does not exist, try getting file path segments
                    val segments = uri.pathSegments
                    if (segments.isNotEmpty()) {
                        fileName = segments.last()
                    }
                }
            }
        }
        return fileName
    }

    private fun stopAudio() {
        mediaPlayer?.apply {
            if (mediaPlayer!!.isPlaying) {
                pause()
            }
            release()
        }
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopPlayback()
    }
}
