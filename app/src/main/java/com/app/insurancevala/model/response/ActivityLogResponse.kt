package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class ActivityLogResponse {
    @SerializedName("Data")
    var Data: ArrayList<ActivityLogModel>? = null

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

data class ActivityLogModel(
    @SerializedName("LeadID")
    var LeadID: Int? = null,
    @SerializedName("NBInquiryTypeID")
    var NBInquiryTypeID: Int? = null,
    @SerializedName("ActivityLog")
    var ActivityLog: String? = null,
    @SerializedName("LeadName")
    var LeadName: String? = null,
    @SerializedName("ActivityType")
    var ActivityType: String? = null,
    @SerializedName("ActivityStutas")
    var ActivityStutas: String? = null,
    @SerializedName("ActivityDate")
    var ActivityDate: String? = null,
    @SerializedName("ActivityTitle")
    var ActivityTitle: String? = null,
    @SerializedName("LogGUID")
    var LogGUID: String? = null,
    @SerializedName("ActivityPurpose")
    var ActivityPurpose: String? = null,
    @SerializedName("ActivityResult")
    var ActivityResult: String? = null,
    @SerializedName("ActivityAgenda")
    var ActivityAgenda: String? = null,
    @SerializedName("ActivityStartTime")
    var ActivityStartTime: String? = null,
    @SerializedName("ActivityEndTime")
    var ActivityEndTime: String? = null,
    @SerializedName("ActivityDescription")
    var ActivityDescription: String? = null,
    @SerializedName("ActivityLocation")
    var ActivityLocation: String? = null,
    @SerializedName("ActivityOutCome")
    var ActivityOutCome: String? = null,
    @SerializedName("ActivityAttendee")
    var ActivityAttendee: String? = null,
    @SerializedName("Activitypriority")
    var Activitypriority: String? = null,
    @SerializedName("ActivityOwnName")
    var ActivityOwnName: String? = null,
    @SerializedName("IsFollowup")
    var IsFollowup: String? = null,
    @SerializedName("FollowupDate")
    var FollowupDate: String? = null,
    @SerializedName("FollowupTime")
    var FollowupTime: String? = null,
    @SerializedName("FollowupNote")
    var FollowupNote: String? = null,
    @SerializedName("CreatedOn")
    var CreatedOn: String? = null,
    @SerializedName("CreatedBy")
    var CreatedBy: String? = null,
    @SerializedName("Details")
    var Details: String? = null,
    @SerializedName("Message")
    var Message: String? = null,
    @SerializedName("Status")
    var Status: String? = null,
    @SerializedName("ErrorMessage")
    var ErrorMessage: String? = null
)