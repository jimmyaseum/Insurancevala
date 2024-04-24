package com.app.insurancevala.model.response

import com.app.insurancevala.model.pojo.DocumentsModel
import com.google.gson.annotations.SerializedName

class MeetingsResponse {
    @SerializedName("Data")
    var Data: ArrayList<MeetingsModel>? = null
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

class MeetingsByGUIDResponse {
    @SerializedName("Data")
    var Data: MeetingsModel? = null
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

data class MeetingsModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("MeetingGUID")
    val MeetingGUID: String? = null,
    @SerializedName("LeadID")
    val LeadID: Int? = null,
    @SerializedName("MeetingTypeID")
    val MeetingTypeID: Int? = null,
    @SerializedName("MeetingType")
    val MeetingType: String? = null,
    @SerializedName("MeetingDate")
    val MeetingDate: String? = null,
    @SerializedName("StartTime")
    val StartTime: String? = null,
    @SerializedName("EndTime")
    val EndTime: String? = null,
    @SerializedName("Location")
    val Location: String? = null,
    @SerializedName("Purpose")
    val Purpose: String? = null,
    @SerializedName("Description")
    val Description: String? = null,
    @SerializedName("MeetingStatusID")
    val MeetingStatusID: Int? = null,
    @SerializedName("MeetingStatus")
    val MeetingStatus: String? = null,
    @SerializedName("MeetingOutcomeID")
    val MeetingOutcomeID: Int? = null,
    @SerializedName("MeetingOutcome")
    val MeetingOutcome: String? = null,
    @SerializedName("Attendee")
    val Attendee: String? = null,
    @SerializedName("IsFollowup")
    val IsFollowup: Boolean? = null,
    @SerializedName("FollowupDate")
    val FollowupDate: String? = null,
    @SerializedName("FollowupTime")
    val FollowupTime: String? = null,
    @SerializedName("FollowupNotes")
    val FollowupNotes: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("ReferenceGUID")
    val ReferenceGUID: String? = null,
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
    @SerializedName("MeetingAttachmentList")
    val MeetingAttachmentList: ArrayList<DocumentsModel>? = null,
)
