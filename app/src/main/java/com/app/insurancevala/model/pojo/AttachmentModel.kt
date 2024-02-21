package com.app.insurancevala.model.pojo

import android.net.Uri
import com.google.gson.annotations.SerializedName


data class AttachmentModel(

    @SerializedName("ID")
    var ID: Int? = null,
    @SerializedName("UID")
    var UID: Int? = null,
    @SerializedName("Attachments")
    var Attachments: String? = null,
    @SerializedName("AttachmentType")
    var AttachmentType: Int? = null,
    @SerializedName("AttachmentTypeName")
    var AttachmentTypeName: String? = null,

    var name: String = "",
    var attachmentPath: String = "",
    var attachmentUri: Uri? = null,
    var attachmentType: Int = 1,//1-Photo and 2-Document
    var isUri: Boolean = true,
    var isSelected: Boolean = false

)