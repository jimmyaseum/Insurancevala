package com.app.insurancevala.model.response

import com.app.insurancevala.model.pojo.AttachmentModel
import com.app.insurancevala.model.pojo.DocumentsModel
import com.google.gson.annotations.SerializedName

class NotesResponse {
    @SerializedName("Data")
    var Data: ArrayList<NotesModel>? = null
    @SerializedName("Details")
    var Details: String? = null
    @SerializedName("Message")
    var Message: String? = null
    @SerializedName("Status")
    var Status: Int? = null
    @SerializedName("ItemCount")
    var ItemCount: Int? = null
    @SerializedName("ErrorMessage")
    var ErrorMessage: String? = null
}

data class NotesModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("NoteGUID")
    val NoteGUID: String? = null,
    @SerializedName("Title")
    val Title: String? = null,
    @SerializedName("Description")
    val Description: String? = null,
    @SerializedName("LeadID")
    val LeadID: Int? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("CreatedBy")
    val CreatedBy: Int? = null,
    @SerializedName("CreatedByName")
    val CreatedByName: String? = null,
    @SerializedName("CreatedOn")
    val CreatedOn: String? = null,
    @SerializedName("UpdatedBy")
    val UpdatedBy: Int? = null,
    @SerializedName("UpdatedByName")
    val UpdatedByName: String? = null,
    @SerializedName("UpdatedOn")
    val UpdatedOn: String? = null,
    @SerializedName("ReferenceGUID")
    val ReferenceGUID: String? = null,
    @SerializedName("NoteAttachmentList")
    val NoteAttachmentList: ArrayList<DocumentsModel>? = null,

    )
