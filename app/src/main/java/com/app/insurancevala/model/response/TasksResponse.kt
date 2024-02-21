package com.app.insurancevala.model.response

import com.app.insurancevala.model.pojo.DocumentsModel
import com.google.gson.annotations.SerializedName

class TasksResponse {
    @SerializedName("Data")
    var Data: ArrayList<TasksModel>? = null
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

data class TasksModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("TaskGUID")
    val TaskGUID: String? = null,
    @SerializedName("Subject")
    val Subject: String? = null,
    @SerializedName("Description")
    val Description: String? = null,
    @SerializedName("LeadID")
    val LeadID: Int? = null,
    @SerializedName("TaskOwnerID")
    val TaskOwnerID: Int? = null,
    @SerializedName("DueDate")
    val DueDate: String? = null,
    @SerializedName("TaskStatus")
    val TaskStatus: String? = null,
    @SerializedName("Priority")
    val Priority: String? = null,
    @SerializedName("IsReminder")
    val IsReminder: Boolean? = null,
    @SerializedName("ReminderDate")
    val ReminderDate: String? = null,
    @SerializedName("Repeat")
    val Repeat: Int? = null,
    @SerializedName("NotifyVia")
    val NotifyVia: Int? = null,
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
    @SerializedName("TaskAttachmentList")
    val TaskAttachmentList: ArrayList<DocumentsModel>? = null,

)