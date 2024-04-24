package com.app.insurancevala.model.response

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class RecordingsResponse (
    @SerializedName("Data")
    val Data: ArrayList<RecordingsModel>? = null,
    @SerializedName("Details")
    val Details: String? = null,
    @SerializedName("Message")
    val Message: String? = null,
    @SerializedName("Status")
    val Status: Int? = null,
    @SerializedName("ItemCount")
    val ItemCount: Int? = null,
    @SerializedName("ErrorMessage")
    val ErrorMessage: String? = null
)

data class RecordingsModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("NBInquiryTypeID")
    val NBInquiryTypeID: Int? = null,
    @SerializedName("RecodingFiles")
    val RecodingFiles: String? = null,
    @SerializedName("Title")
    val Title: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("CreatedOn")
    val CreatedOn: String? = null,
    @SerializedName("UpdatedOn")
    val UpdatedOn: String? = null,


    var isPlaying: Boolean = false,
)
