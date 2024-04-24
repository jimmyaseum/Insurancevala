package com.app.insurancevala.model.response

import com.app.insurancevala.model.pojo.AttachmentModel
import com.app.insurancevala.model.pojo.DocumentsModel
import com.google.gson.annotations.SerializedName

class PlanBrochuresResponse {
    @SerializedName("Data")
    var Data: ArrayList<PlanBrochuresModel>? = null
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

class PlanBrochureByIDResponse {
    @SerializedName("Data")
    var Data: PlanBrochuresModel? = null
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

data class PlanBrochuresModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("CompanyID")
    val CompanyID: Int? = null,
    @SerializedName("CompanyName")
    val CompanyName: String? = null,
    @SerializedName("PlanID")
    val PlanID: Int? = null,
    @SerializedName("PlanName")
    val PlanName: String? = null,
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
    @SerializedName("PlanBrochureAttachmentList")
    val PlanBrochureAttachmentList: ArrayList<DocumentsModel>? = null,

)