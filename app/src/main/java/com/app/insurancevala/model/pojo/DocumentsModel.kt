package com.app.insurancevala.model.pojo

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class DocumentsResponse (
    @SerializedName("Data")
    val Data: ArrayList<DocumentsModel>? = null,
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

data class DocumentsModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("LeadID")
    val LeadID: Int? = null,
    @SerializedName("NBInquiryTypeID")
    val NBInquiryTypeID: Int? = null,
    @SerializedName("AttachmentGUID")
    val AttachmentGUID: String? = null,
    @SerializedName("AttachmentURL")
    val AttachmentURL: String? = null,
    @SerializedName("AttachmentName")
    val AttachmentName: String? = null,
    @SerializedName("ReferenceGUID")
    val ReferenceGUID: String? = null,
    @SerializedName("AttachmentType")
    val AttachmentType: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("CreatedOn")
    val CreatedOn: String? = null,
    @SerializedName("UpdatedOn")
    val UpdatedOn: String? = null,

    var name: String = "",
    var attachmentPath: String = "",
    var attachmentUri: Uri? = null,
    var attachmentType: Int = 1,//1-Photo and 2-Document
    var isUri: Boolean = true,
    var isSelected: Boolean = false
)
