package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class CallsResponse {
    @SerializedName("Data")
    var Data: ArrayList<CallsModel>? = null
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

class CallsByGUIDResponse {
    @SerializedName("Data")
    var Data: CallsModel? = null
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


data class CallsModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("LeadID")
    val LeadID: Int? = null,
    @SerializedName("CallGUID")
    val CallGUID: String? = null,
    @SerializedName("CallTypeID")
    val CallTypeID: Int? = null,
    @SerializedName("CallType")
    val CallType: String? = null,
    @SerializedName("CallStatus")
    val CallStatus: String? = null,
    @SerializedName("CallPurposeID")
    val CallPurposeID: Int? = null,
    @SerializedName("CallPurpose")
    val CallPurpose: String? = null,
    @SerializedName("CallResultID")
    val CallResultID: Int? = null,
    @SerializedName("CallResult")
    val CallResult: String? = null,
    @SerializedName("CallDate")
    val CallDate: String? = null,
    @SerializedName("Subject")
    val Subject: String? = null,
    @SerializedName("IsFollowup")
    val IsFollowup: Boolean? = null,
    @SerializedName("FollowupDate")
    val FollowupDate: String? = null,
    @SerializedName("FollowupNotes")
    val FollowupNotes: String? = null,
    @SerializedName("Agenda")
    val Agenda: String? = null,
    @SerializedName("Description")
    val Description: String? = null,
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
    @SerializedName("IsSelected")
    val IsSelected: Boolean? = null,
    )
